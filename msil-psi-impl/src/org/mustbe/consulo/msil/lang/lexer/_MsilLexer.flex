package org.mustbe.consulo.msil.lang.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
%%

%public
%class _MsilLexer
%extends LexerBase
%unicode
%function advanceImpl
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE=[ \n\r\t\f]+
SINGLE_LINE_COMMENT="/""/"[^\r\n]*
MULTI_LINE_STYLE_COMMENT=("/*"{COMMENT_TAIL})|"/*"
COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")?

DIGIT = [0-9]+
SEPARATOR=(\.) | (\/)

IDENTIFIER_PART=[:jletter:] [:jletterdigit:]* (\`[:jletterdigit:]*)?
IDENTIFIER_IN_QUOTES="'"([^\\\'\r\n])*("'"|\\)?

IDENTIFIERS={IDENTIFIER_PART} | {IDENTIFIER_IN_QUOTES}

VALID_IDENTIFIERS={IDENTIFIERS}({SEPARATOR}{IDENTIFIERS})*

STRING_LITERAL=\"([^\\\"\r\n]|{ESCAPE_SEQUENCE})*(\"|\\)?
ESCAPE_SEQUENCE=\\[^\r\n]

DOUBLE_LITERAL = {DIGIT} "." {DIGIT}

%%

<YYINITIAL>
{
	"..."          { return MsilTokens.ELLIPSIS; }

	"{"            { return MsilTokens.LBRACE; }

	"}"            { return MsilTokens.RBRACE; }

	"<"            { return MsilTokens.LT; }

	">"            { return MsilTokens.GT; }

	"!"            { return MsilTokens.EXCL; }

	"::"           { return MsilTokens.COLONCOLON; }

	"["            { return MsilTokens.LBRACKET; }

	"]"            { return MsilTokens.RBRACKET; }

	"("            { return MsilTokens.LPAR; }

	")"            { return MsilTokens.RPAR; }

	"="            { return MsilTokens.EQ; }

	"&"            { return MsilTokens.AND; }

	"*"            { return MsilTokens.PERC; }

	","            { return MsilTokens.COMMA; }

	"+"            { return MsilTokens.PLUS; }

	"-"            { return MsilTokens.MINUS; }

	".class"       { return MsilTokens._CLASS_KEYWORD; }

	".ctor"        { return MsilTokens._CTOR_KEYWORD; }

	".cctor"       { return MsilTokens._CCTOR_KEYWORD; }

	".custom"      { return MsilTokens._CUSTOM_KEYWORD; }

	".method"      { return MsilTokens._METHOD_KEYWORD; }

	".field"       { return MsilTokens._FIELD_KEYWORD; }

	".property"    { return MsilTokens._PROPERTY_KEYWORD; }

	".event"       { return MsilTokens._EVENT_KEYWORD; }

	".assembly"    { return MsilTokens._ASSEMBLY_KEYWORD; }

	".param"       { return MsilTokens._PARAM_KEYWORD; }

	".get"         { return MsilTokens._GET_KEYWORD; }

	".set"         { return MsilTokens._SET_KEYWORD; }

	".addon"       { return MsilTokens._ADDON_KEYWORD; }

	".removeon"    { return MsilTokens._REMOVEON_KEYWORD; }

	"class"        { return MsilTokens.CLASS_KEYWORD; }

	"valuetype"    { return MsilTokens.VALUETYPE_KEYWORD; }

	"nullref"      { return MsilTokens.NULLREF_KEYWORD; }

	"int8"         { return MsilTokens.INT8_KEYWORD; }

	"uint8"        { return MsilTokens.UINT8_KEYWORD; }

	"int16"        { return MsilTokens.INT16_KEYWORD; }

	"int"          { return MsilTokens.INT_KEYWORD; }

	"uint"          { return MsilTokens.UINT_KEYWORD; }

	"uint16"       { return MsilTokens.UINT16_KEYWORD; }

	"int32"        { return MsilTokens.INT32_KEYWORD; }

	"uint32"       { return MsilTokens.UINT32_KEYWORD; }

	"int64"        { return MsilTokens.INT64_KEYWORD; }

	"uint64"       { return MsilTokens.UINT64_KEYWORD; }

	"float32"      { return MsilTokens.FLOAT32_KEYWORD; }

	"float64"      { return MsilTokens.FLOAT64_KEYWORD; }

	"void"         { return MsilTokens.VOID_KEYWORD; }

	"string"       { return MsilTokens.STRING_KEYWORD; }

	"bool"         { return MsilTokens.BOOL_KEYWORD; }

	"object"       { return MsilTokens.OBJECT_KEYWORD; }

	"char"         { return MsilTokens.CHAR_KEYWORD; }

	"extends"      { return MsilTokens.EXTENDS_KEYWORD; }

	"implements"   { return MsilTokens.IMPLEMENTS_KEYWORD; }

	"public"       { return MsilTokens.PUBLIC_KEYWORD; }

	"private"      { return MsilTokens.PRIVATE_KEYWORD; }

	"assembly"      { return MsilTokens.ASSEMBLY_KEYWORD; }

	"famorassem"    { return MsilTokens.FAMORASSEMBLY_KEYWORD; }

	"static"        { return MsilTokens.STATIC_KEYWORD; }

	"abstract"      { return MsilTokens.ABSTRACT_KEYWORD; }

	"literal"       { return MsilTokens.LITERAL_KEYWORD; }

	"initonly"      { return MsilTokens.INITONLY_KEYWORD; }

	"protected"     { return MsilTokens.PROTECTED_KEYWORD; }

	"final"         { return MsilTokens.FINAL_KEYWORD; }

	"hidebysig"     { return MsilTokens.HIDEBYSIG_KEYWORD; }

	"virtual"       { return MsilTokens.VIRTUAL_KEYWORD; }

	"vararg"       { return MsilTokens.VARARG_KEYWORD; }

	"value"         { return MsilTokens.VALUE_KEYWORD; }

	"nested"        { return MsilTokens.NESTED_KEYWORD; }

	"serializable"  { return MsilTokens.SERIALIZABLE_KEYWORD; }

	"specialname"   { return MsilTokens.SPECIALNAME_KEYWORD; }

	"rtspecialname" { return MsilTokens.RTSPECIALNAME_KEYWORD; }

	"sealed"        { return MsilTokens.SEALED_KEYWORD; }

	"interface"     { return MsilTokens.INTERFACE_KEYWORD; }

	"type"          { return MsilTokens.TYPE_KEYWORD; }

	"[out]"         { return MsilTokens.BRACKET_OUT_KEYWORD; }

	"[in]"          { return MsilTokens.BRACKET_IN_KEYWORD; }

	"[opt]"         { return MsilTokens.BRACKET_OPT_KEYWORD; }

	{DIGIT}         { return MsilTokens.NUMBER_LITERAL; }

	{DOUBLE_LITERAL} { return MsilTokens.DOUBLE_LITERAL; }

	{STRING_LITERAL} { return MsilTokens.STRING_LITERAL; }

	{VALID_IDENTIFIERS}    { return MsilTokens.IDENTIFIER; }

	{SINGLE_LINE_COMMENT}      { return MsilTokens.LINE_COMMENT; }

	{MULTI_LINE_STYLE_COMMENT} { return MsilTokens.BLOCK_COMMENT; }

	{WHITE_SPACE}  { return MsilTokens.WHITE_SPACE; }

	.              { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
