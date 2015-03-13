package org.mustbe.consulo.csharp.cfs.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mustbe.consulo.csharp.cfs.lang.CfsTokens;

%%

%{
  private IElementType myArgumentElementType;

  public _BaseLexer(IElementType argumentElementType) {
     myArgumentElementType = argumentElementType;
  }
%}

%class _BaseLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%state ARGUMENT_WAIT
%state ALIGN_WAIT
%state FORMAT_WAIT

TEXT=([^\r\n\u2028\u2029\u000B\u000C\u0085!(\{)])+

ARGUMENT_WAIT_PART=([^\r\n\u2028\u2029\u000B\u000C\u0085!(\,\}\:)])+

ALIGN_WAIT=([^\r\n\u2028\u2029\u000B\u000C\u0085!(\}\:)])+

FORMAT_WAIT=([^\r\n\u2028\u2029\u000B\u000C\u0085!(\})])+

%%

<YYINITIAL>
{
   "{" { yybegin(ARGUMENT_WAIT); return CfsTokens.START; }

   {TEXT} { return CfsTokens.TEXT; }
}

<ARGUMENT_WAIT>
{
   ":" { yybegin(FORMAT_WAIT); return CfsTokens.COLON; }

   "," { yybegin(ALIGN_WAIT); return CfsTokens.COMMA; }

   "}" { yybegin(YYINITIAL); return CfsTokens.END; }

   {ARGUMENT_WAIT_PART}  { return myArgumentElementType; }
}

<ALIGN_WAIT>
{
   "}" { yybegin(YYINITIAL); return CfsTokens.END; }

   ":" { yybegin(FORMAT_WAIT); return CfsTokens.COLON; }

   {ALIGN_WAIT}   {  return CfsTokens.ALIGN; }
}

<FORMAT_WAIT>
{
   "}" { yybegin(YYINITIAL); return CfsTokens.END; }

   {FORMAT_WAIT} { return CfsTokens.FORMAT; }
}

[^]  { throw new Error("Illegal character <"+yytext()+">" + " state <" + zzLexicalState + ">"); }