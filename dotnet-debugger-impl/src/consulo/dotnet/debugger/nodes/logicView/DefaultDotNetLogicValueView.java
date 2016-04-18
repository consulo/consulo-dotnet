package consulo.dotnet.debugger.nodes.logicView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.PropertyMirror;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DefaultDotNetLogicValueView extends BaseDotNetLogicView
{
	@Override
	public boolean canHandle(@NotNull DotNetDebugContext debugContext, @NotNull DotNetTypeProxy typeMirror)
	{
		return true;
	}

	@Override
	public void computeChildrenImpl(@NotNull DotNetDebugContext debugContext,
			@NotNull DotNetAbstractVariableMirrorNode parentNode,
			@NotNull DotNetThreadProxy threadMirror,
			@Nullable DotNetValueProxy value,
			@NotNull XValueChildrenList childrenList)
	{
		/*if(value instanceof ObjectValueMirror)
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
		}  */
	}

	private static boolean needSkip(FieldOrPropertyMirror fieldMirror)
	{
		return fieldMirror.isStatic() || fieldMirror instanceof PropertyMirror && ((PropertyMirror) fieldMirror).isArrayProperty();
	}
}
