/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.dotnet.impl.assembly.summary;

import consulo.annotation.component.ServiceImpl;
import consulo.container.boot.ContainerPathManager;
import consulo.dotnet.assembly.summary.DotNetAssemblySummary;
import consulo.dotnet.assembly.summary.DotNetAssemblySummaryIndex;
import consulo.logging.Logger;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A store in two levels, like a build cache: {@code paths/} maps the path of an assembly (with its size and modification
 * time) to the hash of its content, and {@code content/} keeps one summary per content hash - shared by every copy of the
 * same assembly (an SDK moved or installed twice, the copies of Unity projects, NuGet packages used by several solutions).
 * A known path whose size and modification time did not change is answered without reading the assembly; a new or changed
 * path is hashed, and read only if its content is not stored yet. A file that is not an assembly is stored too, so it is
 * not read again.
 *
 * @author VISTALL
 */
@Singleton
@ServiceImpl
public class DotNetAssemblySummaryIndexImpl implements DotNetAssemblySummaryIndex {
    private static final Logger LOG = Logger.getInstance(DotNetAssemblySummaryIndexImpl.class);

    // change when the format or the content of a summary changes
    private static final int VERSION = 1;

    private record Stamp(long size, long modified) {
    }

    /**
     * @param contentHash the hash of the content of the file
     * @param summary     null if the file is not an assembly
     */
    private record Entry(Stamp stamp, String contentHash, @Nullable DotNetAssemblySummary summary) {
    }

    private final ConcurrentMap<Path, SoftReference<Entry>> myEntries = new ConcurrentHashMap<>();

    private final AtomicLong myModificationCount = new AtomicLong();
    // how many assemblies were read - for the tests of the store
    final AtomicLong myReadCount = new AtomicLong();

    private final Path myPathDirectory;
    private final Path myContentDirectory;

    public DotNetAssemblySummaryIndexImpl() {
        this(ContainerPathManager.get().getSystemDir().resolve("dotnet").resolve("assembly-summaries"));
    }

    DotNetAssemblySummaryIndexImpl(Path directory) {
        myPathDirectory = directory.resolve("paths");
        myContentDirectory = directory.resolve("content");
    }

    @Nullable
    @Override
    public DotNetAssemblySummary getSummary(Path file) {
        Path path = file.toAbsolutePath().normalize();
        Stamp stamp = stamp(path);
        if (stamp == null) {
            return null;
        }
        Entry entry = find(path, stamp);
        if (entry != null) {
            return entry.summary();
        }

        String contentHash = hash(path);
        if (contentHash == null) {
            return null;
        }
        ContentSummary content = readContent(contentHash);
        DotNetAssemblySummary summary;
        if (content != null) {
            summary = content.summary();
        }
        else {
            myReadCount.incrementAndGet();
            summary = DotNetAssemblySummaryReader.read(path);
            writeContent(contentHash, summary);
        }
        entry = new Entry(stamp, contentHash, summary);
        myEntries.put(path, new SoftReference<>(entry));
        writePath(path, entry);
        myModificationCount.incrementAndGet();
        return summary;
    }

    @Nullable
    @Override
    public DotNetAssemblySummary getStoredSummary(Path file) {
        Path path = file.toAbsolutePath().normalize();
        Stamp stamp = stamp(path);
        if (stamp == null) {
            return null;
        }
        Entry entry = find(path, stamp);
        return entry == null ? null : entry.summary();
    }

    @Override
    public long getModificationCount() {
        return myModificationCount.get();
    }

    @Nullable
    private Entry find(Path path, Stamp stamp) {
        SoftReference<Entry> reference = myEntries.get(path);
        Entry entry = reference == null ? null : reference.get();
        if (entry != null && entry.stamp().equals(stamp)) {
            return entry;
        }
        PathRecord record = readPath(path);
        if (record == null || !record.stamp().equals(stamp)) {
            return null;
        }
        ContentSummary content = readContent(record.contentHash());
        if (content == null) {
            return null;
        }
        entry = new Entry(stamp, record.contentHash(), content.summary());
        myEntries.put(path, new SoftReference<>(entry));
        return entry;
    }

