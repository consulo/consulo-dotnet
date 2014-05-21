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

	"&"            { return MsilTokens.AND; }

	"*"            { return MsilTokens.PERC; }

	","            { return MsilTokens.COMMA; }

	".class"       { return MsilTokens._CLASS_KEYWORD; }

	".custom"      { return MsilTokens._CUSTOM_KEYWORD; }

	".method"      { return MsilTokens._METHOD_KEYWORD; }

	".field"       { return MsilTokens._FIELD_KEYWORD; }

	".property"    { return MsilTokens._PROPERTY_KEYWORD; }

	".event"       { return MsilTokens._EVENT_KEYWORD; }

	".assembly"    { return MsilTokens._ASSEMBLY_KEYWORD; }

	"class"        { return MsilTokens.CLASS_KEYWORD; }

	"valuetype"    { return MsilTokens.VALUETYPE_KEYWORD; }

	"int8"         { return MsilTokens.INT8_KEYWORD; }

	"uint8"        { return MsilTokens.UINT8_KEYWORD; }

	"int16"        { return MsilTokens.INT16_KEYWORD; }

	"uint16"       { return MsilTokens.UINT16_KEYWORD; }

	"int32"        { return MsilTokens.INT32_KEYWORD; }

	"uint32"       { return MsilTokens.UINT32_KEYWORD; }

	"int64"        { return MsilTokens.INT64_KEYWORD; }

	"uint64"       { return MsilTokens.UINT64_KEYWORD; }

	"float"        { return MsilTokens.FLOAT_KEYWORD; }

	"float64"      { return MsilTokens.FLOAT64_KEYWORD; }

	"void"         { return MsilTokens.VOID_KEYWORD; }

	"string"       { return MsilTokens.STRING_KEYWORD; }

	"bool"         { return MsilTokens.BOOL_KEYWORD; }

	"object"       { return MsilTokens.OBJECT_KEYWORD; }

	"extends"      { return MsilTokens.EXTENDS_KEYWORD; }

	"implements"   { return MsilTokens.IMPLEMENTS_KEYWORD; }

	{SINGLE_LINE_COMMENT}      { return MsilTokens.LINE_COMMENT; }

	{MULTI_LINE_STYLE_COMMENT} { return MsilTokens.BLOCK_COMMENT; }

	{WHITE_SPACE}  { return MsilTokens.WHITE_SPACE; }

	.              { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
