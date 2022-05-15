package consulo.dotnet.psi.impl;

import consulo.component.util.ModificationTracker;
import consulo.component.util.SimpleModificationTracker;
import consulo.language.psi.AnyPsiChangeListener;
import consulo.language.psi.PsiManager;
import consulo.project.Project;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2020-04-05
 */
@Singleton
public class DotNetTypeRefCacheManager
{
	@Nonnull
	public static DotNetTypeRefCacheManager getInstance(@Nonnull Project project)
	{
		return project.getInstance(DotNetTypeRefCacheManager.class);
	}

	private SimpleModificationTracker myPhysicalTracker = new SimpleModificationTracker();
	private SimpleModificationTracker myNonPhysicalTracker = new SimpleModificationTracker();

	@Inject
	private DotNetTypeRefCacheManager(Project project)
	{
		project.getMessageBus().connect().subscribe(PsiManager.ANY_PSI_CHANGE_TOPIC, new AnyPsiChangeListener()
		{
			@Override
			public void beforePsiChanged(boolean isPhysical)
			{
				if(isPhysical)
				{
					myPhysicalTracker.incModificationCount();
				}
				else
				{
					myNonPhysicalTracker.incModificationCount();
				}
			}
		});
	}

	@Nonnull
	public ModificationTracker getModificationTracker(boolean isPhysical)
	{
		return isPhysical ? myPhysicalTracker : myNonPhysicalTracker;
	}
}
