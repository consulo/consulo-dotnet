package consulo.dotnet.module.extension;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.util.concurrent.AppExecutorUtil;
import consulo.disposer.Disposable;
import consulo.ide.ServiceManager;
import consulo.internal.dotnet.asm.mbel.AssemblyInfo;
import consulo.internal.dotnet.asm.mbel.ModuleParser;
import consulo.util.lang.ObjectUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author VISTALL
 * @since 2019-10-05
 */
@Singleton
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
public class AssemblyInfoCacheService implements Disposable
{
	@Nonnull
	public static AssemblyInfoCacheService getInstance()
	{
		return ServiceManager.getService(AssemblyInfoCacheService.class);
	}

	private final Map<File, Object> myCachePath = new ConcurrentHashMap<>();
	private final ScheduledFuture<?> myClearFuture;

	public AssemblyInfoCacheService()
	{
		myClearFuture = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(myCachePath::clear, 1, 1, TimeUnit.MINUTES);
	}

	@Nullable
	public AssemblyInfo getAssemblyInfo(@Nonnull File file)
	{
		Object value = myCachePath.computeIfAbsent(file, s -> {
			try
			{
				return ModuleParser.parseAssemblyInfo(file);
			}
			catch(Exception e)
			{
				return ObjectUtil.NULL;
			}
		});
		return value == ObjectUtil.NULL ? null : (AssemblyInfo) value;
	}

	@Override
	public void dispose()
	{
		myClearFuture.cancel(false);
		myCachePath.clear();
	}
}
