/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.debugger.impl {
	requires consulo.execution.api;
	requires consulo.execution.debug.api;
	requires consulo.compiler.api;
	requires consulo.language.editor.ui.api;

	requires consulo.dotnet.api;
	requires consulo.dotnet.psi.api;
	requires consulo.dotnet.debugger.api;
	requires consulo.internal.dotnet.msil.decompiler;
	requires consulo.internal.dotnet.asm;

	// TODO [VISTALL] remove this dep in future
	requires java.desktop;

	exports consulo.dotnet.debugger.impl;
	exports consulo.dotnet.debugger.impl.breakpoint;
	exports consulo.dotnet.debugger.impl.breakpoint.properties;
	exports consulo.dotnet.debugger.impl.breakpoint.ui;
	exports consulo.dotnet.debugger.impl.nodes;
	exports consulo.dotnet.debugger.impl.nodes.logicView;
	exports consulo.dotnet.debugger.impl.nodes.logicView.enumerator;
	exports consulo.dotnet.debugger.impl.nodes.objectReview;
	exports consulo.dotnet.debugger.impl.nodes.valueRender;
	exports consulo.dotnet.debugger.impl.proxy;
	exports consulo.dotnet.debugger.impl.runner;
	exports consulo.dotnet.debugger.impl.runner.remote;
}