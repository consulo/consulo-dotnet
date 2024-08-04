package consulo.dotnet.psi.resolve;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.util.function.Processors;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.psi.stub.IdFilter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 03-Jul-22
 */
@ExtensionAPI(ComponentScope.PROJECT)
public interface DotNetShortNameSearcherExtension
{
	@Nonnull
	default Collection<String> getTypeNames(@Nonnull SearchScope scope, @Nullable IdFilter filter)
	{
		Set<String> types = new HashSet<>();
		collectTypeNames(Processors.cancelableCollectProcessor(types), scope, filter);
		return types;
	}


	public abstract void collectTypeNames(@Nonnull Predicate<String> processor, @Nonnull SearchScope scope, @Nullable IdFilter filter);

	public abstract void collectTypes(@Nonnull String key, @Nonnull SearchScope scope, @Nullable IdFilter filter, @Nonnull Predicate<DotNetTypeDeclaration> processor);

	@Nonnull
	default Collection<DotNetTypeDeclaration> getTypes(@Nonnull String key, @Nonnull SearchScope scope, @Nullable IdFilter filter)
	{
		Set<DotNetTypeDeclaration> types = new HashSet<>();
		collectTypes(key, scope, filter, Processors.cancelableCollectProcessor(types));
		return types;
	}
}
