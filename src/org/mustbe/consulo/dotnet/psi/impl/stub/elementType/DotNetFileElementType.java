package org.mustbe.consulo.dotnet.psi.impl.stub.elementType;

import org.mustbe.consulo.dotnet.psi.impl.stub.DotNetFileStub;
import com.intellij.lang.Language;
import com.intellij.psi.tree.IStubFileElementType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class DotNetFileElementType extends IStubFileElementType<DotNetFileStub>
{
	public DotNetFileElementType(Language language)
	{
		super(language);
	}

}
