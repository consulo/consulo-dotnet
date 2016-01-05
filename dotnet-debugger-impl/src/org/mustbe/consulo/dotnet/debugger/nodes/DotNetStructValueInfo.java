package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.StructValueMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 05.01.2016
 */
public class DotNetStructValueInfo
{
	private StructValueMirror myValueMirror;
	private DotNetAbstractVariableMirrorNode myParentNode;
	private FieldOrPropertyMirror myFieldOrPropertyMirror;
	private Value<?> myValue;

	public DotNetStructValueInfo(@NotNull StructValueMirror valueMirror, DotNetAbstractVariableMirrorNode parentNode, @NotNull FieldOrPropertyMirror fieldOrPropertyMirror, @NotNull Value<?> value)
	{
		myValueMirror = valueMirror;
		myParentNode = parentNode;
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myValue = value;
	}

	public boolean canSetValue()
	{
		// how set value for struct this object?
		return !(myParentNode instanceof DotNetThisAsStructValueMirrorNode);
	}

	public Value<?> getValue()
	{
		return myValue;
	}

	public void setValue(Value<?> newValue)
	{
		StructValueMirror valueMirror = myValueMirror;

		Map<FieldOrPropertyMirror, Value<?>> map = valueMirror.map();

		if(map.put(myFieldOrPropertyMirror, newValue) == null)
		{
			return;
		}

		myValue = newValue;

		StructValueMirror newStructValue = new StructValueMirror(valueMirror.virtualMachine(), valueMirror.type(), map.values().toArray(new Value[map.size()]));

		myParentNode.setValueForVariable(newStructValue);
	}
}
