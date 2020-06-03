package consulo.dotnet.module.extension;

import consulo.disposer.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.ObjectUtil;
import com.intellij.util.concurrency.AppExecutorUtil;
import consulo.internal.dotnet.asm.mbel.AssemblyInfo;
import consulo.internal.dotnet.asm.mbel.ModuleParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;
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
