package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetCodeBlock;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpCodeBlockImpl extends CSharpElementImpl implements DotNetCodeBlock
{
	public CSharpCodeBlockImpl(@NotNull ASTNode node)
	{
		super(node);
	}
}
