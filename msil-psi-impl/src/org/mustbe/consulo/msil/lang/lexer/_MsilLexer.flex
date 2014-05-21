package org.mustbe.consulo.msil.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
%%

%public
%class _MsilLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE=[ \n\r\t\f]+
SINGLE_LINE_COMMENT="/""/"[^\r\n]*
MULTI_LINE_STYLE_COMMENT=("/*"{COMMENT_TAIL})|"/*"
COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")?

%%

<YYINITIAL>
{
	"{"            { return MsilTokens.LBRACE; }

	"}"            { return MsilTokens.RBRACE; }

	".class"       { return MsilTokens._CLASS_KEYWORD; }

	".custom"      { return MsilTokens._CUSTOM_KEYWORD; }

	".method"      { return MsilTokens._METHOD_KEYWORD; }

	".field"       { return MsilTokens._FIELD_KEYWORD; }

	".property"    { return MsilTokens._PROPERTY_KEYWORD; }

	".event"       { return MsilTokens._EVENT_KEYWORD; }

	".assembly"    { return MsilTokens._ASSEMBLY_KEYWORD; }

	"class"        { return MsilTokens.CLASS_KEYWORD; }

	"valuetype"    { return MsilTokens.VALUETYPE_KEYWORD; }

	"int32"        { return MsilTokens.INT32_KEYWORD; }

	"int64"        { return MsilTokens.INT64_KEYWORD; }

	{SINGLE_LINE_COMMENT}      { return MsilTokens.LINE_COMMENT; }

	{MULTI_LINE_STYLE_COMMENT} { return MsilTokens.BLOCK_COMMENT; }

	{WHITE_SPACE}  { return MsilTokens.WHITE_SPACE; }

	.              { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
