package org.mustbe.consulo.csharp.lang.lexer;

import java.util.*;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;

%%

%class _CSharpLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%state MACRO
%state MACRO_ENTERED

DIGIT=[0-9]
LETTER=[a-z]|[A-Z]
WHITE_SPACE=[ \n\r\t\f]+
SINGLE_LINE_COMMENT="/""/"[^\r\n]*
SINGLE_LINE_DOC_COMMENT="/""/""/"[^\r\n]*
MULTI_LINE_STYLE_COMMENT=("/*"[^"*"]{COMMENT_TAIL})|"/*"

COMMENT_TAIL=([^"*"]*("*"+[^"*""/"])?)*("*"+"/")?
CHARACTER_LITERAL="'"([^\\\'\r\n]|{ESCAPE_SEQUENCE})*("'"|\\)?
STRING_LITERAL=\"([^\\\"\r\n]|{ESCAPE_SEQUENCE})*(\"|\\)?
ESCAPE_SEQUENCE=\\[^\r\n]

VERBATIM_STRING_LITERAL=\@\"([^\\\"\"\r\n]|{ESCAPE_SEQUENCE}|\n)*(\"|\\)?

IDENTIFIER=[:jletter:] [:jletterdigit:]*

DIGIT = [0-9]
DIGIT_OR_UNDERSCORE = [_0-9]
DIGITS = {DIGIT} | {DIGIT} {DIGIT_OR_UNDERSCORE}*
HEX_DIGIT_OR_UNDERSCORE = [_0-9A-Fa-f]

INTEGER_LITERAL = {DIGITS} | {HEX_INTEGER_LITERAL} | {BIN_INTEGER_LITERAL}
LONG_LITERAL = {INTEGER_LITERAL} [Ll]
UINTEGER_LITERAL = {INTEGER_LITERAL} [Uu]
ULONG_LITERAL = {INTEGER_LITERAL} ([Uu] [Ll] | [Ll] [Uu])
HEX_INTEGER_LITERAL = 0 [Xx] {HEX_DIGIT_OR_UNDERSCORE}*
BIN_INTEGER_LITERAL = 0 [Bb] {DIGIT_OR_UNDERSCORE}*

FLOAT_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Ff] | {DIGITS} [Ff]
DOUBLE_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Dd]? | {DIGITS} [Dd]
DECIMAL_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Mm] | {DIGITS} [Mm]
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*
HEX_FP_LITERAL = {HEX_SIGNIFICAND} {HEX_EXPONENT}
HEX_SIGNIFICAND = 0 [Xx] ({HEX_DIGIT_OR_UNDERSCORE}+ "."? | {HEX_DIGIT_OR_UNDERSCORE}* "." {HEX_DIGIT_OR_UNDERSCORE}+)
HEX_EXPONENT = [Pp] [+-]? {DIGIT_OR_UNDERSCORE}*

MACRO_WHITE_SPACE=[ \t\f]+
MACRO_NEW_LINE=\r\n|\n|\r

MACRO_DEFINE="#"{WHITE_SPACE}?"define"
MACRO_IF="#"{WHITE_SPACE}?"if"
MACRO_ENDIF="#"{WHITE_SPACE}?"endif"
MACRO_REGION="#"{WHITE_SPACE}?"region"
MACRO_ENDREGION="#"{WHITE_SPACE}?"endregion"
%%

<MACRO>
{
	{MACRO_IF}           { yybegin(MACRO_ENTERED); return CSharpTokens.MACRO_IF_KEYWORD; }

	{MACRO_ENDIF}        { yybegin(MACRO_ENTERED); return CSharpTokens.MACRO_ENDIF_KEYWORD; }

	{MACRO_DEFINE}       { yybegin(MACRO_ENTERED); return CSharpTokens.MACRO_DEFINE_KEYWORD; }

	{MACRO_REGION}       { yybegin(MACRO_ENTERED); return CSharpTokens.MACRO_REGION_KEYWORD; }

	{MACRO_ENDREGION}    { yybegin(MACRO_ENTERED); return CSharpTokens.MACRO_ENDREGION_KEYWORD; }

	{MACRO_NEW_LINE}     { yybegin(YYINITIAL); return CSharpTokens.MACRO_STOP; }

	{MACRO_WHITE_SPACE}  {  return CSharpTokens.WHITE_SPACE; }

	.                    { return CSharpTokens.BAD_CHARACTER; }
}

