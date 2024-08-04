package consulo.dotnet.psi.internal;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.disposer.Disposable;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiModificationTrackerListener;
import consulo.project.Project;
import consulo.util.lang.Pair;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import jakarta.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author VISTALL
 * @since 2020-10-25
 */
@Singleton
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
public class DotNetInheritCache implements Disposable
{
	@Nonnull
	public static DotNetInheritCache getInstance(@Nonnull Project project)
	{
		return project.getInstance(DotNetInheritCache.class);
	}

	private final Map<String, Map<Pair<String, Boolean>, Boolean>> myResult = new ConcurrentHashMap<>();

	@Inject
	public DotNetInheritCache(Project project)
	{
		project.getMessageBus().connect(this).subscribe(PsiModificationTrackerListener.class, myResult::clear);
	}

	@RequiredReadAction
	public boolean calcResult(DotNetTypeDeclaration declaration, String otherVmQName, boolean deep)
	{
		String declVmQName = declaration.getVmQName();
		// if vmqname is null - don't use cache
		if(declVmQName == null)
		{
			return isInheritorImpl(declaration, otherVmQName, deep, new HashSet<>());
		}

		Pair<String, Boolean> otherPair = Pair.create(otherVmQName, deep);

		Map<Pair<String, Boolean>, Boolean> map = myResult.get(declVmQName);
		if(map != null)
		{
			Boolean result = map.get(otherPair);
			if(result != null)
			{
				return result;
			}
		}

		boolean callResult = isInheritorImpl(declaration, otherVmQName, deep, new HashSet<>());

		map = myResult.computeIfAbsent(declVmQName, s -> new ConcurrentHashMap<>());
		map.putIfAbsent(otherPair, callResult);
		return callResult;
	}

	@RequiredReadAction
	static boolean isInheritorImpl(@Nonnull DotNetTypeDeclaration typeDeclaration,
								   @Nonnull String otherVmQName,
								   boolean deep,
								   @Nonnull Set<String> alreadyProcessedTypes)
	{
		DotNetTypeRef[] anExtends = typeDeclaration.getExtendTypeRefs();
		if(anExtends.length > 0)
		{
			for(DotNetTypeRef typeRef : anExtends)
			{
				if(typeRef.isEqualToVmQName(otherVmQName))
				{
					return true;
				}

				PsiElement psiElement = typeRef.resolve().getElement();
				if(psiElement instanceof DotNetTypeDeclaration)
				{
					if(psiElement.isEquivalentTo(typeDeclaration))
					{
						return false;
					}

					String vmQName = ((DotNetTypeDeclaration) psiElement).getVmQName();
					if(Objects.equals(vmQName, otherVmQName))
					{
						return true;
					}

					if(deep)
					{
						if(alreadyProcessedTypes.add(vmQName))
						{
							if(isInheritorImpl((DotNetTypeDeclaration) psiElement, otherVmQName, true, alreadyProcessedTypes))
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void dispose()
	{
		myResult.clear();
	}
}
