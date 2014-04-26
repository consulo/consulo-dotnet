package org.mustbe.consulo.dotnet.debugger.nodes;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.icons.AllIcons;
import mono.debugger.LocalVariableMirror;
import mono.debugger.LocalVariableOrParameterMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.TypeMirror;
import mono.debugger.TypeTag;
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

	public DotNetLocalVariableMirrorNode(DotNetDebugContext debuggerContext, LocalVariableMirror local, StackFrameMirror frame)
	{
		super(debuggerContext, local.name(), frame.thread());
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
		TypeMirror typeOfVariable = getTypeOfVariable();
		if(typeOfVariable.isArray())
		{
			return AllIcons.Debugger.Db_array;
		}

		TypeTag typeTag = typeTag();
		if(typeTag != null && typeTag != TypeTag.String)
		{
			return AllIcons.Debugger.Db_primitive;
		}

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
