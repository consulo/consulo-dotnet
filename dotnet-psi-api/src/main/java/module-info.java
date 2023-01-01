/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.psi.api
{
	requires transitive consulo.language.api;
	requires consulo.language.editor.ui.api;

	requires consulo.dotnet.api;
	requires consulo.internal.dotnet.msil.decompiler;

	exports consulo.dotnet.psi;
	exports consulo.dotnet.psi.internal;
	exports consulo.dotnet.psi.resolve;
	exports consulo.dotnet.psi.search.searches;
	exports consulo.dotnet.psi.test;
	exports consulo.dotnet.psi.ui.chooser;
}