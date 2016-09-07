package consulo.csharp.cfs.ide.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

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
