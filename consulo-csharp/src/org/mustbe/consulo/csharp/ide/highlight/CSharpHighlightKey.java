package org.mustbe.consulo.csharp.ide.highlight;

import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface CSharpHighlightKey
{
	TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.STRING);
	TextAttributesKey CLASS_NAME = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.CLASS_NAME);
	TextAttributesKey GENERIC_PARAMETER_NAME = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.TYPE_ALIAS_NAME);
	TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE, DefaultLanguageHighlighterColors.KEYWORD);
	TextAttributesKey SOFT_KEYWORD = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors./*TODO [VISTALL] temp usage KEYWORD*/ LABEL);
	TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.BLOCK_COMMENT);
	TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(CSharpLanguage.INSTANCE,
			DefaultLanguageHighlighterColors.LINE_COMMENT);
}
