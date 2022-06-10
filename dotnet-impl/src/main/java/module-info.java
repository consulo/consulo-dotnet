/**
 * @author VISTALL
 * @since 15-May-22
 */
module consulo.dotnet.impl
{
	requires consulo.ide.api;
	requires consulo.ide.impl;

	requires consulo.internal.dotnet.asm;
	requires consulo.internal.dotnet.msil.decompiler;

	// TODO remove this dependency in future
	requires java.desktop;

	requires consulo.dotnet.composite.formatting.string;
	requires consulo.dotnet.debugger.api;
	requires consulo.dotnet.debugger.impl;
	requires consulo.dotnet.documentation.api;
	requires consulo.dotnet.documentation.impl;
	requires consulo.dotnet.execution.api;
	requires consulo.dotnet.execution.impl;
	requires consulo.dotnet.api;
	requires consulo.dotnet.psi.api;
	requires consulo.dotnet.psi.impl;
	requires consulo.dotnet.msil.api;
	requires consulo.dotnet.msil.impl;

	exports consulo.dotnet.icon;
	exports consulo.dotnet.impl.assembly;
	exports consulo.dotnet.impl.compiler;
	exports consulo.dotnet.impl.dll;
	exports consulo.dotnet.impl.dll.vfs;
	exports consulo.dotnet.impl.documentation;
	exports consulo.dotnet.impl.externalAttribute;
	exports consulo.dotnet.impl.ide;
	exports consulo.dotnet.impl.library;
	exports consulo.dotnet.impl.module;
	exports consulo.dotnet.impl.module.dependency;
	exports consulo.dotnet.impl.packageSupport;
	exports consulo.dotnet.impl.psi.impl;
	exports consulo.dotnet.impl.roots.orderEntry;
	exports consulo.dotnet.impl.sdk;
	exports consulo.dotnet.impl.testIntegration;
	exports consulo.dotnet.impl.ui.chooser;
}