<MACRO_ENTERED>
{
	{IDENTIFIER}         { return CSharpTokens.MACRO_VALUE; }

	{MACRO_NEW_LINE}     { yybegin(YYINITIAL); return CSharpTokens.MACRO_STOP; }

	{MACRO_WHITE_SPACE}  { return CSharpTokens.WHITE_SPACE; }

	.                    { return CSharpTokens.BAD_CHARACTER; }
}

<YYINITIAL>
{
	"#"
	{
		yypushback(1);
		yybegin(MACRO);
	}

	{VERBATIM_STRING_LITERAL} { return CSharpTokens.VERBATIM_STRING_LITERAL; }

	"using"                   { return CSharpTokens.USING_KEYWORD; }

// native types
	"string"                  { return CSharpTokens.STRING_KEYWORD; }

	"void"                    { return CSharpTokens.VOID_KEYWORD; }

	"int"                     { return CSharpTokens.INT_KEYWORD; }

	"bool"                    { return CSharpTokens.BOOL_KEYWORD; }

	"byte"                    { return CSharpTokens.BYTE_KEYWORD; }

	"char"                    { return CSharpTokens.CHAR_KEYWORD; }

	"decimal"                 { return CSharpTokens.DECIMAL_KEYWORD; }

	"double"                  { return CSharpTokens.DOUBLE_KEYWORD; }

	"float"                   { return CSharpTokens.FLOAT_KEYWORD; }

	"long"                    { return CSharpTokens.LONG_KEYWORD; }

	"object"                  { return CSharpTokens.OBJECT_KEYWORD; }

	"sbyte"                   { return CSharpTokens.SBYTE_KEYWORD; }

	"short"                   { return CSharpTokens.SHORT_KEYWORD; }

	"uint"                    { return CSharpTokens.UINT_KEYWORD; }

	"ulong"                   { return CSharpTokens.ULONG_KEYWORD; }

	"ushort"                  { return CSharpTokens.USHORT_KEYWORD; }

	"dynamic"                 { return CSharpTokens.DYNAMIC_KEYWORD; }

// modifier tokens
	"static"                  { return CSharpTokens.STATIC_KEYWORD; }

	"public"                  { return CSharpTokens.PUBLIC_KEYWORD; }

	"in"                      { return CSharpTokens.IN_KEYWORD; }

	"out"                     { return CSharpTokens.OUT_KEYWORD; }

	"abstract"                { return CSharpTokens.ABSTRACT_KEYWORD; }

	"extern"                  { return CSharpTokens.EXTERN_KEYWORD; }

	"internal"                { return CSharpTokens.INTERNAL_KEYWORD; }

	"override"                { return CSharpTokens.OVERRIDE_KEYWORD; }

	"params"                  { return CSharpTokens.PARAMS_KEYWORD; }

	"private"                 { return CSharpTokens.PRIVATE_KEYWORD; }

	"protected"               { return CSharpTokens.PROTECTED_KEYWORD; }

	"ref"                     { return CSharpTokens.REF_KEYWORD; }

	"readonly"                { return CSharpTokens.READONLY_KEYWORD; }

	"operator"                { return CSharpTokens.OPERATOR_KEYWORD; }

	"sealed"                  { return CSharpTokens.SEALED_KEYWORD; }

	"unsafe"                  { return CSharpTokens.UNSAFE_KEYWORD; }

	"virtual"                 { return CSharpTokens.VIRTUAL_KEYWORD; }

	"volatile"                { return CSharpTokens.VOLATILE_KEYWORD; }

// declaration tokens
	"namespace"               { return CSharpTokens.NAMESPACE_KEYWORD; }

	"class"                   { return CSharpTokens.CLASS_KEYWORD; }

	"interface"               { return CSharpTokens.INTERFACE_KEYWORD; }

	"struct"                  { return CSharpTokens.STRUCT_KEYWORD; }

	"enum"                    { return CSharpTokens.ENUM_KEYWORD; }

	"event"                   { return CSharpTokens.EVENT_KEYWORD; }

	"delegate"                { return CSharpTokens.DELEGATE_KEYWORD; }

	"const"                   { return CSharpTokens.CONST_KEYWORD; }

// expression tokens
	"new"                     { return CSharpTokens.NEW_KEYWORD; }

	"typeof"                  { return CSharpTokens.TYPEOF_KEYWORD; }

	"lock"                    { return CSharpTokens.LOCK_KEYWORD; }

	"return"                  { return CSharpTokens.RETURN_KEYWORD; }

	"while"                   { return CSharpTokens.WHILE_KEYWORD; }

	"break"                   { return CSharpTokens.BREAK_KEYWORD; }

	"continue"                { return CSharpTokens.CONTINUE_KEYWORD; }

//
	"{"                       { return CSharpTokens.LBRACE; }

	"}"                       { return CSharpTokens.RBRACE; }

	"["                       { return CSharpTokens.LBRACKET; }

	"]"                       { return CSharpTokens.RBRACKET; }

	"("                       { return CSharpTokens.LPAR; }

	")"                       { return CSharpTokens.RPAR; }

	"<="                      { return CSharpTokens.LTEQ; }

	">="                      { return CSharpTokens.GTEQ; }

	"<"                       { return CSharpTokens.LT; }

	">"                       { return CSharpTokens.GT; }

	"="                       { return CSharpTokens.EQ; }

	":"                       { return CSharpTokens.COLON; }

	"::"                      { return CSharpTokens.COLONCOLON; }

	";"                       { return CSharpTokens.SEMICOLON; }

	"*"                       { return CSharpTokens.MUL; }

	"=="                      { return CSharpTokens.EQEQ; }

	"++"                      { return CSharpTokens.PLUSPLUS; }

	"+"                       { return CSharpTokens.PLUS; }

	"--"                      { return CSharpTokens.MINUSMINUS; }

	"-"                       { return CSharpTokens.MINUS; }

	"/"                       { return CSharpTokens.DIV; }

	"%"                       { return CSharpTokens.PERC; }

	"&"                       { return CSharpTokens.AND; }

	"|"                       { return CSharpTokens.OR; }

	"~"                       { return CSharpTokens.TILDE; }

	"!="                      { return CSharpTokens.NTEQ; }

	"!"                       { return CSharpTokens.EXCL; }

	"^"                       { return CSharpTokens.CARET; }

	"."                       { return CSharpTokens.DOT; }

	","                       { return CSharpTokens.COMMA; }

	"??"                      { return CSharpTokens.NULL_COALESCING; }

	"false"                   { return CSharpTokens.BOOL_LITERAL; }

	"true"                    { return CSharpTokens.BOOL_LITERAL; }

	"null"                    { return CSharpTokens.NULL_LITERAL; }

	{SINGLE_LINE_DOC_COMMENT} { return CSharpTokens.LINE_DOC_COMMENT; }

	{SINGLE_LINE_COMMENT}     { return CSharpTokens.LINE_COMMENT; }

	{MULTI_LINE_STYLE_COMMENT} { return CSharpTokens.BLOCK_COMMENT; }

	{UINTEGER_LITERAL}        { return CSharpTokens.UINTEGER_LITERAL; }

	{ULONG_LITERAL}           { return CSharpTokens.ULONG_LITERAL; }

	{INTEGER_LITERAL}         { return CSharpTokens.INTEGER_LITERAL; }

	{LONG_LITERAL}            { return CSharpTokens.LONG_LITERAL; }

	{DECIMAL_LITERAL}         { return CSharpTokens.DECIMAL_LITERAL; }

	{DOUBLE_LITERAL}          { return CSharpTokens.DOUBLE_LITERAL; }

	{FLOAT_LITERAL}           { return CSharpTokens.FLOAT_LITERAL; }

	{CHARACTER_LITERAL}       { return CSharpTokens.CHARACTER_LITERAL; }

	{STRING_LITERAL}          { return CSharpTokens.STRING_LITERAL; }

	{IDENTIFIER}              { return CSharpTokens.IDENTIFIER; }

	{WHITE_SPACE}             { return CSharpTokens.WHITE_SPACE; }

	.                         { return CSharpTokens.BAD_CHARACTER; }
}
