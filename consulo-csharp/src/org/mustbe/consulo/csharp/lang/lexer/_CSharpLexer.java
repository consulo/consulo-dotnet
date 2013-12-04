/* The following code was generated by JFlex 1.4.3 on 04.12.13 22:30 */

package org.mustbe.consulo.csharp.lang.lexer;

import java.util.*;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 04.12.13 22:30 from the specification file
 * <tt>_CSharpLexer.flex</tt>
 */
class _CSharpLexer implements FlexLexer {
  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\14\1\3\1\13\1\0\1\3\1\5\16\14\4\0\1\3\1\0"+
    "\1\11\1\0\1\2\2\0\1\7\1\64\1\65\1\6\1\36\1\73"+
    "\1\36\1\33\1\4\1\22\11\1\1\71\1\72\1\66\1\70\1\67"+
    "\1\0\1\12\1\17\1\26\1\17\1\32\1\35\1\30\5\2\1\21"+
    "\3\2\1\40\7\2\1\24\2\2\1\62\1\10\1\63\1\0\1\15"+
    "\1\0\1\16\1\25\1\53\1\31\1\34\1\27\1\45\1\54\1\43"+
    "\1\56\1\2\1\20\1\55\1\44\1\51\1\37\1\2\1\47\1\42"+
    "\1\46\1\41\1\50\1\57\1\23\1\52\1\2\1\60\1\0\1\61"+
    "\1\0\41\14\2\0\4\2\4\0\1\2\2\0\1\14\7\0\1\2"+
    "\4\0\1\2\5\0\27\2\1\0\37\2\1\0\u013f\2\31\0\162\2"+
    "\4\0\14\2\16\0\5\2\11\0\1\2\21\0\130\14\5\0\23\14"+
    "\12\0\1\2\13\0\1\2\1\0\3\2\1\0\1\2\1\0\24\2"+
    "\1\0\54\2\1\0\46\2\1\0\5\2\4\0\202\2\1\0\4\14"+
    "\3\0\105\2\1\0\46\2\2\0\2\2\6\0\20\2\41\0\46\2"+
    "\2\0\1\2\7\0\47\2\11\0\21\14\1\0\27\14\1\0\3\14"+
    "\1\0\1\14\1\0\2\14\1\0\1\14\13\0\33\2\5\0\3\2"+
    "\15\0\4\14\14\0\6\14\13\0\32\2\5\0\13\2\16\14\7\0"+
    "\12\14\4\0\2\2\1\14\143\2\1\0\1\2\10\14\1\0\6\14"+
    "\2\2\2\14\1\0\4\14\2\2\12\14\3\2\2\0\1\2\17\0"+
    "\1\14\1\2\1\14\36\2\33\14\2\0\3\2\60\0\46\2\13\14"+
    "\1\2\u014f\0\3\14\66\2\2\0\1\14\1\2\20\14\2\0\1\2"+
    "\4\14\3\0\12\2\2\14\2\0\12\14\21\0\3\14\1\0\10\2"+
    "\2\0\2\2\2\0\26\2\1\0\7\2\1\0\1\2\3\0\4\2"+
    "\2\0\1\14\1\2\7\14\2\0\2\14\2\0\3\14\11\0\1\14"+
    "\4\0\2\2\1\0\3\2\2\14\2\0\12\14\4\2\15\0\3\14"+
    "\1\0\6\2\4\0\2\2\2\0\26\2\1\0\7\2\1\0\2\2"+
    "\1\0\2\2\1\0\2\2\2\0\1\14\1\0\5\14\4\0\2\14"+
    "\2\0\3\14\13\0\4\2\1\0\1\2\7\0\14\14\3\2\14\0"+
    "\3\14\1\0\11\2\1\0\3\2\1\0\26\2\1\0\7\2\1\0"+
    "\2\2\1\0\5\2\2\0\1\14\1\2\10\14\1\0\3\14\1\0"+
    "\3\14\2\0\1\2\17\0\2\2\2\14\2\0\12\14\1\0\1\2"+
    "\17\0\3\14\1\0\10\2\2\0\2\2\2\0\26\2\1\0\7\2"+
    "\1\0\2\2\1\0\5\2\2\0\1\14\1\2\6\14\3\0\2\14"+
    "\2\0\3\14\10\0\2\14\4\0\2\2\1\0\3\2\4\0\12\14"+
    "\1\0\1\2\20\0\1\14\1\2\1\0\6\2\3\0\3\2\1\0"+
    "\4\2\3\0\2\2\1\0\1\2\1\0\2\2\3\0\2\2\3\0"+
    "\3\2\3\0\10\2\1\0\3\2\4\0\5\14\3\0\3\14\1\0"+
    "\4\14\11\0\1\14\17\0\11\14\11\0\1\2\7\0\3\14\1\0"+
    "\10\2\1\0\3\2\1\0\27\2\1\0\12\2\1\0\5\2\4\0"+
    "\7\14\1\0\3\14\1\0\4\14\7\0\2\14\11\0\2\2\4\0"+
    "\12\14\22\0\2\14\1\0\10\2\1\0\3\2\1\0\27\2\1\0"+
    "\12\2\1\0\5\2\2\0\1\14\1\2\7\14\1\0\3\14\1\0"+
    "\4\14\7\0\2\14\7\0\1\2\1\0\2\2\4\0\12\14\22\0"+
    "\2\14\1\0\10\2\1\0\3\2\1\0\27\2\1\0\20\2\4\0"+
    "\6\14\2\0\3\14\1\0\4\14\11\0\1\14\10\0\2\2\4\0"+
    "\12\14\22\0\2\14\1\0\22\2\3\0\30\2\1\0\11\2\1\0"+
    "\1\2\2\0\7\2\3\0\1\14\4\0\6\14\1\0\1\14\1\0"+
    "\10\14\22\0\2\14\15\0\60\2\1\14\2\2\7\14\4\0\10\2"+
    "\10\14\1\0\12\14\47\0\2\2\1\0\1\2\2\0\2\2\1\0"+
    "\1\2\2\0\1\2\6\0\4\2\1\0\7\2\1\0\3\2\1\0"+
    "\1\2\1\0\1\2\2\0\2\2\1\0\4\2\1\14\2\2\6\14"+
    "\1\0\2\14\1\2\2\0\5\2\1\0\1\2\1\0\6\14\2\0"+
    "\12\14\2\0\2\2\42\0\1\2\27\0\2\14\6\0\12\14\13\0"+
    "\1\14\1\0\1\14\1\0\1\14\4\0\2\14\10\2\1\0\42\2"+
    "\6\0\24\14\1\0\2\14\4\2\4\0\10\14\1\0\44\14\11\0"+
    "\1\14\71\0\42\2\1\0\5\2\1\0\2\2\1\0\7\14\3\0"+
    "\4\14\6\0\12\14\6\0\6\2\4\14\106\0\46\2\12\0\51\2"+
    "\7\0\132\2\5\0\104\2\5\0\122\2\6\0\7\2\1\0\77\2"+
    "\1\0\1\2\1\0\4\2\2\0\7\2\1\0\1\2\1\0\4\2"+
    "\2\0\47\2\1\0\1\2\1\0\4\2\2\0\37\2\1\0\1\2"+
    "\1\0\4\2\2\0\7\2\1\0\1\2\1\0\4\2\2\0\7\2"+
    "\1\0\7\2\1\0\27\2\1\0\37\2\1\0\1\2\1\0\4\2"+
    "\2\0\7\2\1\0\47\2\1\0\23\2\16\0\11\14\56\0\125\2"+
    "\14\0\u026c\2\2\0\10\2\12\0\32\2\5\0\113\2\3\0\3\2"+
    "\17\0\15\2\1\0\4\2\3\14\13\0\22\2\3\14\13\0\22\2"+
    "\2\14\14\0\15\2\1\0\3\2\1\0\2\14\14\0\64\2\40\14"+
    "\3\0\1\2\3\0\2\2\1\14\2\0\12\14\41\0\3\14\2\0"+
    "\12\14\6\0\130\2\10\0\51\2\1\14\126\0\35\2\3\0\14\14"+
    "\4\0\14\14\12\0\12\14\36\2\2\0\5\2\u038b\0\154\2\224\0"+
    "\234\2\4\0\132\2\6\0\26\2\2\0\6\2\2\0\46\2\2\0"+
    "\6\2\2\0\10\2\1\0\1\2\1\0\1\2\1\0\1\2\1\0"+
    "\37\2\2\0\65\2\1\0\7\2\1\0\1\2\3\0\3\2\1\0"+
    "\7\2\3\0\4\2\2\0\6\2\4\0\15\2\5\0\3\2\1\0"+
    "\7\2\17\0\4\14\32\0\5\14\20\0\2\2\23\0\1\2\13\0"+
    "\4\14\6\0\6\14\1\0\1\2\15\0\1\2\40\0\22\2\36\0"+
    "\15\14\4\0\1\14\3\0\6\14\27\0\1\2\4\0\1\2\2\0"+
    "\12\2\1\0\1\2\3\0\5\2\6\0\1\2\1\0\1\2\1\0"+
    "\1\2\1\0\4\2\1\0\3\2\1\0\7\2\3\0\3\2\5\0"+
    "\5\2\26\0\44\2\u0e81\0\3\2\31\0\11\2\6\14\1\0\5\2"+
    "\2\0\5\2\4\0\126\2\2\0\2\14\2\0\3\2\1\0\137\2"+
    "\5\0\50\2\4\0\136\2\21\0\30\2\70\0\20\2\u0200\0\u19b6\2"+
    "\112\0\u51a6\2\132\0\u048d\2\u0773\0\u2ba4\2\u215c\0\u012e\2\2\0\73\2"+
    "\225\0\7\2\14\0\5\2\5\0\1\2\1\14\12\2\1\0\15\2"+
    "\1\0\5\2\1\0\1\2\1\0\2\2\1\0\2\2\1\0\154\2"+
    "\41\0\u016b\2\22\0\100\2\2\0\66\2\50\0\15\2\3\0\20\14"+
    "\20\0\4\14\17\0\2\2\30\0\3\2\31\0\1\2\6\0\5\2"+
    "\1\0\207\2\2\0\1\14\4\0\1\2\13\0\12\14\7\0\32\2"+
    "\4\0\1\2\1\0\32\2\12\0\132\2\3\0\6\2\2\0\6\2"+
    "\2\0\6\2\2\0\3\2\3\0\2\2\3\0\2\2\22\0\3\14"+
    "\4\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\1\4\1\1\1\5\1\6"+
    "\1\1\2\3\1\2\3\3\1\7\13\3\1\10\1\11"+
    "\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21"+
    "\1\22\1\23\1\24\1\25\3\26\1\27\1\30\2\5"+
    "\2\6\1\31\2\3\2\2\24\3\1\32\15\3\1\33"+
    "\1\26\1\27\1\34\1\30\2\31\2\3\1\2\1\0"+
    "\30\3\1\35\1\3\1\36\2\3\1\37\1\40\4\3"+
    "\1\41\4\3\1\0\1\3\1\42\1\0\1\43\1\44"+
    "\6\3\1\45\10\3\1\46\15\3\1\47\4\3\1\50"+
    "\1\30\1\3\1\51\5\3\1\52\4\3\1\53\1\54"+
    "\2\3\1\55\4\3\1\56\10\3\1\57\1\60\3\3"+
    "\1\61\1\3\1\62\1\63\1\64\2\3\1\65\1\66"+
    "\1\67\1\70\1\71\1\72\3\3\1\73\3\3\1\74"+
    "\3\3\1\75\1\76\1\77\5\3\1\100\2\3\1\101"+
    "\1\102\2\3\1\103\1\3\1\104\1\105\1\106\1\107"+
    "\1\110\1\111";

