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

package consulo.dotnet.debugger.impl.nodes.valueRender;

import consulo.colorScheme.TextAttributesKey;
import consulo.execution.debug.frame.presentation.XValuePresentation;
import consulo.localize.LocalizeValue;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 22-Oct-17
 */
public class DotNetValueTextRendererProxy implements XValuePresentation.XValueTextRenderer {
    enum Type {
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
    private Object myValue;

    public void renderBack(XValuePresentation.XValueTextRenderer renderer) {
        // not rendered
        if (myType == null) {
            return;
        }

        switch (myType) {
            case value:
                renderer.renderValue((String) myValue);
                break;
            case stringValue:
                renderer.renderStringValue((String) myValue);
                break;
            case numericValue:
                renderer.renderNumericValue((String) myValue);
                break;
            case charValue:
                renderer.renderCharValue((String) myValue);
                break;
            case keywordValue:
                renderer.renderKeywordValue((String) myValue);
                break;
            case comment:
                renderer.renderComment((String) myValue);
                break;
            case specialSymbol:
                renderer.renderSpecialSymbol((String) myValue);
                break;
            case error:
                renderer.renderError((LocalizeValue) myValue);
                break;
        }
    }

    private void set(Type type, Object value) {
        assert myValue == null;

        myType = type;
        myValue = value;
    }

    @Override
    public void renderValue(String value) {
        set(Type.value, value);
    }

    @Override
    public void renderStringValue(String value) {
        set(Type.stringValue, value);
    }

    @Override
    public void renderNumericValue(String value) {
        set(Type.numericValue, value);
    }

    @Override
    public void renderCharValue(String value) {
        set(Type.charValue, value);
    }

    @Override
    public void renderKeywordValue(String value) {
        set(Type.keywordValue, value);
    }

    @Override
    public void renderValue(String value, TextAttributesKey key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renderStringValue(String value, @Nullable String additionalSpecialCharsToHighlight, int maxLength) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renderStringValue(String value, @Nullable String additionalSpecialCharsToHighlight, char quoteChar, int maxLength) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void renderComment(String comment) {
        set(Type.comment, comment);
    }

    @Override
    public void renderSpecialSymbol(String symbol) {
        set(Type.specialSymbol, symbol);
    }

    @Override
    public void renderError(LocalizeValue error) {
        set(Type.error, error);
    }
}
