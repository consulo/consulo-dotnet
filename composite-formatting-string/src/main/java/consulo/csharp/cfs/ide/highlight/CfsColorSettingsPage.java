package consulo.csharp.cfs.ide.highlight;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.psi.tree.IElementType;
import consulo.csharp.cfs.lang.CfsLanguage;
import consulo.csharp.cfs.lang.CfsTokens;
import consulo.csharp.cfs.lang.IndexCfsLanguageVersion;

/**
 * @author VISTALL
 * @since 25.03.2015
 */
public class CfsColorSettingsPage implements ColorSettingsPage
{
	private AttributesDescriptor[] myAttributesDescriptors = new AttributesDescriptor[]{
			new AttributesDescriptor("Index", CfsHighlightKey.INDEX),
			new AttributesDescriptor("Align", CfsHighlightKey.ALIGN),
			new AttributesDescriptor("Format", CfsHighlightKey.FORMAT)
	};

	private Map<String, TextAttributesKey> myTags = new HashMap<String, TextAttributesKey>()
	{
		{
			put("index", CfsHighlightKey.INDEX);
			put("format", CfsHighlightKey.FORMAT);
			put("align", CfsHighlightKey.ALIGN);
			put("text", HighlighterColors.TEXT);
		}
	};

	@Nonnull
	@Override
	public SyntaxHighlighter getHighlighter()
	{
		return new CfsSyntaxHighlighter(CfsLanguage.INSTANCE.findVersionByClass(IndexCfsLanguageVersion.class))
		{
			@Nonnull
			@Override
			public TextAttributesKey[] getTokenHighlights(IElementType elementType)
			{
				if(elementType == CfsTokens.TEXT)
				{
					return pack(DefaultLanguageHighlighterColors.STRING);
				}
				return super.getTokenHighlights(elementType);
			}
		};
	}

	@Nonnull
	@Override
	public String getDemoText()
	{
		return "value = {<index>0</index>,<align>1</align>:<format>X</format>}, value2 = {<text>arg</text>," +
				"<align> -1</align>:<format>X</format>}";
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
	{
		return myTags;
	}

	@Nonnull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors()
	{
		return myAttributesDescriptors;
	}

	@Nonnull
	@Override
	public ColorDescriptor[] getColorDescriptors()
	{
		return new ColorDescriptor[0];
	}

	@Nonnull
	@Override
	public String getDisplayName()
	{
		return CfsLanguage.INSTANCE.getDisplayName();
	}
}
