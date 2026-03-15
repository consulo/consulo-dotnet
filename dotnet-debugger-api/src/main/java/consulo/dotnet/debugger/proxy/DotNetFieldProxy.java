package consulo.dotnet.debugger.proxy;

import consulo.util.collection.ArrayFactory;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public interface DotNetFieldProxy extends DotNetFieldOrPropertyProxy
{
	public static final DotNetFieldProxy[] EMPTY_ARRAY = new DotNetFieldProxy[0];

	public static ArrayFactory<DotNetFieldProxy> ARRAY_FACTORY = new ArrayFactory<DotNetFieldProxy>()
	{
		@Override
		public DotNetFieldProxy[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetFieldProxy[count];
		}
	};

	boolean isLiteral();

	@Nullable
	Number getEnumConstantValue(DotNetStackFrameProxy stackFrameProxy);
}
