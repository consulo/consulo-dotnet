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

import consulo.dotnet.assembly.summary.DotNetAssemblySummary;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The summaries of {@code assemblySummary/Sample.dll} and {@code SampleFacade.dll} (sources and build script next to them)
 *
 * @author VISTALL
 */
public class DotNetAssemblySummaryIndexTest {
    @Rule
    public TemporaryFolder myTemp = new TemporaryFolder();

    private static Path resource(String name) throws Exception {
        return Path.of(DotNetAssemblySummaryIndexTest.class.getResource("/assemblySummary/" + name).toURI());
    }

    @Test
    public void testTypes() throws Exception {
        DotNetAssemblySummary summary = DotNetAssemblySummaryReader.read(resource("Sample.dll"));
        Assert.assertNotNull(summary);
        Assert.assertEquals("Sample", summary.assemblyName());

        // public top-level types only: no Nested, no Hidden, no <Module>; Box twice, by arity
        String types = summary.types().stream()
            .map(type -> type.namespace() + "." + type.name() + "/" + type.arity() + "/" + type.kind())
            .collect(Collectors.toCollection(TreeSet::new)).toString();
        Assert.assertEquals("[.GlobalType/0/0, Sample.Api.Box/1/0, Sample.Api.Box/2/0, Sample.Api.Color/0/"
            + (DotNetAssemblySummary.ENUM | DotNetAssemblySummary.VALUE_TYPE) + ", "
            + "Sample.Api.Handler/0/" + DotNetAssemblySummary.DELEGATE + ", Sample.Api.IWidget/0/" + DotNetAssemblySummary.INTERFACE + ", "
            + "Sample.Api.MarkerAttribute/0/" + DotNetAssemblySummary.ATTRIBUTE + ", Sample.Api.Point/0/" + DotNetAssemblySummary.VALUE_TYPE + ", "
            + "Sample.Api.Shape/0/" + DotNetAssemblySummary.ABSTRACT + ", Sample.Api.Widget/0/0, Sample.Api.WidgetExtensions/0/"
            + DotNetAssemblySummary.STATIC + "]", types);

        // public extension methods only: not Helper (no this), not Hidden (internal)
        Assert.assertEquals(List.of(new DotNetAssemblySummary.ExtensionMethod("Count", "Sample.Api")), summary.extensionMethods());
        Assert.assertEquals(List.of(), summary.forwardedTypes());
    }

    @Test
    public void testForwardedTypes() throws Exception {
        DotNetAssemblySummary summary = DotNetAssemblySummaryReader.read(resource("SampleFacade.dll"));
        Assert.assertNotNull(summary);
        Assert.assertEquals("SampleFacade", summary.assemblyName());
        Assert.assertEquals(List.of(), summary.types());
        Assert.assertEquals(new TreeSet<>(List.of("Sample.Api.Box/1", "Sample.Api.Widget/0")),
            summary.forwardedTypes().stream().map(type -> type.namespace() + "." + type.name() + "/" + type.arity())
                .collect(Collectors.toCollection(TreeSet::new)));
    }

    @Test
    public void testNotAnAssembly() throws Exception {
        Path file = myTemp.newFile("NotAnAssembly.dll").toPath();
        Files.writeString(file, "not an assembly");
        Assert.assertNull(DotNetAssemblySummaryReader.read(file));
    }

    @Test
    public void testStore() throws Exception {
        Path store = myTemp.newFolder("store").toPath();
        Path assembly = myTemp.newFolder("sdk").toPath().resolve("Sample.dll");
        Files.copy(resource("Sample.dll"), assembly);

        DotNetAssemblySummaryIndexImpl index = new DotNetAssemblySummaryIndexImpl(store);
        Assert.assertNull("nothing stored yet", index.getStoredSummary(assembly));
        DotNetAssemblySummary summary = index.getSummary(assembly);
        Assert.assertNotNull(summary);

        // a new index (a restart) reads the stored summary without reading the assembly
        DotNetAssemblySummaryIndexImpl restarted = new DotNetAssemblySummaryIndexImpl(store);
        Assert.assertEquals(summary, restarted.getStoredSummary(assembly));

        // a changed assembly is not answered from the store
        Files.copy(resource("SampleFacade.dll"), assembly, StandardCopyOption.REPLACE_EXISTING);
        Files.setLastModifiedTime(assembly, FileTime.fromMillis(Files.getLastModifiedTime(assembly).toMillis() + 10_000));
        Assert.assertNull(restarted.getStoredSummary(assembly));
        DotNetAssemblySummary changed = restarted.getSummary(assembly);
        Assert.assertNotNull(changed);
        Assert.assertEquals("SampleFacade", changed.assemblyName());
    }

    @Test
    public void testContentShared() throws Exception {
        Path store = myTemp.newFolder("shared").toPath();
        Path first = myTemp.newFolder("first").toPath().resolve("Sample.dll");
        Path second = myTemp.newFolder("second").toPath().resolve("Sample.dll");
        Files.copy(resource("Sample.dll"), first);
        Files.copy(resource("Sample.dll"), second);

        DotNetAssemblySummaryIndexImpl index = new DotNetAssemblySummaryIndexImpl(store);
        DotNetAssemblySummary summary = index.getSummary(first);
        Assert.assertEquals(1, index.myReadCount.get());

        // a copy at another path is hashed, not read: its content is stored already
        Assert.assertNull(index.getStoredSummary(second));
        Assert.assertEquals(summary, index.getSummary(second));
        Assert.assertEquals(1, index.myReadCount.get());

        // after a restart, both paths are answered from the store without reading or hashing
        DotNetAssemblySummaryIndexImpl restarted = new DotNetAssemblySummaryIndexImpl(store);
        Assert.assertEquals(summary, restarted.getStoredSummary(first));
        Assert.assertEquals(summary, restarted.getStoredSummary(second));
        Assert.assertEquals(0, restarted.myReadCount.get());
    }
}
