package org.mustbe.consulo.csharp.lang.lexer;

import java.util.*;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mustbe.consulo.csharp.lang.psi.CSharpMacroTokens;

%%

%class _CSharpMacroLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%state MACRO
%state MACRO_ENTERED
%state MACRO_EXPRESSION

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
	{MACRO_IF}           { yybegin(MACRO_EXPRESSION); return CSharpMacroTokens.MACRO_IF_KEYWORD; }

	{MACRO_ENDIF}        { yybegin(MACRO_ENTERED); return CSharpMacroTokens.MACRO_ENDIF_KEYWORD; }

	{MACRO_DEFINE}       { yybegin(MACRO_ENTERED); return CSharpMacroTokens.MACRO_DEFINE_KEYWORD; }

	{MACRO_REGION}       { yybegin(MACRO_ENTERED); return CSharpMacroTokens.MACRO_REGION_KEYWORD; }

	{MACRO_ENDREGION}    { yybegin(MACRO_ENTERED); return CSharpMacroTokens.MACRO_ENDREGION_KEYWORD; }

	{MACRO_NEW_LINE}     { yybegin(YYINITIAL); return CSharpMacroTokens.MACRO_STOP; }

	{MACRO_WHITE_SPACE}  {  return CSharpMacroTokens.WHITE_SPACE; }

	.                    { return CSharpMacroTokens.BAD_CHARACTER; }
}

<MACRO_ENTERED>
{
	{IDENTIFIER}         { return CSharpMacroTokens.MACRO_VALUE; }

	{MACRO_NEW_LINE}     { yybegin(YYINITIAL); return CSharpMacroTokens.MACRO_STOP; }

	{MACRO_WHITE_SPACE}  { return CSharpMacroTokens.WHITE_SPACE; }

	.                    { return CSharpMacroTokens.BAD_CHARACTER; }
}

<MACRO_EXPRESSION>
{
	"("                  { return CSharpMacroTokens.LPAR; }

	")"                  { return CSharpMacroTokens.RPAR; }

	"!"                  { return CSharpMacroTokens.EXCL; }

	"&&"                 { return CSharpMacroTokens.ANDAND; }

	"||"                 { return CSharpMacroTokens.OROR; }

	{IDENTIFIER}         { return CSharpMacroTokens.IDENTIFIER; }

	{MACRO_NEW_LINE}     { yybegin(YYINITIAL); return CSharpMacroTokens.MACRO_STOP; }

	{MACRO_WHITE_SPACE}  { return CSharpMacroTokens.WHITE_SPACE; }

	.                    { return CSharpMacroTokens.BAD_CHARACTER; }
}

<YYINITIAL>
{
	"#"
	{
		yypushback(1);
		yybegin(MACRO);
	}

	{WHITE_SPACE}             { return CSharpMacroTokens.CSHARP_FRAGMENT; }

	.                         { return CSharpMacroTokens.CSHARP_FRAGMENT; }
}