  private static int [] zzUnpackAction() {
    int [] result = new int[264];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\74\0\170\0\264\0\360\0\u012c\0\u0168\0\u01a4"+
    "\0\u01e0\0\u021c\0\u0258\0\u0294\0\u02d0\0\u030c\0\u0348\0\u0384"+
    "\0\u03c0\0\u03fc\0\u0438\0\u0474\0\u04b0\0\u04ec\0\u0528\0\u0564"+
    "\0\u05a0\0\u05dc\0\u0618\0\74\0\74\0\74\0\74\0\74"+
    "\0\74\0\74\0\74\0\74\0\u0654\0\74\0\74\0\74"+
    "\0\74\0\74\0\u0690\0\u06cc\0\u0708\0\u0744\0\74\0\u0780"+
    "\0\u07bc\0\74\0\u07f8\0\u0834\0\u0870\0\u08ac\0\u08e8\0\u0924"+
    "\0\u0960\0\u099c\0\u09d8\0\u0a14\0\u0a50\0\u0a8c\0\u0ac8\0\u0b04"+
    "\0\u0b40\0\u0b7c\0\u0bb8\0\u0bf4\0\u0c30\0\u0c6c\0\u0ca8\0\u0ce4"+
    "\0\u0d20\0\u0d5c\0\u0d98\0\u0dd4\0\u0e10\0\u0e4c\0\u0e88\0\u0ec4"+
    "\0\u0f00\0\u0f3c\0\u0f78\0\u0fb4\0\u0ff0\0\u102c\0\u1068\0\u10a4"+
    "\0\u10e0\0\74\0\u111c\0\u1158\0\u1194\0\u11d0\0\u120c\0\74"+
    "\0\u1248\0\u1284\0\u12c0\0\u12fc\0\u1338\0\u1374\0\u13b0\0\u13ec"+
    "\0\u1428\0\u1464\0\u14a0\0\u14dc\0\u1518\0\u1554\0\u1590\0\u15cc"+
    "\0\u1608\0\u1644\0\u1680\0\u16bc\0\u16f8\0\u1734\0\u1770\0\u17ac"+
    "\0\u17e8\0\u1824\0\u1860\0\u189c\0\u18d8\0\u1914\0\264\0\u1950"+
    "\0\u198c\0\264\0\264\0\u19c8\0\u1a04\0\u1a40\0\u1a7c\0\264"+
    "\0\u1ab8\0\u1af4\0\u1b30\0\u1b6c\0\u1ba8\0\u1be4\0\264\0\u1c20"+
    "\0\264\0\264\0\u1c5c\0\u1c98\0\u1cd4\0\u1d10\0\u1d4c\0\u1d88"+
    "\0\264\0\u1dc4\0\u1e00\0\u1e3c\0\u1e78\0\u1eb4\0\u1ef0\0\u1f2c"+
    "\0\u1f68\0\264\0\u1fa4\0\u1fe0\0\u201c\0\u2058\0\u2094\0\u20d0"+
    "\0\u210c\0\u2148\0\u2184\0\u21c0\0\u21fc\0\u2238\0\u2274\0\264"+
    "\0\u22b0\0\u22ec\0\u2328\0\u2364\0\264\0\74\0\u23a0\0\264"+
    "\0\u23dc\0\u2418\0\u2454\0\u2490\0\u24cc\0\264\0\u2508\0\u2544"+
    "\0\u2580\0\u25bc\0\264\0\264\0\u25f8\0\u2634\0\264\0\u2670"+
    "\0\u26ac\0\u26e8\0\u2724\0\264\0\u2760\0\u279c\0\u27d8\0\u2814"+
    "\0\u2850\0\u288c\0\u28c8\0\u2904\0\264\0\264\0\u2940\0\u297c"+
    "\0\u29b8\0\264\0\u29f4\0\264\0\264\0\264\0\u2a30\0\u2a6c"+
    "\0\264\0\264\0\264\0\264\0\264\0\264\0\u2aa8\0\u2ae4"+
    "\0\u2b20\0\264\0\u2b5c\0\u2b98\0\u2bd4\0\264\0\u2c10\0\u2c4c"+
    "\0\u2c88\0\264\0\264\0\264\0\u2cc4\0\u2d00\0\u2d3c\0\u2d78"+
    "\0\u2db4\0\264\0\u2df0\0\u2e2c\0\264\0\264\0\u2e68\0\u2ea4"+
    "\0\264\0\u2ee0\0\264\0\264\0\264\0\264\0\264\0\264";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[264];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\1\4\1\5\1\6\1\5\1\2\1\7"+
    "\1\2\1\10\1\11\1\5\1\2\1\4\1\12\1\4"+
    "\1\13\1\4\1\14\2\4\1\15\1\4\1\16\1\4"+
    "\1\17\1\4\1\20\1\21\1\4\1\2\1\22\1\4"+
    "\1\23\1\24\1\25\1\26\1\4\1\27\1\30\1\31"+
    "\1\32\1\4\1\33\4\4\1\34\1\35\1\36\1\37"+
    "\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47"+
    "\75\0\1\3\13\0\1\3\2\0\2\50\1\3\4\0"+
    "\2\51\2\52\1\53\2\54\37\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\21\4\17\0\1\5\1\0\1\5"+
    "\5\0\1\5\64\0\1\55\1\0\1\56\65\0\5\7"+
    "\1\0\1\7\1\57\1\60\2\7\1\0\60\7\5\10"+
    "\1\0\2\10\1\61\1\62\1\10\1\0\60\10\11\0"+
    "\1\63\63\0\2\4\11\0\11\4\1\64\5\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\12\4\1\65\6\4\15\0\1\3\13\0"+
    "\1\3\2\0\2\50\1\3\2\66\2\67\2\51\2\52"+
    "\1\53\2\54\37\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\12\4\1\70\1\71\5\4\15\0\2\4\11\0"+
    "\4\4\1\72\12\4\1\0\2\4\1\0\21\4\15\0"+
    "\2\4\11\0\17\4\1\0\1\73\1\4\1\0\12\4"+
    "\1\74\1\75\5\4\15\0\1\53\20\0\1\53\52\0"+
    "\2\4\11\0\7\4\1\76\7\4\1\0\2\4\1\0"+
    "\5\4\1\77\3\4\1\100\7\4\15\0\2\4\11\0"+
    "\2\4\1\101\14\4\1\0\2\4\1\0\2\4\1\102"+
    "\5\4\1\103\10\4\15\0\2\4\11\0\4\4\1\104"+
    "\12\4\1\0\2\4\1\0\3\4\1\105\1\106\1\107"+
    "\13\4\15\0\2\4\11\0\11\4\1\110\5\4\1\0"+
    "\1\111\1\4\1\0\7\4\1\112\5\4\1\113\3\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\5\4"+
    "\1\114\13\4\15\0\2\4\11\0\2\4\1\115\14\4"+
    "\1\0\1\116\1\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\13\4\1\117\5\4\15\0"+
    "\2\4\11\0\17\4\1\0\1\120\1\4\1\0\21\4"+
    "\15\0\2\4\11\0\2\4\1\121\14\4\1\0\2\4"+
    "\1\0\4\4\1\122\5\4\1\123\6\4\15\0\2\4"+
    "\11\0\11\4\1\124\5\4\1\0\2\4\1\0\2\4"+
    "\1\125\6\4\1\126\7\4\15\0\2\4\11\0\4\4"+
    "\1\127\12\4\1\0\2\4\1\0\12\4\1\130\2\4"+
    "\1\131\3\4\105\0\1\132\3\0\1\53\13\0\1\53"+
    "\4\0\1\53\4\0\2\51\2\52\1\0\2\54\37\0"+
    "\1\133\13\0\1\133\4\0\1\133\4\0\2\51\2\52"+
    "\3\0\1\133\35\0\4\134\1\135\1\0\5\134\1\0"+
    "\60\134\6\136\1\0\65\136\5\7\1\0\5\7\1\0"+
    "\60\7\5\10\1\0\5\10\1\0\60\10\5\63\1\0"+
    "\2\63\1\137\1\140\62\63\1\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\3\4\1\141\15\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\5\4\1\142\13\4"+
    "\15\0\1\143\13\0\3\143\2\50\1\143\2\0\6\143"+
    "\1\144\2\143\15\0\1\143\21\0\1\67\13\0\1\67"+
    "\2\0\2\50\1\67\52\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\12\4\1\145\6\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\7\4\1\146\11\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\12\4\1\147"+
    "\6\4\15\0\2\4\11\0\4\4\1\150\12\4\1\0"+
    "\2\4\1\0\14\4\1\151\4\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\2\4\1\152\16\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\5\4\1\153"+
    "\13\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\7\4\1\154\11\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\2\4\1\155\16\4\15\0\2\4\11\0"+
    "\17\4\1\0\1\156\1\4\1\0\21\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\10\4\1\157\10\4"+
    "\15\0\2\4\11\0\11\4\1\160\5\4\1\0\2\4"+
    "\1\0\21\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\4\4\1\161\5\4\1\162\6\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\12\4\1\163\6\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\4\4"+
    "\1\164\10\4\1\165\3\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\5\4\1\166\13\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\3\4\1\167\15\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\13\4"+
    "\1\170\5\4\15\0\2\4\11\0\2\4\1\171\14\4"+
    "\1\0\2\4\1\0\21\4\15\0\2\4\11\0\2\4"+
    "\1\172\14\4\1\0\2\4\1\0\10\4\1\173\10\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\12\4"+
    "\1\174\6\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\7\4\1\175\11\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\16\4\1\176\2\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\20\4\1\177\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\1\200\20\4"+
    "\15\0\2\4\11\0\2\4\1\201\10\4\1\202\3\4"+
    "\1\0\2\4\1\0\21\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\10\4\1\203\10\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\10\4\1\204\10\4"+
    "\15\0\2\4\11\0\4\4\1\205\12\4\1\0\2\4"+
    "\1\0\4\4\1\206\14\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\17\4\1\207\1\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\7\4\1\210\11\4"+
    "\15\0\2\4\11\0\17\4\1\0\1\211\1\4\1\0"+
    "\21\4\15\0\2\4\11\0\2\4\1\212\14\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\5\4\1\213\13\4\15\0\2\4\11\0"+
    "\2\4\1\214\14\4\1\0\2\4\1\0\21\4\15\0"+
    "\1\133\13\0\1\133\4\0\1\133\4\0\2\51\2\52"+
    "\41\0\5\134\1\0\5\134\1\0\60\134\5\135\1\0"+
    "\5\135\1\0\60\135\6\136\1\215\65\136\5\63\1\0"+
    "\5\63\1\0\60\63\1\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\7\4\1\216\11\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\6\4\1\217\12\4\15\0"+
    "\1\143\13\0\3\143\2\50\1\143\2\0\6\143\1\220"+
    "\2\143\1\0\2\54\12\0\1\143\21\0\1\220\13\0"+
    "\3\220\2\0\1\220\2\0\6\220\1\0\2\220\15\0"+
    "\1\220\21\0\2\4\11\0\4\4\1\221\12\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\17\4\1\0"+
    "\1\222\1\4\1\0\21\4\15\0\2\4\11\0\2\4"+
    "\1\223\14\4\1\0\2\4\1\0\21\4\15\0\2\4"+
    "\11\0\17\4\1\0\1\224\1\4\1\0\21\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\4\4\1\225"+
    "\14\4\15\0\2\4\11\0\11\4\1\226\5\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\2\4\1\227"+
    "\14\4\1\0\2\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\1\230\1\4\1\0\21\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\16\4\1\231\2\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\5\4"+
    "\1\232\13\4\15\0\2\4\11\0\2\4\1\233\14\4"+
    "\1\0\2\4\1\0\21\4\15\0\2\4\11\0\4\4"+
    "\1\234\12\4\1\0\2\4\1\0\21\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\11\4\1\235\7\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\7\4"+
    "\1\236\11\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\5\4\1\237\13\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\5\4\1\240\13\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\12\4\1\241\6\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\7\4"+
    "\1\242\11\4\15\0\2\4\11\0\2\4\1\243\14\4"+
    "\1\0\2\4\1\0\21\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\7\4\1\244\11\4\15\0\2\4"+
    "\11\0\4\4\1\245\12\4\1\0\2\4\1\0\21\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\7\4"+
    "\1\246\11\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\2\4\1\247\1\4\1\250\14\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\10\4\1\251\10\4"+
    "\15\0\2\4\11\0\17\4\1\0\1\252\1\4\1\0"+
    "\21\4\15\0\2\4\11\0\17\4\1\0\1\253\1\4"+
    "\1\0\21\4\15\0\2\4\11\0\17\4\1\0\1\254"+
    "\1\4\1\0\21\4\15\0\2\4\11\0\15\4\1\255"+
    "\1\4\1\0\2\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\7\4\1\256\11\4\15\0"+
    "\2\4\11\0\2\4\1\257\14\4\1\0\2\4\1\0"+
    "\21\4\15\0\2\4\11\0\15\4\1\260\1\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\17\4\1\0"+
    "\1\261\1\4\1\0\21\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\10\4\1\262\10\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\3\4\1\263\15\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\3\4"+
    "\1\264\15\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\10\4\1\265\10\4\14\0\4\136\1\266\1\136"+
    "\1\215\65\136\1\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\10\4\1\267\10\4\15\0\1\220\13\0\3\220"+
    "\2\0\1\220\2\0\6\220\1\0\2\220\1\0\2\54"+
    "\12\0\1\220\21\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\7\4\1\270\11\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\6\4\1\271\12\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\16\4\1\272\2\4"+
    "\15\0\2\4\11\0\4\4\1\273\12\4\1\0\2\4"+
    "\1\0\21\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\16\4\1\274\2\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\10\4\1\275\10\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\7\4\1\276\11\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\16\4"+
    "\1\277\2\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\4\4\1\300\14\4\15\0\2\4\11\0\2\4"+
    "\1\301\14\4\1\0\2\4\1\0\21\4\15\0\2\4"+
    "\11\0\17\4\1\0\1\302\1\4\1\0\21\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\6\4\1\303"+
    "\12\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\6\4\1\304\12\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\10\4\1\305\10\4\15\0\2\4\11\0"+
    "\13\4\1\306\3\4\1\0\2\4\1\0\21\4\15\0"+
    "\2\4\11\0\17\4\1\0\1\307\1\4\1\0\21\4"+
    "\15\0\2\4\11\0\17\4\1\0\1\310\1\4\1\0"+
    "\21\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\4\4\1\311\14\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\14\4\1\312\4\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\5\4\1\313\13\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\7\4\1\314"+
    "\11\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\10\4\1\315\10\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\3\4\1\316\15\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\12\4\1\317\6\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\12\4\1\320"+
    "\6\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\2\4\1\321\16\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\7\4\1\322\11\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\14\4\1\323\4\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\10\4\1\324"+
    "\10\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\3\4\1\325\15\4\15\0\2\4\11\0\17\4\1\0"+
    "\2\4\1\0\7\4\1\326\11\4\15\0\2\4\11\0"+
    "\2\4\1\327\14\4\1\0\2\4\1\0\21\4\15\0"+
    "\2\4\11\0\2\4\1\330\14\4\1\0\2\4\1\0"+
    "\21\4\15\0\2\4\11\0\2\4\1\331\14\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\17\4\1\0"+
    "\1\332\1\4\1\0\21\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\4\4\1\333\14\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\5\4\1\334\13\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\3\4"+
    "\1\335\15\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\14\4\1\336\4\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\7\4\1\337\11\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\14\4\1\340\4\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\7\4"+
    "\1\341\11\4\15\0\2\4\11\0\17\4\1\0\1\342"+
    "\1\4\1\0\21\4\15\0\2\4\11\0\15\4\1\343"+
    "\1\4\1\0\2\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\14\4\1\344\4\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\7\4\1\345"+
    "\11\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\6\4\1\346\12\4\15\0\2\4\11\0\13\4\1\347"+
    "\3\4\1\0\2\4\1\0\5\4\1\350\13\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\1\351\20\4"+
    "\15\0\2\4\11\0\13\4\1\352\3\4\1\0\2\4"+
    "\1\0\21\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\5\4\1\353\13\4\15\0\2\4\11\0\2\4"+
    "\1\354\14\4\1\0\2\4\1\0\21\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\4\4\1\355\14\4"+
    "\15\0\2\4\11\0\17\4\1\0\2\4\1\0\7\4"+
    "\1\356\11\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\4\4\1\357\14\4\15\0\2\4\11\0\17\4"+
    "\1\0\2\4\1\0\14\4\1\360\4\4\15\0\2\4"+
    "\11\0\17\4\1\0\2\4\1\0\7\4\1\361\11\4"+
    "\15\0\2\4\11\0\4\4\1\362\12\4\1\0\2\4"+
    "\1\0\21\4\15\0\2\4\11\0\17\4\1\0\2\4"+
    "\1\0\14\4\1\363\4\4\15\0\2\4\11\0\17\4"+
    "\1\0\1\364\1\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\7\4\1\365\11\4\15\0"+
    "\2\4\11\0\2\4\1\366\14\4\1\0\2\4\1\0"+
    "\21\4\15\0\2\4\11\0\2\4\1\367\14\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\2\4\1\370"+
    "\14\4\1\0\2\4\1\0\21\4\15\0\2\4\11\0"+
    "\4\4\1\371\12\4\1\0\2\4\1\0\21\4\15\0"+
    "\2\4\11\0\4\4\1\372\12\4\1\0\2\4\1\0"+
    "\21\4\15\0\2\4\11\0\4\4\1\373\12\4\1\0"+
    "\2\4\1\0\21\4\15\0\2\4\11\0\15\4\1\374"+
    "\1\4\1\0\2\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\7\4\1\375\11\4\15\0"+
    "\2\4\11\0\17\4\1\0\1\376\1\4\1\0\21\4"+
    "\15\0\2\4\11\0\17\4\1\0\1\377\1\4\1\0"+
    "\21\4\15\0\2\4\11\0\17\4\1\0\2\4\1\0"+
    "\14\4\1\u0100\4\4\15\0\2\4\11\0\4\4\1\u0101"+
    "\12\4\1\0\2\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\2\4\1\0\14\4\1\u0102\4\4\15\0"+
    "\2\4\11\0\17\4\1\0\2\4\1\0\13\4\1\u0103"+
    "\5\4\15\0\2\4\11\0\17\4\1\0\1\u0104\1\4"+
    "\1\0\21\4\15\0\2\4\11\0\17\4\1\0\1\u0105"+
    "\1\4\1\0\21\4\15\0\2\4\11\0\15\4\1\u0106"+
    "\1\4\1\0\2\4\1\0\21\4\15\0\2\4\11\0"+
    "\17\4\1\0\1\u0107\1\4\1\0\21\4\15\0\2\4"+
    "\11\0\17\4\1\0\1\u0108\1\4\1\0\21\4\14\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[12060];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;
  private static final char[] EMPTY_BUFFER = new char[0];
  private static final int YYEOF = -1;
  private static java.io.Reader zzReader = null; // Fake

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\31\1\11\11\1\1\5\11\4\1\1\11"+
    "\2\1\1\11\47\1\1\11\5\1\1\11\3\1\1\0"+
    "\50\1\1\0\2\1\1\0\45\1\1\11\122\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[264];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** this buffer may contains the current text array to be matched when it is cheap to acquire it */
  private char[] zzBufferArray;

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;


  _CSharpLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  _CSharpLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 1782) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart(){
    return zzStartRead;
  }

