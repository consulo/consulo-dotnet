/**
 * @author VISTALL
 * @since 14-May-22
 */
open module consulo.dotnet.api {
    requires consulo.language.api;
    requires consulo.path.macro.api;
    requires consulo.compiler.api;
    requires consulo.module.ui.api;
    
    requires consulo.internal.dotnet.asm;

    // TODO [VISTALL] remove this dep in future
    requires java.desktop;

    exports consulo.dotnet;
    exports consulo.dotnet.assembly;
    exports consulo.dotnet.compiler;
    exports consulo.dotnet.dll;
    exports consulo.dotnet.externalAttributes;
    exports consulo.dotnet.module;
    exports consulo.dotnet.module.extension;
    exports consulo.dotnet.module.macro;
    exports consulo.dotnet.sdk;
    exports consulo.dotnet.util;
}