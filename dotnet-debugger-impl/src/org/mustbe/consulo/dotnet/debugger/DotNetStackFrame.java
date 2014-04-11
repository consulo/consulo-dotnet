package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.MethodMirror;
import mono.debugger.MethodParameterMirror;
import mono.debugger.StackFrameMirror;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetStackFrame extends XStackFrame
{
	private final StackFrameMirror myFrame;

	public DotNetStackFrame(StackFrameMirror frame)
	{
		myFrame = frame;
	}

	@Nullable
	@Override
	public XSourcePosition getSourcePosition()
	{
		return super.getSourcePosition();
	}

	@Override
	public void customizePresentation(ColoredTextContainer component)
	{
		component.setIcon(AllIcons.Nodes.Method);
		component.append(myFrame.location().method().name(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		MethodMirror method = myFrame.location().method();

		MethodParameterMirror[] parameters = method.parameters();

		XValueChildrenList parameterValues = new XValueChildrenList();
		for(MethodParameterMirror parameter : parameters)
		{
			DotNetMethodParameterMirrorNode parameterMirrorNode = new DotNetMethodParameterMirrorNode(parameter, myFrame);

			parameterValues.add(parameterMirrorNode);
		}
		node.addChildren(parameterValues, true);
	}
}
