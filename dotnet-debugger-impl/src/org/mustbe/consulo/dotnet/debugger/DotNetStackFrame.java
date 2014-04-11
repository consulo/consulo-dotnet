package org.mustbe.consulo.dotnet.debugger;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.Location;
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
	private final Project myProject;

	public DotNetStackFrame(StackFrameMirror frame, Project project)
	{
		myFrame = frame;
		myProject = project;
	}

	@Nullable
	@Override
	public XSourcePosition getSourcePosition()
	{
		String fileName = myFrame.location().sourcePath();
		if(fileName == null)
		{
			return null;
		}
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(fileName);
		if(fileByPath == null)
		{
			return null;
		}
		return XDebuggerUtil.getInstance().createPosition(fileByPath, myFrame.location().lineNumber());
	}

	@Override
	public void customizePresentation(ColoredTextContainer component)
	{
		component.setIcon(AllIcons.Nodes.Method);

		Location location = myFrame.location();

		component.append(location.method().name() + "()", SimpleTextAttributes.REGULAR_ATTRIBUTES);

		StringBuilder builder = new StringBuilder();
		String fileName = location.sourcePath();
		if(fileName != null)
		{
			builder.append(":");
			builder.append(new File(fileName).getName());
		}

		builder.append(":");
		builder.append(location.lineNumber());
		builder.append(":");
		builder.append(location.columnNumber());
		builder.append(", ");
		builder.append(location.method().declaringType().qualifiedName());

		component.append(builder.toString(), SimpleTextAttributes.GRAY_ATTRIBUTES);
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		MethodMirror method = myFrame.location().method();

		MethodParameterMirror[] parameters = method.parameters();

		XValueChildrenList parameterValues = new XValueChildrenList();
		for(MethodParameterMirror parameter : parameters)
		{
			DotNetMethodParameterMirrorNode parameterMirrorNode = new DotNetMethodParameterMirrorNode(parameter, myFrame, myProject);

			parameterValues.add(parameterMirrorNode);
		}
		node.addChildren(parameterValues, true);
	}
}
