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
import consulo.internal.dotnet.asm.mbel.AbstractTypeReference;
import consulo.internal.dotnet.asm.mbel.AssemblyInfo;
import consulo.internal.dotnet.asm.mbel.CustomAttribute;
import consulo.internal.dotnet.asm.mbel.ExportedTypeRef;
import consulo.internal.dotnet.asm.mbel.MethodDef;
import consulo.internal.dotnet.asm.mbel.MethodDefOrRef;
import consulo.internal.dotnet.asm.mbel.ModuleParser;
import consulo.internal.dotnet.asm.mbel.TypeDef;
import consulo.internal.dotnet.asm.mbel.TypeRef;
import consulo.internal.dotnet.asm.parse.MSILParseException;
import consulo.internal.dotnet.asm.signature.MethodAttributes;
import consulo.internal.dotnet.asm.signature.TypeAttributes;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a {@link DotNetAssemblySummary} from the metadata of an assembly file
 *
 * @author VISTALL
 */
final class DotNetAssemblySummaryReader {
    private static final String EXTENSION_ATTRIBUTE = "System.Runtime.CompilerServices.ExtensionAttribute";
    private static final String MULTICAST_DELEGATE = "System.MulticastDelegate";
    // ECMA-335 II.23.1.15: an exported type that is a forwarder (TypeForwardedToAttribute)
    private static final long FORWARDER = 0x00200000;

    private DotNetAssemblySummaryReader() {
    }

    /**
     * @return null if the file is not a .NET module
     */
    @Nullable
    static DotNetAssemblySummary read(Path file) {
        ModuleParser parser;
        try {
            parser = new ModuleParser(file);
        }
        catch (IOException | MSILParseException | RuntimeException e) {
            return null;
        }

        List<DotNetAssemblySummary.Type> types = new ArrayList<>();
        List<DotNetAssemblySummary.ExtensionMethod> extensionMethods = new ArrayList<>();
        for (TypeDef typeDef : parser.getTypeDefs()) {
            String name = typeDef.getName();
            // top-level public types only: nested types have a nested visibility, compiler generated ones a '<' name
            if ((typeDef.getFlags() & TypeAttributes.VisibilityMask) != TypeAttributes.Public || name.isEmpty() || name.charAt(0) == '<') {
                continue;
            }
            String namespace = typeDef.getNamespace() == null ? "" : typeDef.getNamespace();
            int kind = kind(typeDef);
            types.add(new DotNetAssemblySummary.Type(cutArity(name), namespace, arity(name), kind));

            if ((kind & DotNetAssemblySummary.STATIC) != 0) {
                for (MethodDef method : typeDef.getMethods()) {
                    if ((method.getFlags() & MethodAttributes.MemberAccessMask) == MethodAttributes.Public
                        && (method.getFlags() & MethodAttributes.Static) != 0
                        && hasAttribute(method.getCustomAttributes(), EXTENSION_ATTRIBUTE)) {
                        extensionMethods.add(new DotNetAssemblySummary.ExtensionMethod(method.getName(), namespace));
                    }
                }
            }
        }

        List<DotNetAssemblySummary.ForwardedType> forwardedTypes = new ArrayList<>();
        AssemblyInfo assemblyInfo = parser.getAssemblyInfo();
        if (assemblyInfo != null) {
            for (ExportedTypeRef exported : assemblyInfo.getExportedTypes()) {
                // top-level forwarders: a nested exported type has its declaring exported type as the implementation
                if ((exported.getFlags() & FORWARDER) == 0 || exported.getExportedTypeRef() != null) {
                    continue;
                }
                String name = exported.getName();
                forwardedTypes.add(new DotNetAssemblySummary.ForwardedType(cutArity(name),
                    exported.getNamespace() == null ? "" : exported.getNamespace(), arity(name)));
            }
        }

        return new DotNetAssemblySummary(assemblyInfo == null ? null : assemblyInfo.getName(), types, forwardedTypes, extensionMethods);
    }

    private static int kind(TypeDef typeDef) {
        long flags = typeDef.getFlags();
        int kind = 0;
        if ((flags & TypeAttributes.Interface) != 0) {
            return DotNetAssemblySummary.INTERFACE;
        }
        boolean isAbstract = (flags & TypeAttributes.Abstract) != 0;
        if (isAbstract && (flags & TypeAttributes.Sealed) != 0) {
            kind |= DotNetAssemblySummary.STATIC;
        }
        else if (isAbstract) {
            kind |= DotNetAssemblySummary.ABSTRACT;
        }
        if (typeDef.isEnum()) {
            kind |= DotNetAssemblySummary.ENUM | DotNetAssemblySummary.VALUE_TYPE;
        }
        else if (typeDef.isValueType()) {
            kind |= DotNetAssemblySummary.VALUE_TYPE;
        }
        AbstractTypeReference superClass = typeDef.getSuperClass();
        // a generic base class (TypeSpec) has no name to compare: it is no delegate and no attribute base
        String superName = superClass instanceof TypeRef ref ? ref.getFullName() : null;
        if (MULTICAST_DELEGATE.equals(superName)) {
            kind |= DotNetAssemblySummary.DELEGATE;
        }
        // the base class may be in another assembly: an attribute class is named so, and so is its base
        if (typeDef.getName().endsWith("Attribute") && superName != null && superName.endsWith("Attribute")) {
            kind |= DotNetAssemblySummary.ATTRIBUTE;
        }
        return kind;
    }

    private static boolean hasAttribute(List<CustomAttribute> attributes, String fullName) {
        for (CustomAttribute attribute : attributes) {
            MethodDefOrRef constructor = attribute.getConstructor();
            AbstractTypeReference owner = constructor == null ? null : constructor.getParent();
            if (owner instanceof TypeRef ref && fullName.equals(ref.getFullName())) {
                return true;
            }
        }
        return false;
    }

    static String cutArity(String name) {
        int index = name.lastIndexOf('`');
        return index > 0 ? name.substring(0, index) : name;
    }

    static int arity(String name) {
        int index = name.lastIndexOf('`');
        if (index <= 0) {
            return 0;
        }
        try {
            return Integer.parseInt(name.substring(index + 1));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
}
