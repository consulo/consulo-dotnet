package consulo.dotnet.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayFactory;
import com.intellij.util.NotNullFunction;
import com.intellij.util.Processor;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;

import javax.annotation.Nonnull;
import java.util.Collection;

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

	@Nonnull
	@RequiredReadAction
	default Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull ChildrenFilter filter)
	{
		return getChildren(globalSearchScope, DotNetTypeTransformer.INSTANCE, filter);
	}

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull NotNullFunction<PsiElement, PsiElement> transformer, @Nonnull ChildrenFilter filter);

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@Nonnull String name, @Nonnull GlobalSearchScope globalSearchScope, @Nonnull ChildrenFilter filter);

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@Nonnull String name,
										@Nonnull GlobalSearchScope globalSearchScope,
										@Nonnull NotNullFunction<PsiElement, PsiElement> transformer,
										@Nonnull ChildrenFilter filter);

	@RequiredReadAction
	default boolean processChildren(@Nonnull GlobalSearchScope globalSearchScope,
									@Nonnull NotNullFunction<PsiElement, PsiElement> transformer,
									@Nonnull ChildrenFilter filter,
									@Nonnull Processor<PsiElement> processor)
	{
		Collection<PsiElement> children = getChildren(globalSearchScope, transformer, filter);

		for(PsiElement child : children)
		{
			if(!processor.process(child))
			{
				return false;
			}
		}
		return true;
	}
}
