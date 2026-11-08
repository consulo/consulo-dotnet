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
package consulo.dotnet.assembly.summary;

import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * What an assembly file offers to code that does not reference it yet: its identity, its public top-level types,
 * the types it forwards to other assemblies, and its extension methods. Read from the metadata of the file only,
 * stored per file by {@link DotNetAssemblySummaryIndex}.
 *
 * @param assemblyName      the name of the assembly (the identity a reference uses), null for a module without an assembly manifest
 * @param types             the public top-level types declared in the file
 * @param forwardedTypes    the public types the assembly forwards to another one ({@code [TypeForwardedTo]}, reference assemblies)
 * @param extensionMethods  the extension methods of the public static classes
 * @author VISTALL
 */
public record DotNetAssemblySummary(@Nullable String assemblyName,
                                    List<Type> types,
                                    List<ForwardedType> forwardedTypes,
                                    List<ExtensionMethod> extensionMethods) {
    /**
     * Kinds of a type, as bit flags
     */
    public static final int INTERFACE = 1;
    public static final int STATIC = 1 << 1;
    public static final int VALUE_TYPE = 1 << 2;
    public static final int ENUM = 1 << 3;
    public static final int DELEGATE = 1 << 4;
    public static final int ATTRIBUTE = 1 << 5;
    public static final int ABSTRACT = 1 << 6;

    /**
     * @param name       the name without the generic arity marker ({@code List} for {@code List`1})
     * @param namespace  the namespace, empty for the global namespace
     * @param arity      the number of generic parameters
     * @param kind       {@link #INTERFACE}, {@link #STATIC} … flags
     */
    public record Type(String name, String namespace, int arity, int kind) {
        public boolean is(int flag) {
            return (kind & flag) != 0;
        }
    }

    /**
     * @param name       the name without the generic arity marker
     * @param namespace  the namespace
     * @param arity      the number of generic parameters
     */
    public record ForwardedType(String name, String namespace, int arity) {
    }

    /**
     * @param name       the method name
     * @param namespace  the namespace of the static class declaring it - the one a {@code using} must import
     */
    public record ExtensionMethod(String name, String namespace) {
    }
}
