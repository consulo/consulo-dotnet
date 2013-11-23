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

IDENTIFIER=[:jletter:] [:jletterdigit:]*

INTEGER_LITERAL={DECIMAL_INTEGER_LITERAL}|{HEX_INTEGER_LITERAL}
DECIMAL_INTEGER_LITERAL=(0|([1-9]({DIGIT})*))
HEX_INTEGER_LITERAL=0[Xx]([0-9A-Fa-f])*

VERBATIM_STRING_LITERAL=\@\"([^\\\"\"\r\n]|{ESCAPE_SEQUENCE}|\n)*(\"|\\)?

//
EXPONENT_PART=[Ee]["+""-"]?({DIGIT})*
FLOATING_POINT_LITERAL1=({DIGIT})+("."({DIGIT})+)?({EXPONENT_PART})?
FLOATING_POINT_LITERAL2="."({DIGIT})+({EXPONENT_PART})?
FLOAT_LITERAL=(({FLOATING_POINT_LITERAL1})|({FLOATING_POINT_LITERAL2}))

%%

<YYINITIAL>
{
	{VERBATIM_STRING_LITERAL} { return CSharpTokens.VERBATIM_STRING_LITERAL; }

	"string"                 { return CSharpTokens.STRING_KEYWORD; }

	"void"                   { return CSharpTokens.VOID_KEYWORD; }

	"using"                  { return CSharpTokens.USING_KEYWORD; }

	"static"                 { return CSharpTokens.STATIC_KEYWORD; }

	"class"                  { return CSharpTokens.CLASS_KEYWORD; }

	{SINGLE_LINE_COMMENT}     { return CSharpTokens.LINE_COMMENT; }

	{CHARACTER_LITERAL}       { return CSharpTokens.CHARACTER_LITERAL; }

	{STRING_LITERAL}          { return CSharpTokens.STRING_LITERAL; }

	{IDENTIFIER}              { return CSharpTokens.IDENTIFIER; }

	{WHITE_SPACE}             { return CSharpTokens.WHITE_SPACE; }

	.                         { return CSharpTokens.BAD_CHARACTER; }
}
