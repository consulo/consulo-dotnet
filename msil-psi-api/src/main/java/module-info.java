/**
 * @author VISTALL
 * @since 15-May-22
 */
module consulo.dotnet.msil.api
{
	requires consulo.language.api;
	requires consulo.dotnet.psi.api;

	exports consulo.msil;
	exports consulo.msil.lang.psi;
	exports consulo.msil.representation;
}