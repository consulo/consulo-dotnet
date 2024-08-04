package consulo.dotnet.psi.resolve;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.ContainerUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static consulo.dotnet.psi.resolve.DotNetPsiSearcher.DEFAULT_TRANSFORMER;

/**
 * @author VISTALL
 * @since 03-Jul-22
 */
@ExtensionAPI(ComponentScope.PROJECT)
public interface DotNetPsiSearcherExtension
{
	@Nullable
	@RequiredReadAction
	default DotNetNamespaceAsElement findNamespace(@Nonnull String qName, @Nonnull SearchScope scope)
	{
		return null;
	}

	@Nonnull
	@RequiredReadAction
	default DotNetTypeDeclaration[] findTypes(@Nonnull String vmQName, @Nonnull SearchScope scope)
	{
		return findTypes(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@Nonnull
	@RequiredReadAction
	default DotNetTypeDeclaration[] findTypes(@Nonnull String vmQName, @Nonnull SearchScope scope, @Nonnull Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		Collection<? extends DotNetTypeDeclaration> declarations = findTypesImpl(vmQName, scope);
		if(declarations.isEmpty())
		{
			return DotNetTypeDeclaration.EMPTY_ARRAY;
		}
		List<DotNetTypeDeclaration> list = new ArrayList<DotNetTypeDeclaration>(declarations.size());

		for(DotNetTypeDeclaration declaration : declarations)
		{
			list.add(transformer.apply(declaration));
		}

		return ContainerUtil.toArray(list, DotNetTypeDeclaration.ARRAY_FACTORY);
	}

	@Nonnull
	@RequiredReadAction
	public abstract Collection<? extends DotNetTypeDeclaration> findTypesImpl(@Nonnull String vmQName, @Nonnull SearchScope scope);

	@Nullable
	@RequiredReadAction
	default DotNetTypeDeclaration findType(@Nonnull String vmQName, @Nonnull SearchScope scope)
	{
		return findType(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@Nullable
	@RequiredReadAction
	default DotNetTypeDeclaration findType(@Nonnull String vmQName, @Nonnull SearchScope scope, @Nonnull Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		DotNetTypeDeclaration[] types = findTypes(vmQName, scope, transformer);
		return ArrayUtil.getFirstElement(types);
	}
}
