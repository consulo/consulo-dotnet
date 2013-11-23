package org.mustbe.consulo.csharp;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public class CSharpFileType extends LanguageFileType
{
	public static final CSharpFileType INSTANCE = new CSharpFileType();

	private CSharpFileType()
	{
		super(CSharpLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName()
	{
		return "C#";
	}

	@NotNull
	@Override
	public String getDescription()
	{
		return "C# files";
	}

	@NotNull
	@Override
	public String getDefaultExtension()
	{
		return "cs";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return CSharpIcons.FileType;
	}
}
