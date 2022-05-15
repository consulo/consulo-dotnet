/**
 * @author VISTALL
 * @since 15-May-22
 */
open module consulo.dotnet.msil.impl
{
	requires consulo.dotnet.api;
	requires consulo.dotnet.psi.api;
	requires consulo.dotnet.msil.api;
	requires consulo.language.impl;
	requires consulo.dotnet.psi.impl;
	requires consulo.ide.api;

	requires consulo.internal.dotnet.asm;
	requires consulo.internal.dotnet.msil.decompiler;

	requires org.jooq.joou;

	// TODO remove this dependency in future
	requires consulo.ide.impl;

	exports consulo.msil.impl.ide;
	exports consulo.msil.impl.ide.highlight;
	exports consulo.msil.impl.ide.navigation;
	exports consulo.msil.impl.ide.presentation;
	exports consulo.msil.impl.lang;
	exports consulo.msil.impl.lang.lexer;
	exports consulo.msil.impl.lang.parser;
	exports consulo.msil.impl.lang.psi;
	exports consulo.msil.impl.lang.psi.impl;
	exports consulo.msil.impl.lang.psi.impl.elementType;
	exports consulo.msil.impl.lang.psi.impl.elementType.stub;
	exports consulo.msil.impl.lang.psi.impl.elementType.stub.index;
	exports consulo.msil.impl.lang.psi.impl.resolve;
	exports consulo.msil.impl.lang.psi.impl.searchers;
	exports consulo.msil.impl.lang.psi.impl.type;
	exports consulo.msil.impl.lang.stubbing;
	exports consulo.msil.impl.lang.stubbing.values;
	exports consulo.msil.impl.representation;
	exports consulo.msil.impl.representation.fileSystem;
	exports consulo.msil.impl.representation.projectView;
}