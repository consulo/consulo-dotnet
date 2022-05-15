/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.debugger.api
{
	requires consulo.dotnet.psi.api;
	requires consulo.dotnet.api;
	requires consulo.internal.dotnet.msil.decompiler;
	requires consulo.execution.debug.api;

	exports consulo.dotnet.debugger;
	exports consulo.dotnet.debugger.nodes.logicView;
	exports consulo.dotnet.debugger.proxy;
	exports consulo.dotnet.debugger.proxy.light;
	exports consulo.dotnet.debugger.proxy.value;
}