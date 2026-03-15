package consulo.dotnet.psi.resolve;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.ContainerUtil;

import org.jspecify.annotations.Nullable;
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
	default DotNetNamespaceAsElement findNamespace(String qName, SearchScope scope)
	{
		return null;
	}

	@RequiredReadAction
	default DotNetTypeDeclaration[] findTypes(String vmQName, SearchScope scope)
	{
		return findTypes(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@RequiredReadAction
	default DotNetTypeDeclaration[] findTypes(String vmQName, SearchScope scope, Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
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

	@RequiredReadAction
	public abstract Collection<? extends DotNetTypeDeclaration> findTypesImpl(String vmQName, SearchScope scope);

	@Nullable
	@RequiredReadAction
	default DotNetTypeDeclaration findType(String vmQName, SearchScope scope)
	{
		return findType(vmQName, scope, DEFAULT_TRANSFORMER);
	}

	@Nullable
	@RequiredReadAction
	default DotNetTypeDeclaration findType(String vmQName, SearchScope scope, Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		DotNetTypeDeclaration[] types = findTypes(vmQName, scope, transformer);
		return ArrayUtil.getFirstElement(types);
	}
}
