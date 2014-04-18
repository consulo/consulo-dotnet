package org.mustbe.consulo.dotnet.debugger.nodes;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import mono.debugger.LocalVariableMirror;
import mono.debugger.LocalVariableOrParameterMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;
import mono.debugger.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 12.04.14
 */
public class DotNetLocalVariableMirrorNode extends DotNetAbstractVariableMirrorNode
{
	private final LocalVariableMirror myLocal;
	private final StackFrameMirror myFrame;

	public DotNetLocalVariableMirrorNode(LocalVariableMirror local, StackFrameMirror frame, Project project)
	{
		super(local.name(), project);
		myLocal = local;
		myFrame = frame;
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myLocal.type();
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		return AllIcons.Nodes.Variable;
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariable()
	{
		return myFrame.localOrParameterValue(myLocal);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValueForVariable(@NotNull Value<?> value)
	{
		myFrame.setLocalOrParameterValues(new ImmutablePair<LocalVariableOrParameterMirror, Value<?>>(myLocal, value));
	}
}
