package org.mustbe.consulo.dotnet.debugger.nodes;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import mono.debugger.FieldMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetFieldMirrorNode extends DotNetAbstractVariableMirrorNode
{
	private final FieldMirror myFieldMirror;
	private final ObjectValueMirror myObjectValueMirror;

	public DotNetFieldMirrorNode(FieldMirror fieldMirror, @NotNull Project project, @Nullable ObjectValueMirror objectValueMirror)
	{
		super(fieldMirror.name(), project);
		myFieldMirror = fieldMirror;
		myObjectValueMirror = objectValueMirror;
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myFieldMirror.type();
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		return AllIcons.Nodes.Field;
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariable()
	{
		return myFieldMirror.value(myObjectValueMirror);
	}
}
