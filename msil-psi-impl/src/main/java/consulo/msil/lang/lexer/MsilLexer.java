package consulo.msil.lang.lexer;

import javax.annotation.Nonnull;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.psi.tree.IElementType;
import consulo.msil.lang.psi.MsilTokenSets;
import consulo.msil.lang.psi.MsilTokens;

/**
 * @author VISTALL
 * @since 18.05.2015
 */
public class MsilLexer extends LookAheadLexer
{
	private static final int NONE = 0;
	private static final int ATTRIBUTE = 1;
	private static final int WAIT_LPAR = 2;
	private static final int SIGNATURE = 3;

	private int myState = NONE;

	public MsilLexer()
	{
		super(new _MsilLexer());
	}

	@Override
	protected void lookAhead(Lexer baseLexer)
	{
		IElementType tokenType = baseLexer.getTokenType();

		switch(myState)
		{
			case NONE:
				if(tokenType == MsilTokens._CUSTOM_KEYWORD)
				{
					myState = ATTRIBUTE;
				}
				break;
			case ATTRIBUTE:
				if(tokenType == MsilTokens.EQ)
				{
					myState = WAIT_LPAR;
				}
				break;
			case WAIT_LPAR:
				if(tokenType == MsilTokens.LPAR)
				{
					myState = SIGNATURE;
				}
				else if(!canSkipElement(baseLexer))
				{
					myState = NONE;
				}
				break;
			case SIGNATURE:
				int tokenLength = baseLexer.getTokenEnd() - baseLexer.getTokenStart();
				if(tokenType == MsilTokens.IDENTIFIER)
				{
					if(tokenLength == 2)
					{
						advanceAs(baseLexer, MsilTokens.HEX_NUMBER_LITERAL);
						return;
					}
				}
				else if(tokenType == MsilTokens.NUMBER_LITERAL)
				{
					// for example 1A = NUMBER IDENTIFIER
					if(tokenLength == 1)
					{
						int tokenStart = baseLexer.getTokenStart();

						LexerPosition currentPosition = baseLexer.getCurrentPosition();
						baseLexer.advance();

						IElementType nextToken = baseLexer.getTokenType();
						CharSequence nextSequence = baseLexer.getTokenSequence();
						baseLexer.restore(currentPosition);

						// if next token is identifier and length = 1, advance it
						if(nextToken == MsilTokens.IDENTIFIER && nextSequence.length() == 1)
						{
							baseLexer.advance();
							baseLexer.advance();

							addToken(tokenStart + 2, MsilTokens.HEX_NUMBER_LITERAL);
							return;
						}
					}
					else if(tokenLength == 2)
					{
						advanceAs(baseLexer, MsilTokens.HEX_NUMBER_LITERAL);
						return;
					}
				}
				else if(!canSkipElement(baseLexer))
				{
					myState = NONE;
				}
				break;
		}

		super.lookAhead(baseLexer);
	}


	private boolean canSkipElement(@Nonnull Lexer baseLexer)
	{
		IElementType tokenType = baseLexer.getTokenType();
		return MsilTokenSets.WHITESPACES.contains(tokenType) || MsilTokenSets.COMMENTS.contains(tokenType);
	}
}
