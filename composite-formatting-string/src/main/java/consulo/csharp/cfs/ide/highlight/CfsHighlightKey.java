package consulo.csharp.cfs.ide.highlight;

import consulo.colorScheme.TextAttributesKey;
import consulo.language.editor.DefaultLanguageHighlighterColors;

/**
 * @author VISTALL
 * @since 25.03.2015
 */
public interface CfsHighlightKey
{
	TextAttributesKey INDEX = TextAttributesKey.createTextAttributesKey("CFS_INDEX", DefaultLanguageHighlighterColors.NUMBER);
	TextAttributesKey ALIGN = TextAttributesKey.createTextAttributesKey("CFS_ALIGN", DefaultLanguageHighlighterColors.NUMBER);
	TextAttributesKey FORMAT = TextAttributesKey.createTextAttributesKey("CFS_FORMAT", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
}
