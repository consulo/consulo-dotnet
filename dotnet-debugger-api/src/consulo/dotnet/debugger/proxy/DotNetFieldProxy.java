package consulo.dotnet.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.ArrayFactory;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetFieldProxy extends DotNetFieldOrPropertyProxy
{
	public static final DotNetFieldProxy[] EMPTY_ARRAY = new DotNetFieldProxy[0];

	public static ArrayFactory<DotNetFieldProxy> ARRAY_FACTORY = new ArrayFactory<DotNetFieldProxy>()
	{
		@NotNull
		@Override
		public DotNetFieldProxy[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetFieldProxy[count];
		}
	};

	boolean isLiteral();

	@Nullable
	Number getEnumConstantValue(@NotNull DotNetStackFrameProxy stackFrameProxy);
}
