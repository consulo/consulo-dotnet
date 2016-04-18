package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetFieldOrPropertyMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetStructValueInfo;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueMirrorNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.InvalidObjectException;
import mono.debugger.ObjectValueMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.StructValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DefaultDotNetLogicValueView extends BaseDotNetLogicView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull TypeMirror typeMirror)
	{
		return true;
	}

	@Override
	public void computeChildrenImpl(@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableMirrorNode parentNode,
			@NotNull ThreadMirror threadMirror,
			@Nullable Value<?> value,
			@NotNull XValueChildrenList childrenList)
	{
		if(value instanceof ObjectValueMirror)
		{
			try
			{
				TypeMirror type = value.type();

				assert type != null;

				DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, threadMirror, type);

				List<FieldOrPropertyMirror> fieldMirrors = type.fieldAndProperties(true);
				for(FieldOrPropertyMirror fieldMirror : fieldMirrors)
				{
					if(needSkip(fieldMirror))
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
		else if(value instanceof StructValueMirror)
		{
			Map<FieldOrPropertyMirror, Value<?>> fields = ((StructValueMirror) value).map();

			for(Map.Entry<FieldOrPropertyMirror, Value<?>> entry : fields.entrySet())
			{
				FieldOrPropertyMirror fieldMirror = entry.getKey();
				Value<?> fieldValue = entry.getValue();

				DotNetStructValueInfo valueInfo = new DotNetStructValueInfo((StructValueMirror) value, parentNode, fieldMirror, fieldValue);

				childrenList.add(new DotNetFieldOrPropertyMirrorNode(debugContext, fieldMirror, threadMirror, null, valueInfo));
			}
		}
	}

	private static boolean needSkip(FieldOrPropertyMirror fieldMirror)
	{
		return fieldMirror.isStatic() || fieldMirror instanceof PropertyMirror && ((PropertyMirror) fieldMirror).isArrayProperty();
	}
}
