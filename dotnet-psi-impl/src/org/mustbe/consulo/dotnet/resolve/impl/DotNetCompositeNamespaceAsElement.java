package org.mustbe.consulo.dotnet.resolve.impl;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public class DotNetCompositeNamespaceAsElement extends LightElement implements DotNetNamespaceAsElement
{
	private final DotNetNamespaceAsElement[] myArray;

	public DotNetCompositeNamespaceAsElement(Project project, DotNetNamespaceAsElement[] array)
	{
		super(PsiManager.getInstance(project), Language.ANY);
		myArray = array;
		assert myArray.length != 0;
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement
			place)
	{
		for(DotNetNamespaceAsElement dotNetNamespaceAsElement : myArray)
		{
			if(!dotNetNamespaceAsElement.processDeclarations(processor, state, lastParent, place))
			{
				return false;
			}
		}
		return true;
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return myArray[0].getPresentableParentQName();
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		return myArray[0].getPresentableQName();
	}

	@Override
	public String toString()
	{
		return getPresentableQName();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}
