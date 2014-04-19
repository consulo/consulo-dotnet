package org.mustbe.consulo.dotnet.debugger.nodes;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.icons.AllIcons;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetFieldOrPropertyMirrorNode extends DotNetAbstractVariableMirrorNode
{
	private final FieldOrPropertyMirror myFieldOrPropertyMirror;
	private final ObjectValueMirror myObjectValueMirror;

	public DotNetFieldOrPropertyMirrorNode(
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull FieldOrPropertyMirror fieldOrPropertyMirror,
			@NotNull ThreadMirror threadMirror,
			@Nullable ObjectValueMirror objectValueMirror)
	{
		super(debuggerContext, fieldOrPropertyMirror.name(), threadMirror);
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myObjectValueMirror = objectValueMirror;
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myFieldOrPropertyMirror.type();
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		return myFieldOrPropertyMirror instanceof PropertyMirror ? AllIcons.Nodes.Property : AllIcons.Nodes.Field;
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariable()
	{
		return myFieldOrPropertyMirror.value(myThreadMirror, myObjectValueMirror);
	}

	@Override
	public void setValueForVariable(@NotNull Value<?> value)
	{
		myFieldOrPropertyMirror.setValue(myThreadMirror, myObjectValueMirror, value);
	}
}
