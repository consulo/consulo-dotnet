/*
 * Copyright 2013-2017 consulo.io
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

package consulo.dotnet.debugger.nodes.valueRender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;

/**
 * @author VISTALL
 * @since 22-Oct-17
 */
public class DotNetValueTextRendererProxy implements XValuePresentation.XValueTextRenderer
{
	enum Type
	{
		value,
		stringValue,
		numericValue,
		charValue,
		keywordValue,
		comment,
		specialSymbol,
		error
	}

	private Type myType;
	private String myValue;

	public void renderBack(@Nonnull XValuePresentation.XValueTextRenderer renderer)
	{
		// not rendered
		if(myType == null)
		{
			return;
		}

		switch(myType)
		{
			case value:
				renderer.renderValue(myValue);
				break;
			case stringValue:
				renderer.renderStringValue(myValue);
				break;
			case numericValue:
				renderer.renderNumericValue(myValue);
				break;
			case charValue:
				renderer.renderCharValue(myValue);
				break;
			case keywordValue:
				renderer.renderKeywordValue(myValue);
				break;
			case comment:
				renderer.renderComment(myValue);
				break;
			case specialSymbol:
				renderer.renderSpecialSymbol(myValue);
				break;
			case error:
				renderer.renderError(myValue);
				break;
		}
	}

	private void set(Type type, String value)
	{
		assert myValue == null;

		myType = type;
		myValue = value;
	}

	@Override
	public void renderValue(@Nonnull String value)
	{
		set(Type.value, value);
	}

	@Override
	public void renderStringValue(@Nonnull String value)
	{
		set(Type.stringValue, value);
	}

	@Override
	public void renderNumericValue(@Nonnull String value)
	{
		set(Type.numericValue, value);
	}

	@Override
	public void renderCharValue(@Nonnull String value)
	{
		set(Type.charValue, value);
	}

	@Override
	public void renderKeywordValue(@Nonnull String value)
	{
		set(Type.keywordValue, value);
	}

	@Override
	public void renderValue(@Nonnull String value, @Nonnull TextAttributesKey key)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void renderStringValue(@Nonnull String value, @Nullable String additionalSpecialCharsToHighlight, int maxLength)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void renderStringValue(@Nonnull String value, @Nullable String additionalSpecialCharsToHighlight, char quoteChar, int maxLength)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void renderComment(@Nonnull String comment)
	{
		set(Type.comment, comment);
	}

	@Override
	public void renderSpecialSymbol(@Nonnull String symbol)
	{
		set(Type.specialSymbol, symbol);
	}

	@Override
	public void renderError(@Nonnull String error)
	{
		set(Type.error, error);
	}
}
