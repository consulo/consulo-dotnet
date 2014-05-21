/* The following code was generated by JFlex 1.4.3 on 21.05.14 16:17 */

package org.mustbe.consulo.msil.lang.lexer;

import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 21.05.14 16:17 from the specification file
 * <tt>H:/github.com/consulo/consulo-dotnet/msil-psi-impl/src/org/mustbe/consulo/msil/lang/lexer/_MsilLexer.flex</tt>
 */
public class _MsilLexer implements FlexLexer {
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
    "\11\0\1\1\1\3\1\0\1\1\1\3\22\0\1\1\5\0\1\7"+
    "\3\0\1\4\1\0\1\10\1\0\1\11\1\2\1\0\1\36\1\41"+
    "\1\40\1\42\1\0\1\37\1\0\1\35\50\0\1\14\1\34\1\12"+
    "\1\24\1\22\1\25\1\43\1\23\1\26\1\44\1\0\1\13\1\21"+
    "\1\33\1\20\1\27\1\0\1\30\1\15\1\17\1\16\1\32\1\0"+
    "\1\45\1\31\1\0\1\5\1\0\1\6\uff82\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\1\1\3\1\4\1\5\1\6"+
    "\1\7\12\1\1\10\1\11\44\0\1\11\16\0\1\12"+
    "\4\0\1\13\1\14\7\0\1\15\1\0\1\16\5\0"+
    "\1\17\1\0\1\20\1\21\1\22\1\0\1\23\3\0"+
    "\1\24\1\25\1\0\1\26\1\27\1\30\1\31\1\32"+
    "\4\0\1\33\1\0\1\34\1\0\1\35\1\36\6\0"+
    "\1\37\1\40\1\0\1\41\1\42";

