package consulo.dotnet.psi.internal;

import com.google.common.base.Objects;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiModificationTracker;
import consulo.annotation.access.RequiredReadAction;
import consulo.disposer.Disposable;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.util.lang.Pair;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author VISTALL
 * @since 2020-10-25
 */
@Singleton
public class DotNetInheritCache implements Disposable
{
	@Nonnull
	public static DotNetInheritCache getInstance(@Nonnull Project project)
	{
		return ServiceManager.getService(project, DotNetInheritCache.class);
	}

	private final Map<String, Map<Pair<String, Boolean>, Boolean>> myResult = new ConcurrentHashMap<>();

	@Inject
	public DotNetInheritCache(Project project)
	{
		project.getMessageBus().connect(this).subscribe(PsiModificationTracker.TOPIC, myResult::clear);
	}

	@RequiredReadAction
	public boolean calcResult(DotNetTypeDeclaration declaration, String otherVmQName, boolean deep)
	{
		String declVmQName = declaration.getVmQName();
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
					if(Objects.equal(vmQName, otherVmQName))
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
