package consulo.dotnet.impl.dll;

import com.jetbrains.util.filetype.DetectedFileInfo;
import com.jetbrains.util.filetype.FileProperties;
import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.util.io.ByteSequence;
import consulo.util.io.FileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.virtualFileSystem.fileType.FileTypeDetector;

/**
 * @author VISTALL
 * @since 2026-04-02
 */
@ExtensionImpl(id = "dotnet-native", order = "before native")
public class DotNetModuleFileTypeDetector implements FileTypeDetector {
    @Override
    public FileType detect(VirtualFile virtualFile, ByteSequence byteSequence, CharSequence charSequence) {
        if (DotNetModuleFileType.EXTENSION.equalsIgnoreCase(virtualFile.getExtension())) {
            DetectedFileInfo detected =
                com.jetbrains.util.filetype.FileTypeDetector.detectFileType(new ByteSequenceSeekableByteChannel(byteSequence));

            if (detected.fileType() == com.jetbrains.util.filetype.FileType.Pe && detected.fileProperties().contains(FileProperties.Managed)) {
                return DotNetModuleFileType.INSTANCE;
            }
        }
        return null;
    }

    @Override
    public int getDesiredContentPrefixLength() {
        return FileUtil.THREAD_LOCAL_BUFFER_LENGTH;
    }

    @Override
    public int getVersion() {
        return 3;
    }
}
