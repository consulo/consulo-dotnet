/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.csharp.cfs.lang.lexer;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.cfs.lang.CfsTokens;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public abstract class BaseCfsLexer extends LexerBase implements CfsTokens
{
	private static final int TEXT_LAYER = 0;
	private static final int ARGUMENT_WAIT = 1;
	private static final int ALIGN_OR_FORMAT_OR_STOP = 2;
	private static final int ALIGN_WAIT = 3;
	private static final int FORMAT_WAIT = 5;
	private static final int FORMAT_OR_STOP = 6;

	protected CharSequence myBuffer = "";
	protected int myIndex = 0;
	protected int myStopIndex = 0;
	protected int myBufferEnd = 1;

	protected int myState = TEXT_LAYER;

	protected IElementType myCache = null;

	@Override
	public void start(CharSequence buffer, int startOffset, int endOffset, int initialState)
	{
		myBuffer = buffer.subSequence(startOffset, endOffset);
		myIndex = 0;
		myState = initialState;
		myBufferEnd = myBuffer.length();
	}

	@Override
	public int getState()
	{
		return myState;
	}

	@Override
	public IElementType getTokenType()
	{
		if(myCache != null)
		{
			return myCache;
		}
		return myCache = getTokenTypeImpl();
	}

	private IElementType getTokenTypeImpl()
	{
		if(myIndex >= myBufferEnd)
		{
			return null;
		}

		char c = myBuffer.charAt(myIndex);

		switch(myState)
		{
			default:
			case TEXT_LAYER:
				if(c == '{')
				{
					myStopIndex = myIndex + 1;
					myState = ARGUMENT_WAIT;
					return START;
				}
				else
				{
					myStopIndex = myIndex + 1;
					return TEXT;
				}
			case ARGUMENT_WAIT:
				return stepArgument(c, INDEX, ALIGN_OR_FORMAT_OR_STOP);
			case ALIGN_OR_FORMAT_OR_STOP:
				if(c == '}')
				{
					myStopIndex = myIndex + 1;
					myState = TEXT_LAYER;
					return END;
				}
				else if(c == ',')
				{
					myStopIndex = myIndex + 1;
					myState = ALIGN_WAIT;
					return COMMA;
				}
				else if(c == ':')
				{
					myStopIndex = myIndex + 1;
					myState = FORMAT_OR_STOP;
					return COLON;
				}
				else
				{
					myStopIndex = myIndex + 1;
					myState = ALIGN_OR_FORMAT_OR_STOP;
					return TEXT;
				}
			case ALIGN_WAIT:
				return stepNumber(c, ALIGN, FORMAT_WAIT);
			case FORMAT_WAIT:
				if(c == '}')
				{
					myState = TEXT_LAYER;
					myStopIndex = myIndex + 1;
					return END;
				}
				else if(c == ':')
				{
					myStopIndex = myIndex + 1;
					myState = FORMAT_OR_STOP;
					return COLON;
				}
				else
				{
					myStopIndex = myIndex + 1;
					myState = FORMAT_OR_STOP;
					return TEXT;
				}
			case FORMAT_OR_STOP:
				if(c == '}')
				{
					myState = TEXT_LAYER;
					myStopIndex = myIndex + 1;
					return END;
				}
				else if(c == ':')
				{
					myStopIndex = myIndex + 1;
					myState = FORMAT_OR_STOP;
					return COLON;
				}
				else
				{
					int i = 0;
					while(true)
					{
						int newIndex = myIndex + i;
						if(newIndex >= myBufferEnd)
						{
							break;
						}

						c = myBuffer.charAt(newIndex);
						if(c != '}')
						{
							i++;
						}
						else
						{
							break;
						}
					}

					myState = FORMAT_OR_STOP;
					myStopIndex = myIndex + i;
					return FORMAT;
				}
		}
	}

	@NotNull
	public abstract IElementType stepArgument(char c, IElementType to, int newState);

	@NotNull
	protected IElementType stepNumber(char c, IElementType to, int newState)
	{
		boolean minus = false;
		int i = 0;
		if(c == '-')
		{
			i ++;
			minus = true;
		}

		while(true)
		{
			int newIndex = myIndex + i;
			if(newIndex >= myBufferEnd)
			{
				break;
			}

			c = myBuffer.charAt(newIndex);
			if(Character.isDigit(c))
			{
				i++;
			}
			else
			{
				break;
			}
		}

		if(minus && i == 1 || !minus && i == 0)
		{
			myStopIndex = myIndex + 1;
			return TEXT;
		}
		else
		{
			myStopIndex = myIndex + i;
			myState = newState;
			return to;
		}
	}

	@Override
	public int getTokenStart()
	{
		return myIndex;
	}

	@Override
	public int getTokenEnd()
	{
		getTokenType();
		return myStopIndex;
	}

	@Override
	public void advance()
	{
		if(myIndex < myBufferEnd)
		{
			int i = getTokenEnd() - getTokenStart();

			myIndex += i;

			myCache = null;

			getTokenType();
		}
	}

	@Override
	public CharSequence getBufferSequence()
	{
		return myBuffer;
	}

	@Override
	public int getBufferEnd()
	{
		return myBufferEnd;
	}
}
