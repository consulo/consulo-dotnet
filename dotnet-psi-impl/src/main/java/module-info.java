/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.psi.impl
{
	requires consulo.dotnet.api;
	requires consulo.dotnet.psi.api;
	requires consulo.language.impl;

	requires consulo.internal.dotnet.msil.decompiler;

	exports consulo.dotnet.psi.impl;
	exports consulo.dotnet.psi.impl.externalAttributes;
	exports consulo.dotnet.psi.impl.externalAttributes.nodes;
	exports consulo.dotnet.psi.impl.module.extension;
	exports consulo.dotnet.psi.impl.resolve;
	exports consulo.dotnet.psi.impl.resolve.impl;
	exports consulo.dotnet.psi.impl.stub;
}