  private static int [] zzUnpackAction() {
    int [] result = new int[133];
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
    "\0\0\0\46\0\114\0\162\0\46\0\46\0\46\0\46"+
    "\0\46\0\230\0\276\0\344\0\u010a\0\u0130\0\u0156\0\u017c"+
    "\0\u01a2\0\u01c8\0\u01ee\0\u0214\0\u023a\0\u0260\0\u0286\0\u02ac"+
    "\0\u02d2\0\u02f8\0\u031e\0\u0344\0\u036a\0\u0390\0\u03b6\0\u03dc"+
    "\0\u0402\0\u0428\0\u044e\0\u0474\0\u049a\0\u04c0\0\u04e6\0\u050c"+
    "\0\u0532\0\u0558\0\u057e\0\u05a4\0\u05ca\0\u05f0\0\u0616\0\u063c"+
    "\0\u0662\0\u0688\0\u06ae\0\u06d4\0\u06fa\0\u0720\0\u0746\0\u076c"+
    "\0\u0792\0\46\0\u07b8\0\u07de\0\u0804\0\u082a\0\u0850\0\u0876"+
    "\0\u089c\0\u08c2\0\u08e8\0\u090e\0\u0934\0\u095a\0\u0980\0\u09a6"+
    "\0\46\0\u09cc\0\u09f2\0\u0a18\0\u0a3e\0\46\0\46\0\u0a64"+
    "\0\u0a8a\0\u0ab0\0\u0ad6\0\u0afc\0\u0b22\0\u0b48\0\46\0\u0b6e"+
    "\0\46\0\u0b94\0\u0bba\0\u0be0\0\u0c06\0\u0c2c\0\u0c52\0\u0c78"+
    "\0\46\0\46\0\46\0\u0c9e\0\46\0\u0cc4\0\u0cea\0\u0d10"+
    "\0\46\0\46\0\u0d36\0\46\0\46\0\46\0\46\0\46"+
    "\0\u0d5c\0\u0d82\0\u0da8\0\u0dce\0\46\0\u0df4\0\46\0\u0e1a"+
    "\0\46\0\46\0\u0e40\0\u0e66\0\u0e8c\0\u0eb2\0\u0ed8\0\u0efe"+
    "\0\46\0\46\0\u0f24\0\46\0\46";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[133];
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
    "\1\2\1\3\1\4\1\3\1\5\1\6\1\7\1\10"+
    "\1\11\1\12\1\13\2\2\1\14\1\15\1\2\1\16"+
    "\1\2\1\17\2\2\1\20\1\21\3\2\1\22\1\2"+
    "\1\23\11\2\47\0\1\3\1\0\1\3\44\0\1\24"+
    "\1\0\1\25\53\0\1\26\1\0\1\27\4\0\1\30"+
    "\1\31\2\0\1\32\1\0\1\33\31\0\1\34\51\0"+
    "\1\35\54\0\1\36\53\0\1\37\56\0\1\40\13\0"+
    "\1\41\53\0\1\42\11\0\1\43\26\0\1\44\3\0"+
    "\1\45\45\0\1\46\25\0\3\24\1\0\42\24\4\25"+
    "\1\47\41\25\13\0\1\50\2\0\1\51\44\0\1\52"+
    "\52\0\1\53\55\0\1\54\41\0\1\55\47\0\1\56"+
    "\31\0\1\57\61\0\1\60\50\0\1\61\56\0\1\62"+
    "\20\0\1\63\46\0\1\64\54\0\1\65\35\0\1\66"+
    "\41\0\1\67\60\0\1\70\37\0\1\71\25\0\2\25"+
    "\1\72\1\25\1\47\41\25\14\0\1\73\46\0\1\74"+
    "\45\0\1\75\47\0\1\76\50\0\1\77\45\0\1\100"+
    "\43\0\1\101\42\0\1\102\56\0\1\103\36\0\1\104"+
    "\50\0\1\105\45\0\1\106\37\0\1\107\44\0\1\110"+
    "\67\0\1\111\1\112\1\113\1\114\23\0\1\115\53\0"+
    "\1\116\34\0\1\117\47\0\1\120\47\0\1\121\50\0"+
    "\1\122\46\0\1\123\55\0\1\124\25\0\1\125\61\0"+
    "\1\126\33\0\1\127\63\0\1\130\47\0\1\131\1\132"+
    "\1\133\1\134\17\0\1\135\66\0\1\136\31\0\1\137"+
    "\50\0\1\140\62\0\1\141\50\0\1\142\44\0\1\143"+
    "\26\0\1\144\40\0\1\145\50\0\1\146\46\0\1\147"+
    "\44\0\1\150\44\0\1\151\52\0\1\152\43\0\1\153"+
    "\66\0\1\154\41\0\1\155\50\0\1\156\44\0\1\157"+
    "\23\0\1\160\52\0\1\161\60\0\1\162\27\0\1\163"+
    "\43\0\1\164\47\0\1\165\60\0\1\166\35\0\1\167"+
    "\51\0\1\170\32\0\1\171\72\0\1\172\25\0\1\173"+
    "\54\0\1\174\27\0\1\175\51\0\1\176\61\0\1\177"+
    "\41\0\1\200\47\0\1\201\45\0\1\202\33\0\1\203"+
    "\50\0\1\204\40\0\1\205\30\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[3914];
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
    "\1\0\1\11\2\1\5\11\14\1\44\0\1\11\16\0"+
    "\1\11\4\0\2\11\7\0\1\11\1\0\1\11\5\0"+
    "\1\1\1\0\3\11\1\0\1\11\3\0\2\11\1\0"+
    "\5\11\4\0\1\11\1\0\1\11\1\0\2\11\6\0"+
    "\2\11\1\0\2\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[133];
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


  public _MsilLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public _MsilLexer(java.io.InputStream in) {
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
    while (i < 114) {
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
        case 13: 
          { return MsilTokens.CLASS_KEYWORD;
          }
        case 35: break;
        case 3: 
          { return MsilTokens.PERC;
          }
        case 36: break;
        case 33: 
          { return MsilTokens.VALUETYPE_KEYWORD;
          }
        case 37: break;
        case 28: 
          { return MsilTokens._METHOD_KEYWORD;
          }
        case 38: break;
        case 11: 
          { return MsilTokens.VOID_KEYWORD;
          }
        case 39: break;
        case 31: 
          { return MsilTokens._ASSEMBLY_KEYWORD;
          }
        case 40: break;
        case 22: 
          { return MsilTokens.STRING_KEYWORD;
          }
        case 41: break;
        case 24: 
          { return MsilTokens.UINT64_KEYWORD;
          }
        case 42: break;
        case 19: 
          { return MsilTokens._CLASS_KEYWORD;
          }
        case 43: break;
        case 1: 
          { return com.intellij.psi.TokenType.BAD_CHARACTER;
          }
        case 44: break;
        case 2: 
          { return MsilTokens.WHITE_SPACE;
          }
        case 45: break;
        case 12: 
          { return MsilTokens.BOOL_KEYWORD;
          }
        case 46: break;
        case 34: 
          { return MsilTokens.IMPLEMENTS_KEYWORD;
          }
        case 47: break;
        case 15: 
          { return MsilTokens.FLOAT_KEYWORD;
          }
        case 48: break;
        case 21: 
          { return MsilTokens._FIELD_KEYWORD;
          }
        case 49: break;
        case 17: 
          { return MsilTokens.INT64_KEYWORD;
          }
        case 50: break;
        case 9: 
          { return MsilTokens.BLOCK_COMMENT;
          }
        case 51: break;
        case 27: 
          { return MsilTokens._CUSTOM_KEYWORD;
          }
        case 52: break;
        case 8: 
          { return MsilTokens.LINE_COMMENT;
          }
        case 53: break;
        case 6: 
          { return MsilTokens.AND;
          }
        case 54: break;
        case 26: 
          { return MsilTokens.OBJECT_KEYWORD;
          }
        case 55: break;
        case 23: 
          { return MsilTokens.UINT16_KEYWORD;
          }
        case 56: break;
        case 29: 
          { return MsilTokens.EXTENDS_KEYWORD;
          }
        case 57: break;
        case 18: 
          { return MsilTokens.INT32_KEYWORD;
          }
        case 58: break;
        case 20: 
          { return MsilTokens._EVENT_KEYWORD;
          }
        case 59: break;
        case 32: 
          { return MsilTokens._PROPERTY_KEYWORD;
          }
        case 60: break;
        case 7: 
          { return MsilTokens.COMMA;
          }
        case 61: break;
        case 10: 
          { return MsilTokens.INT8_KEYWORD;
          }
        case 62: break;
        case 14: 
          { return MsilTokens.UINT8_KEYWORD;
          }
        case 63: break;
        case 4: 
          { return MsilTokens.LBRACE;
          }
        case 64: break;
        case 30: 
          { return MsilTokens.FLOAT64_KEYWORD;
          }
        case 65: break;
        case 25: 
          { return MsilTokens.UINT32_KEYWORD;
          }
        case 66: break;
        case 5: 
          { return MsilTokens.RBRACE;
          }
        case 67: break;
        case 16: 
          { return MsilTokens.INT16_KEYWORD;
          }
        case 68: break;
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