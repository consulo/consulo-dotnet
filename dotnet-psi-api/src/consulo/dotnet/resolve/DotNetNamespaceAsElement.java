package consulo.dotnet.resolve;

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
	public static final DotNetNamespaceAsElement[] EMPTY_ARRAY = new DotNetNamespaceAsElement[0];

	public static ArrayFactory<DotNetNamespaceAsElement> ARRAY_FACTORY = new ArrayFactory<DotNetNamespaceAsElement>()
	{
		@NotNull
		@Override
		public DotNetNamespaceAsElement[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetNamespaceAsElement[count];
		}
	};

	enum ChildrenFilter
	{
		ONLY_ELEMENTS,
		ONLY_NAMESPACES,
		NONE
	}

	@NotNull
	@RequiredReadAction
	PsiElement[] getChildren(@NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter);

	@NotNull
	@RequiredReadAction
	PsiElement[] getChildren(@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter);

	@NotNull
	@RequiredReadAction
	PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, @NotNull ChildrenFilter filter);

	@NotNull
	@RequiredReadAction
	PsiElement[] findChildren(@NotNull String name,
			@NotNull GlobalSearchScope globalSearchScope,
			@NotNull NotNullFunction<PsiElement, PsiElement> transformer,
			@NotNull ChildrenFilter filter);
}
