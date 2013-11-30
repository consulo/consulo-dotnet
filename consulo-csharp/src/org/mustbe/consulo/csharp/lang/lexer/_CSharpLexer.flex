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
HEX_INTEGER_LITERAL = 0 [Xx] {HEX_DIGIT_OR_UNDERSCORE}*
BIN_INTEGER_LITERAL = 0 [Bb] {DIGIT_OR_UNDERSCORE}*

FLOAT_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Ff] | {DIGITS} [Ff]
DOUBLE_LITERAL = ({DEC_FP_LITERAL} | {HEX_FP_LITERAL}) [Dd]? | {DIGITS} [Dd]
DEC_FP_LITERAL = {DIGITS} {DEC_EXPONENT} | {DEC_SIGNIFICAND} {DEC_EXPONENT}?
DEC_SIGNIFICAND = "." {DIGITS} | {DIGITS} "." {DIGIT_OR_UNDERSCORE}*
DEC_EXPONENT = [Ee] [+-]? {DIGIT_OR_UNDERSCORE}*
HEX_FP_LITERAL = {HEX_SIGNIFICAND} {HEX_EXPONENT}
HEX_SIGNIFICAND = 0 [Xx] ({HEX_DIGIT_OR_UNDERSCORE}+ "."? | {HEX_DIGIT_OR_UNDERSCORE}* "." {HEX_DIGIT_OR_UNDERSCORE}+)
HEX_EXPONENT = [Pp] [+-]? {DIGIT_OR_UNDERSCORE}*

%%

<YYINITIAL>
{
	{VERBATIM_STRING_LITERAL} { return CSharpTokens.VERBATIM_STRING_LITERAL; }

	"using"                   { return CSharpTokens.USING_KEYWORD; }

// native types
	"string"                  { return CSharpTokens.STRING_KEYWORD; }

	"void"                    { return CSharpTokens.VOID_KEYWORD; }

	"int"                     { return CSharpTokens.INT_KEYWORD; }

// modifier tokens
	"static"                  { return CSharpTokens.STATIC_KEYWORD; }

	"public"                  { return CSharpTokens.PUBLIC_KEYWORD; }

	"in"                      { return CSharpTokens.IN_KEYWORD; }

	"out"                     { return CSharpTokens.OUT_KEYWORD; }

// declaration tokens
	"namespace"               { return CSharpTokens.NAMESPACE_KEYWORD; }

	"class"                   { return CSharpTokens.CLASS_KEYWORD; }

	"interface"               { return CSharpTokens.INTERFACE_KEYWORD; }

	"struct"                  { return CSharpTokens.STRUCT_KEYWORD; }

	"enum"                    { return CSharpTokens.ENUM_KEYWORD; }

	"event"                   { return CSharpTokens.EVENT_KEYWORD; }

	"delegate"                { return CSharpTokens.DELEGATE_KEYWORD; }

// expression tokens
	"new"                     { return CSharpTokens.NEW_KEYWORD; }

	"typeof"                  { return CSharpTokens.TYPEOF_KEYWORD; }

//
	"{"                       { return CSharpTokens.LBRACE; }

	"}"                       { return CSharpTokens.RBRACE; }

	"["                       { return CSharpTokens.LBRACKET; }

	"]"                       { return CSharpTokens.RBRACKET; }

	"("                       { return CSharpTokens.LPAR; }

	")"                       { return CSharpTokens.RPAR; }

	"<"                       { return CSharpTokens.LT; }

	">"                       { return CSharpTokens.GT; }

	"="                       { return CSharpTokens.EQ; }

	":"                       { return CSharpTokens.COLON; }

	";"                       { return CSharpTokens.SEMICOLON; }

	"."                       { return CSharpTokens.DOT; }

	","                       { return CSharpTokens.COMMA; }

	{SINGLE_LINE_DOC_COMMENT} { return CSharpTokens.LINE_DOC_COMMENT; }

	{SINGLE_LINE_COMMENT}     { return CSharpTokens.LINE_COMMENT; }

	{MULTI_LINE_STYLE_COMMENT} { return CSharpTokens.BLOCK_COMMENT; }

	{INTEGER_LITERAL}         { return CSharpTokens.INTEGER_LITERAL; }

	{LONG_LITERAL}            { return CSharpTokens.LONG_LITERAL; }

	{FLOAT_LITERAL}           { return CSharpTokens.FLOAT_LITERAL; }

	{DOUBLE_LITERAL}          { return CSharpTokens.DOUBLE_LITERAL; }

	{CHARACTER_LITERAL}       { return CSharpTokens.CHARACTER_LITERAL; }

	{STRING_LITERAL}          { return CSharpTokens.STRING_LITERAL; }

	{IDENTIFIER}              { return CSharpTokens.IDENTIFIER; }

	{WHITE_SPACE}             { return CSharpTokens.WHITE_SPACE; }

	.                         { return CSharpTokens.BAD_CHARACTER; }
}
