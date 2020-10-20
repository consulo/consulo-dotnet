package consulo.dotnet.lang.psi.impl;

import javax.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.psi.impl.AnyPsiChangeListener;
import com.intellij.psi.impl.PsiManagerImpl;

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
		return ServiceManager.getService(project, DotNetTypeRefCacheManager.class);
	}

	private SimpleModificationTracker myPhysicalTracker = new SimpleModificationTracker();
	private SimpleModificationTracker myNonPhysicalTracker = new SimpleModificationTracker();

	@Inject
	private DotNetTypeRefCacheManager(Project project)
	{
		project.getMessageBus().connect().subscribe(PsiManagerImpl.ANY_PSI_CHANGE_TOPIC, new AnyPsiChangeListener()
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
