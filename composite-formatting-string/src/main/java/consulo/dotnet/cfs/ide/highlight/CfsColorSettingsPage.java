package consulo.dotnet.cfs.ide.highlight;

import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.codeEditor.HighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.colorScheme.setting.AttributesDescriptor;
import consulo.colorScheme.setting.ColorDescriptor;
import consulo.dotnet.cfs.lang.CfsLanguage;
import consulo.dotnet.cfs.lang.CfsTokens;
import consulo.dotnet.cfs.lang.IndexCfsLanguageVersion;
import consulo.language.ast.IElementType;
import consulo.language.editor.colorScheme.setting.ColorSettingsPage;
import consulo.language.editor.highlight.SyntaxHighlighter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 25.03.2015
 */
@ExtensionImpl
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