    @Nullable
    private static Stamp stamp(Path path) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return attributes.isRegularFile() ? new Stamp(attributes.size(), attributes.lastModifiedTime().toMillis()) : null;
        }
        catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static String hash(Path path) {
        try (InputStream in = Files.newInputStream(path)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[64 * 1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        }
        catch (IOException e) {
            return null;
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hashOf(String text) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(text.getBytes(StandardCharsets.UTF_8)));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // ---- paths/: path -> stamp and content hash

    private record PathRecord(Stamp stamp, String contentHash) {
    }

    private Path pathFile(Path path) {
        return myPathDirectory.resolve(hashOf(path.toString()) + ".path");
    }

    @Nullable
    private PathRecord readPath(Path path) {
        Path file = pathFile(path);
        if (!Files.isRegularFile(file)) {
            return null;
        }
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            if (in.readInt() != VERSION || !in.readUTF().equals(path.toString())) {
                return null;
            }
            return new PathRecord(new Stamp(in.readLong(), in.readLong()), in.readUTF());
        }
        catch (IOException | RuntimeException e) {
            return null;
        }
    }

    private void writePath(Path path, Entry entry) {
        write(myPathDirectory, pathFile(path), out -> {
            out.writeInt(VERSION);
            out.writeUTF(path.toString());
            out.writeLong(entry.stamp().size());
            out.writeLong(entry.stamp().modified());
            out.writeUTF(entry.contentHash());
        });
    }

    // ---- content/: content hash -> summary

    /**
     * @param summary null if the content is not an assembly
     */
    private record ContentSummary(@Nullable DotNetAssemblySummary summary) {
    }

    private Path contentFile(String contentHash) {
        return myContentDirectory.resolve(contentHash + ".summary");
    }

    @Nullable
    private ContentSummary readContent(String contentHash) {
        Path file = contentFile(contentHash);
        if (!Files.isRegularFile(file)) {
            return null;
        }
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            if (in.readInt() != VERSION) {
                return null;
            }
            if (!in.readBoolean()) {
                return new ContentSummary(null);
            }
            String assemblyName = in.readBoolean() ? in.readUTF() : null;

            int typeCount = in.readInt();
            List<DotNetAssemblySummary.Type> types = new ArrayList<>(typeCount);
            for (int i = 0; i < typeCount; i++) {
                types.add(new DotNetAssemblySummary.Type(in.readUTF(), in.readUTF(), in.readInt(), in.readInt()));
            }
            int forwardedCount = in.readInt();
            List<DotNetAssemblySummary.ForwardedType> forwardedTypes = new ArrayList<>(forwardedCount);
            for (int i = 0; i < forwardedCount; i++) {
                forwardedTypes.add(new DotNetAssemblySummary.ForwardedType(in.readUTF(), in.readUTF(), in.readInt()));
            }
            int extensionCount = in.readInt();
            List<DotNetAssemblySummary.ExtensionMethod> extensionMethods = new ArrayList<>(extensionCount);
            for (int i = 0; i < extensionCount; i++) {
                extensionMethods.add(new DotNetAssemblySummary.ExtensionMethod(in.readUTF(), in.readUTF()));
            }
            return new ContentSummary(new DotNetAssemblySummary(assemblyName, List.copyOf(types), List.copyOf(forwardedTypes), List.copyOf(extensionMethods)));
        }
        catch (IOException | RuntimeException e) {
            // a broken or an old store file: read the assembly again
            return null;
        }
    }

    private void writeContent(String contentHash, @Nullable DotNetAssemblySummary summary) {
        write(myContentDirectory, contentFile(contentHash), out -> {
            out.writeInt(VERSION);
            out.writeBoolean(summary != null);
            if (summary == null) {
                return;
            }
            out.writeBoolean(summary.assemblyName() != null);
            if (summary.assemblyName() != null) {
                out.writeUTF(summary.assemblyName());
            }
            out.writeInt(summary.types().size());
            for (DotNetAssemblySummary.Type type : summary.types()) {
                out.writeUTF(type.name());
                out.writeUTF(type.namespace());
                out.writeInt(type.arity());
                out.writeInt(type.kind());
            }
            out.writeInt(summary.forwardedTypes().size());
            for (DotNetAssemblySummary.ForwardedType type : summary.forwardedTypes()) {
                out.writeUTF(type.name());
                out.writeUTF(type.namespace());
                out.writeInt(type.arity());
            }
            out.writeInt(summary.extensionMethods().size());
            for (DotNetAssemblySummary.ExtensionMethod method : summary.extensionMethods()) {
                out.writeUTF(method.name());
                out.writeUTF(method.namespace());
            }
        });
    }

    private interface Writer {
        void write(DataOutputStream out) throws IOException;
    }

    /**
     * Writes a temporary file and moves it: a reader sees the old or the new file, never a part of one
     */
    private static void write(Path directory, Path file, Writer writer) {
        try {
            Files.createDirectories(directory);
            Path temp = Files.createTempFile(directory, "summary", ".tmp");
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(temp)))) {
                writer.write(out);
            }
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException e) {
            LOG.warn("Cannot store " + file, e);
        }
    }
}
