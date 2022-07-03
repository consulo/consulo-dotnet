package consulo.dotnet.psi.impl;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.util.ModificationTracker;
import consulo.component.util.SimpleModificationTracker;
import consulo.language.psi.AnyPsiChangeListener;
import consulo.project.Project;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2020-04-05
 */
@Singleton
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
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
	DotNetTypeRefCacheManager(Project project)
	{
		project.getMessageBus().connect().subscribe(AnyPsiChangeListener.class, new AnyPsiChangeListener()
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
