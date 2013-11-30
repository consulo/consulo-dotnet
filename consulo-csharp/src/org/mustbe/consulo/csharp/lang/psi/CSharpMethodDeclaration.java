package org.mustbe.consulo.csharp.lang.psi;

import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface CSharpMethodDeclaration extends DotNetMethodDeclaration
{
	@Override
	CSharpCodeBlock getCodeBlock();
}
