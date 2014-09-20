package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetFieldOrPropertyMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetObjectValueMirrorNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.InvalidObjectException;
import mono.debugger.ObjectValueMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DefaultDotNetLogicValueView implements DotNetLogicValueView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror)
	{
		return true;
	}

	@Override
	public void computeChildren(@NotNull DotNetDebugContext debugContext, @NotNull ThreadMirror threadMirror, @Nullable Value<?> value, @NotNull XValueChildrenList childrenList)
	{
		if(!(value instanceof ObjectValueMirror))
		{
			return;
		}

		try
		{
			TypeMirror type = value.type();

			assert type != null;

			childrenList.add(new DotNetObjectValueMirrorNode(debugContext, threadMirror, type, null));

			List<FieldOrPropertyMirror> fieldMirrors = type.fieldAndProperties(true);
			for(FieldOrPropertyMirror fieldMirror : fieldMirrors)
			{
				if(fieldMirror.isStatic())
				{
					continue;
				}

				if(fieldMirror instanceof PropertyMirror && ((PropertyMirror) fieldMirror).isArrayProperty())
				{
					continue;
				}
				childrenList.add(new DotNetFieldOrPropertyMirrorNode(debugContext, fieldMirror, threadMirror, (ObjectValueMirror) value));
			}
		}
		catch(InvalidObjectException ignored)
		{
		}
	}
}
