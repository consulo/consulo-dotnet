package org.mustbe.consulo.dotnet.resolve;

import org.consulo.lombok.annotations.ArrayFactoryFields;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.NotNullFunction;

/**
 * @author VISTALL
 * @since 11.02.14
 */
@ArrayFactoryFields
public interface DotNetNamespaceAsElement extends DotNetQualifiedElement
{
	enum ChildrenFilter
	{
		ONLY_ELEMENTS,
		ONLY_NAMESPACES,
		NONE
	}

	@NotNull
	PsiElement[] getChildren(@NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter);

	@NotNull
	PsiElement[] getChildren(@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter);

	@NotNull
	PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter);

	@NotNull
	PsiElement[] findChildren(@NotNull String name,
			@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter);
}
