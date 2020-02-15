package consulo.dotnet.debugger.nodes.logicView;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.nodes.DotNetSimpleValueNode;
import consulo.dotnet.debugger.nodes.logicView.enumerator.CantCreateException;
import consulo.dotnet.debugger.nodes.logicView.enumerator.IEnumeratorAsIterator;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.logging.Logger;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class EnumerableDotNetLogicValueView extends BaseDotNetLogicView
{
	private static final Logger LOG = Logger.getInstance(EnumerableDotNetLogicValueView.class);

	private final Set<String> myIgnoreTypeSet = new HashSet<>();

	@Override
	public boolean canHandle(@Nonnull DotNetDebugContext debugContext, @Nonnull DotNetTypeProxy typeMirror)
	{
		if(!myIgnoreTypeSet.isEmpty() && myIgnoreTypeSet.contains(typeMirror.getFullName()))
		{
			return false;
		}

		return DotNetDebuggerSearchUtil.isInImplementList(typeMirror, "System.Collections.IEnumerable");
	}

	public void addIgnoredType(String typeQualifedName)
	{
		myIgnoreTypeSet.add(typeQualifedName);
	}

	@Override
	public void computeChildrenImpl(@Nonnull DotNetDebugContext debugContext,
			@Nonnull XNamedValue parentNode,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy value,
			@Nonnull XValueChildrenList childrenList)
	{
		if(!(value instanceof DotNetObjectValueProxy) && !(value instanceof DotNetStringValueProxy))
		{
			return;
		}

		DotNetTypeProxy type = value.getType();

		assert type != null;

		DotNetMethodProxy getEnumerator = DotNetDebuggerSearchUtil.findMethod("System.Collections.IEnumerable.GetEnumerator", type);

		if(getEnumerator == null)
		{
			return;
		}

		DotNetValueProxy getEnumeratorValue = null;
		try
		{
			getEnumeratorValue = getEnumerator.invoke(frameProxy, value);
		}
		catch(Exception e)
		{
			DotNetDebuggerSearchUtil.rethrow(frameProxy, e);
		}

		// need test returned object
		try
		{
			if(getEnumeratorValue == null)
			{
				return;
			}
			getEnumeratorValue.getType();
		}
		catch(Exception e)
		{
			LOG.warn(e);
			return;
		}

		try
		{
			IEnumeratorAsIterator iterator = new IEnumeratorAsIterator(frameProxy, getEnumeratorValue);

			int i = 0;
			while(iterator.hasNext())
			{
				DotNetValueProxy next = iterator.next();

				if(next == null)
				{
					continue;
				}
				childrenList.add(new DotNetSimpleValueNode(debugContext, String.valueOf(i++), frameProxy, next));
			}
		}
		catch(CantCreateException e)
		{
			LOG.warn(e);
		}
	}
}
