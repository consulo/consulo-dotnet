package consulo.dotnet.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
		@NotNull
		@Override
		public DotNetFieldOrPropertyProxy[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetFieldOrPropertyProxy[count];
		}
	};

	boolean isStatic();

	@NotNull
	DotNetTypeProxy getParentType();

	@Nullable
	DotNetValueProxy getValue(@NotNull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy);

	void setValue(@NotNull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy, @NotNull DotNetValueProxy newValueProxy);
}
