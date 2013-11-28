package org.mustbe.consulo.dotnet.psi.impl.stub;

import org.mustbe.consulo.dotnet.psi.DotNetFile;
import com.intellij.psi.stubs.PsiFileStubImpl;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class DotNetFileStub extends PsiFileStubImpl<DotNetFile>
{
	public DotNetFileStub(DotNetFile file)
	{
		super(file);
	}
}
