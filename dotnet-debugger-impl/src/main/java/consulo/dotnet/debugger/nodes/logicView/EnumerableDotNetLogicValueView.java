package consulo.dotnet.debugger.nodes.logicView;

import consulo.annotation.UsedInPlugin;
import consulo.debugger.frame.XNamedValue;
import consulo.debugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.nodes.DotNetSimpleValueNode;
import consulo.dotnet.debugger.nodes.DotNetThrowValueNode;
import consulo.dotnet.debugger.nodes.logicView.enumerator.CantCreateException;
import consulo.dotnet.debugger.nodes.logicView.enumerator.IEnumeratorAsIterator;
import consulo.dotnet.debugger.proxy.*;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

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

	@UsedInPlugin
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

		DotNetMethodProxy getEnumerator = DotNetDebuggerSearchUtil.findMethodImplementation(debugContext.getVirtualMachine(), "System.Collections.IEnumerable", "GetEnumerator", type);

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
		DotNetTypeProxy typeOfValue;
		try
		{
			if(getEnumeratorValue == null)
			{
				return;
			}
			typeOfValue = getEnumeratorValue.getType();
			assert typeOfValue != null;
		}
		catch(Exception e)
		{
			LOG.warn(e);
			return;
		}

		try
		{
			IEnumeratorAsIterator iterator = new IEnumeratorAsIterator(debugContext.getVirtualMachine(), frameProxy, getEnumeratorValue, typeOfValue);

			int i = 0;
			while(iterator.hasNext())
			{
				DotNetValueProxy next = iterator.next();

				if(next == null)
				{
					break;
				}
				childrenList.add(new DotNetSimpleValueNode(debugContext, String.valueOf(i++), frameProxy, next));
			}
		}
		catch(DotNetThrowValueException e)
		{
			childrenList.add(new DotNetThrowValueNode(debugContext, frameProxy, e.getThrowExceptionValue()));
			LOG.warn(e);
		}
		catch(CantCreateException e)
		{
			LOG.warn(e);
		}
		catch(DotNetNotSuspendedException ignored)
		{
		}
	}
}
