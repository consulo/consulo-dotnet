/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.composite.formatting.string
{
	requires consulo.language.api;
	requires consulo.language.impl;
	requires consulo.language.editor.api;
	requires consulo.dotnet.psi.api;
	requires consulo.dotnet.api;

	exports consulo.dotnet.cfs.ide.completion;
	exports consulo.dotnet.cfs.ide.highlight;
	exports consulo.dotnet.cfs.lang;
	exports consulo.dotnet.cfs.lang.lexer;
	exports consulo.dotnet.cfs.lang.parser;
	exports consulo.dotnet.cfs.psi;
}