package org.mustbe.consulo.dotnet.debugger;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetLocalVariableMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetMethodParameterMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetObjectValueMirrorNode;
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
import mono.debugger.AbsentInformationException;
import mono.debugger.LocalVariableMirror;
import mono.debugger.Location;
import mono.debugger.MethodMirror;
import mono.debugger.MethodParameterMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

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
		return XDebuggerUtil.getInstance().createPosition(fileByPath, myFrame.location().lineNumber() - 1);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof DotNetStackFrame && ((DotNetStackFrame) obj).myFrame.id() == myFrame.id() && ((DotNetStackFrame) obj).myFrame.thread()
				.id() == myFrame.thread().id();
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

		XValueChildrenList childrenList = new XValueChildrenList();

		try
		{
			Value value = myFrame.thisObject();
			if(value instanceof ObjectValueMirror)
			{
				TypeMirror type = value.type();
				assert type != null;
				childrenList.add(new DotNetObjectValueMirrorNode(myProject, myFrame.thread(), type, (ObjectValueMirror) value));
			}
			else
			{
				childrenList.add(new DotNetObjectValueMirrorNode(myProject, myFrame.thread(), myFrame.location().declaringType(), null));
			}
		}
		catch(AbsentInformationException ignored)
		{
		}

		MethodParameterMirror[] parameters = method.parameters();

		for(MethodParameterMirror parameter : parameters)
		{
			DotNetMethodParameterMirrorNode parameterMirrorNode = new DotNetMethodParameterMirrorNode(parameter, myFrame, myProject);

			childrenList.add(parameterMirrorNode);
		}

		try
		{
			LocalVariableMirror[] locals = method.locals(myFrame.location().codeIndex());
			for(LocalVariableMirror local : locals)
			{
				DotNetLocalVariableMirrorNode localVariableMirrorNode = new DotNetLocalVariableMirrorNode(local, myFrame, myProject);

				childrenList.add(localVariableMirrorNode);
			}
		}
		catch(IllegalArgumentException ignored)
		{
		}
		node.addChildren(childrenList, true);
	}

	@NotNull
	public StackFrameMirror getFrame()
	{
		return myFrame;
	}
}
