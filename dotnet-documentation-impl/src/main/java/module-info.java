/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.documentation.impl
{
	requires consulo.dotnet.documentation.api;
	requires consulo.dotnet.api;
	requires consulo.dotnet.psi.api;
	requires consulo.ide.api;

	exports consulo.dotnet.documentation.impl;
}