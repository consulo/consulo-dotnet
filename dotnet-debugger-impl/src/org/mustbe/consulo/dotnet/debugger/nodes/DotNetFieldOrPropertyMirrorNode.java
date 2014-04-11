package org.mustbe.consulo.dotnet.debugger.nodes;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.PropertyMirror;
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
			FieldOrPropertyMirror fieldOrPropertyMirror,
			@NotNull Project project,
			@Nullable ObjectValueMirror objectValueMirror)
	{
		super(fieldOrPropertyMirror.name(), project);
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
		return myFieldOrPropertyMirror.value(myObjectValueMirror);
	}
}
