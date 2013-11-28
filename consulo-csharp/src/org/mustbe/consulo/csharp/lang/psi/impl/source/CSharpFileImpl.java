package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.CSharpFileType;
import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import org.mustbe.consulo.dotnet.psi.DotNetFile;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpFileImpl extends PsiFileBase implements DotNetFile
{
	public CSharpFileImpl(@NotNull FileViewProvider viewProvider)
	{
		super(viewProvider, CSharpLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType()
	{
		return CSharpFileType.INSTANCE;
	}
}
