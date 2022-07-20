/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.execution.impl
{
	requires consulo.dotnet.execution.api;
	requires consulo.dotnet.api;
	requires consulo.dotnet.psi.api;
	requires consulo.dotnet.debugger.impl;
	requires consulo.module.ui.api;

	// TODO remove this dependency in future
	requires java.desktop;

	exports consulo.dotnet.run.impl;
	exports consulo.dotnet.run.impl.coverage;
	exports consulo.dotnet.run.impl.filters;
}