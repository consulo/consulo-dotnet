package consulo.dotnet.debugger.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.util.ArrayFactory;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetFieldOrPropertyProxy extends DotNetVariableProxy
{
	public static final DotNetFieldOrPropertyProxy[] EMPTY_ARRAY = new DotNetFieldOrPropertyProxy[0];

	public static ArrayFactory<DotNetFieldOrPropertyProxy> ARRAY_FACTORY = new ArrayFactory<DotNetFieldOrPropertyProxy>()
	{
		@Nonnull
		@Override
		public DotNetFieldOrPropertyProxy[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetFieldOrPropertyProxy[count];
		}
	};

	boolean isStatic();

	@Nonnull
	DotNetTypeProxy getParentType();

	@Nullable
	DotNetValueProxy getValue(@Nonnull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy);

	void setValue(@Nonnull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy, @Nonnull DotNetValueProxy newValueProxy);
}
