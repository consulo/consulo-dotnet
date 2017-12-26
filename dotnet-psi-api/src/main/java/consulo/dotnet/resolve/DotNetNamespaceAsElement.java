package consulo.dotnet.resolve;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayFactory;
import com.intellij.util.NotNullFunction;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public interface DotNetNamespaceAsElement extends DotNetQualifiedElement
{
	DotNetNamespaceAsElement[] EMPTY_ARRAY = new DotNetNamespaceAsElement[0];

	ArrayFactory<DotNetNamespaceAsElement> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetNamespaceAsElement[count];

	enum ChildrenFilter
	{
		ONLY_ELEMENTS,
		ONLY_NAMESPACES,
		NONE
	}

	@NotNull
	@RequiredReadAction
	Collection<PsiElement> getChildren(@NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter);

	@NotNull
	@RequiredReadAction
	Collection<PsiElement> getChildren(@NotNull GlobalSearchScope globalSearchScope, @NotNull NotNullFunction<PsiElement, PsiElement> transformer, @NotNull ChildrenFilter filter);

	@NotNull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter);

	@NotNull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, @NotNull NotNullFunction<PsiElement, PsiElement> transformer, @NotNull ChildrenFilter filter);
}