  public final int getTokenEnd(){
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end,int initialState){
    zzBuffer = buffer;
    zzBufferArray = com.intellij.util.text.CharArrayUtil.fromSequenceWithoutCopying(buffer);
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzPushbackPos = 0;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBufferArray != null ? zzBufferArray[zzStartRead+pos]:zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
    
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;
    char[] zzBufferArrayL = zzBufferArray;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL)
            zzInput = zzBufferL.charAt(zzCurrentPosL++);
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = zzBufferL.charAt(zzCurrentPosL++);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 65: 
          { return CSharpTokens.ABSTRACT_KEYWORD;
          }
        case 74: break;
        case 38: 
          { return CSharpTokens.UINT_KEYWORD;
          }
        case 75: break;
        case 6: 
          { return CSharpTokens.STRING_LITERAL;
          }
        case 76: break;
        case 12: 
          { return CSharpTokens.LPAR;
          }
        case 77: break;
        case 1: 
          { return CSharpTokens.BAD_CHARACTER;
          }
        case 78: break;
        case 67: 
          { return CSharpTokens.INTERNAL_KEYWORD;
          }
        case 79: break;
        case 23: 
          { return CSharpTokens.LINE_COMMENT;
          }
        case 80: break;
        case 70: 
          { return CSharpTokens.OVERRIDE_KEYWORD;
          }
        case 81: break;
        case 42: 
          { return CSharpTokens.EVENT_KEYWORD;
          }
        case 82: break;
        case 44: 
          { return CSharpTokens.USING_KEYWORD;
          }
        case 83: break;
        case 41: 
          { return CSharpTokens.FLOAT_KEYWORD;
          }
        case 84: break;
        case 17: 
          { return CSharpTokens.COLON;
          }
        case 85: break;
        case 40: 
          { return CSharpTokens.CHAR_KEYWORD;
          }
        case 86: break;
        case 63: 
          { return CSharpTokens.PRIVATE_KEYWORD;
          }
        case 87: break;
        case 53: 
          { return CSharpTokens.USHORT_KEYWORD;
          }
        case 88: break;
        case 51: 
          { return CSharpTokens.PARAMS_KEYWORD;
          }
        case 89: break;
        case 27: 
          { return CSharpTokens.COLONCOLON;
          }
        case 90: break;
        case 64: 
          { return CSharpTokens.VIRTUAL_KEYWORD;
          }
        case 91: break;
        case 3: 
          { return CSharpTokens.IDENTIFIER;
          }
        case 92: break;
        case 14: 
          { return CSharpTokens.LT;
          }
        case 93: break;
        case 58: 
          { return CSharpTokens.STRING_KEYWORD;
          }
        case 94: break;
        case 4: 
          { return CSharpTokens.WHITE_SPACE;
          }
        case 95: break;
        case 26: 
          { return CSharpTokens.IN_KEYWORD;
          }
        case 96: break;
        case 49: 
          { return CSharpTokens.DOUBLE_KEYWORD;
          }
        case 97: break;
        case 32: 
          { return CSharpTokens.VAR_KEYWORD;
          }
        case 98: break;
        case 5: 
          { return CSharpTokens.CHARACTER_LITERAL;
          }
        case 99: break;
        case 19: 
          { return CSharpTokens.COMMA;
          }
        case 100: break;
        case 66: 
          { return CSharpTokens.DELEGATE_KEYWORD;
          }
        case 101: break;
        case 36: 
          { return CSharpTokens.BYTE_KEYWORD;
          }
        case 102: break;
        case 39: 
          { return CSharpTokens.VOID_KEYWORD;
          }
        case 103: break;
        case 29: 
          { return CSharpTokens.INT_KEYWORD;
          }
        case 104: break;
        case 61: 
          { return CSharpTokens.DECIMAL_KEYWORD;
          }
        case 105: break;
        case 9: 
          { return CSharpTokens.RBRACE;
          }
        case 106: break;
        case 8: 
          { return CSharpTokens.LBRACE;
          }
        case 107: break;
        case 15: 
          { return CSharpTokens.GT;
          }
        case 108: break;
        case 55: 
          { return CSharpTokens.SEALED_KEYWORD;
          }
        case 109: break;
        case 2: 
          { return CSharpTokens.INTEGER_LITERAL;
          }
        case 110: break;
        case 7: 
          { return CSharpTokens.DOT;
          }
        case 111: break;
        case 31: 
          { return CSharpTokens.REF_KEYWORD;
          }
        case 112: break;
        case 25: 
          { return CSharpTokens.VERBATIM_STRING_LITERAL;
          }
        case 113: break;
        case 50: 
          { return CSharpTokens.EXTERN_KEYWORD;
          }
        case 114: break;
        case 20: 
          { return CSharpTokens.LONG_LITERAL;
          }
        case 115: break;
        case 33: 
          { return CSharpTokens.OUT_KEYWORD;
          }
        case 116: break;
        case 68: 
          { return CSharpTokens.READONLY_KEYWORD;
          }
        case 117: break;
        case 30: 
          { return CSharpTokens.NEW_KEYWORD;
          }
        case 118: break;
        case 18: 
          { return CSharpTokens.SEMICOLON;
          }
        case 119: break;
        case 45: 
          { return CSharpTokens.SBYTE_KEYWORD;
          }
        case 120: break;
        case 24: 
          { return CSharpTokens.BLOCK_COMMENT;
          }
        case 121: break;
        case 52: 
          { return CSharpTokens.PUBLIC_KEYWORD;
          }
        case 122: break;
        case 57: 
          { return CSharpTokens.STRUCT_KEYWORD;
          }
        case 123: break;
        case 54: 
          { return CSharpTokens.UNSAFE_KEYWORD;
          }
        case 124: break;
        case 37: 
          { return CSharpTokens.ENUM_KEYWORD;
          }
        case 125: break;
        case 71: 
          { return CSharpTokens.PROTECTED_KEYWORD;
          }
        case 126: break;
        case 11: 
          { return CSharpTokens.RBRACKET;
          }
        case 127: break;
        case 48: 
          { return CSharpTokens.CONST_KEYWORD;
          }
        case 128: break;
        case 60: 
          { return CSharpTokens.OBJECT_KEYWORD;
          }
        case 129: break;
        case 35: 
          { return CSharpTokens.BOOL_KEYWORD;
          }
        case 130: break;
        case 62: 
          { return CSharpTokens.DYNAMIC_KEYWORD;
          }
        case 131: break;
        case 43: 
          { return CSharpTokens.ULONG_KEYWORD;
          }
        case 132: break;
        case 73: 
          { return CSharpTokens.NAMESPACE_KEYWORD;
          }
        case 133: break;
        case 21: 
          { return CSharpTokens.FLOAT_LITERAL;
          }
        case 134: break;
        case 69: 
          { return CSharpTokens.VOLATILE_KEYWORD;
          }
        case 135: break;
        case 34: 
          { return CSharpTokens.LONG_KEYWORD;
          }
        case 136: break;
        case 46: 
          { return CSharpTokens.SHORT_KEYWORD;
          }
        case 137: break;
        case 56: 
          { return CSharpTokens.STATIC_KEYWORD;
          }
        case 138: break;
        case 72: 
          { return CSharpTokens.INTERFACE_KEYWORD;
          }
        case 139: break;
        case 13: 
          { return CSharpTokens.RPAR;
          }
        case 140: break;
        case 10: 
          { return CSharpTokens.LBRACKET;
          }
        case 141: break;
        case 28: 
          { return CSharpTokens.LINE_DOC_COMMENT;
          }
        case 142: break;
        case 47: 
          { return CSharpTokens.CLASS_KEYWORD;
          }
        case 143: break;
        case 22: 
          { return CSharpTokens.DOUBLE_LITERAL;
          }
        case 144: break;
        case 59: 
          { return CSharpTokens.TYPEOF_KEYWORD;
          }
        case 145: break;
        case 16: 
          { return CSharpTokens.EQ;
          }
        case 146: break;
        default:
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
            return null;
          }
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
