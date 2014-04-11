package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import mono.debugger.MethodParameterMirror;
import mono.debugger.PrimitiveValueMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetMethodParameterMirrorNode extends XNamedValue
{
	private final Value myValue;
	private final MethodParameterMirror myParameter;

	public DotNetMethodParameterMirrorNode(MethodParameterMirror parameter, StackFrameMirror frame)
	{
		super(parameter.name());
		myParameter = parameter;
		myValue = frame.parameterValue(parameter);
	}

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		xValueNode.setPresentation(AllIcons.Nodes.Parameter, new XValuePresentation()
		{
			@Nullable
			@Override
			public String getType()
			{
				return myParameter.type().qualifiedName();
			}

			@Override
			public void renderValue(@NotNull XValueTextRenderer xValueTextRenderer)
			{
				if(myValue != null)
				{
					if(myValue instanceof PrimitiveValueMirror)
					{
						xValueTextRenderer.renderNumericValue(String.valueOf(((PrimitiveValueMirror) myValue).value()));
					}
				}
				else
				{
					xValueTextRenderer.renderComment("error");
				}
			}
		}, true);
	}
}
