package consulo.csharp.cfs.lang.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import consulo.csharp.cfs.lang.CfsTokens;

%%

%{
  private IElementType myArgumentElementType;
  private int myParenthesesBalance;

  public _BaseLexer(IElementType argumentElementType) {
     myArgumentElementType = argumentElementType;
  }
%}

%class _BaseLexer
%extends LexerBase
%unicode
%function advanceImpl
%type IElementType
%eof{  return;
%eof}

%state ARGUMENT_WAIT
%state ALIGN_WAIT
%state FORMAT_WAIT

%%

<YYINITIAL>
{
   "{" { yybegin(ARGUMENT_WAIT); return CfsTokens.START; }

   [^]   { return CfsTokens.TEXT; }
}

<ARGUMENT_WAIT>
{
   ":" { yybegin(FORMAT_WAIT); return CfsTokens.COLON; }

   ","
   {
       if(myParenthesesBalance == 0)
       {
           yybegin(ALIGN_WAIT);
           return CfsTokens.COMMA;
       }
       return myArgumentElementType;
   }

   "}" { yybegin(YYINITIAL); return CfsTokens.END; }

   "(" { myParenthesesBalance ++; return  myArgumentElementType; }

   ")" { myParenthesesBalance --; return  myArgumentElementType; }

   [^]   { return myArgumentElementType; }
}

<ALIGN_WAIT>
{
   "}" { yybegin(YYINITIAL); return CfsTokens.END; }

   ":" { yybegin(FORMAT_WAIT); return CfsTokens.COLON; }

   [^]   {  return CfsTokens.ALIGN; }
}

<FORMAT_WAIT>
{
   "}" { yybegin(YYINITIAL); return CfsTokens.END; }

   [^]   { return CfsTokens.FORMAT; }
}