package consulo.dotnet.debugger.nodes;

import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.StructValueMirror;

/**
 * @author VISTALL
 * @since 05.01.2016
 *
 * TODO [VISTALL] restore it
 */
public class DotNetStructValueInfo
{
	private StructValueMirror myValueMirror;
	@Nullable
	private DotNetAbstractVariableMirrorNode myParentNode;
	private FieldOrPropertyMirror myFieldOrPropertyMirror;
	private DotNetValueProxy myValue;

	/*public DotNetStructValueInfo(@NotNull StructValueMirror valueMirror,
			@Nullable DotNetAbstractVariableMirrorNode parentNode,
			@NotNull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@NotNull DotNetValueProxy value)
	{
		myValueMirror = valueMirror;
		myParentNode = parentNode;
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myValue = value;
	}      */

	public boolean canSetValue()
	{
		/*if(myParentNode == null)
		{
			return false;
		}
		// how set value for struct this object?
		return !(myParentNode instanceof DotNetThisAsStructValueMirrorNode);    */
		return false;
	}

	public DotNetValueProxy getValue()
	{
		return myValue;
	}

	public void setValue(DotNetValueProxy newValue)
	{
		/*assert myParentNode != null;

		StructValueMirror valueMirror = myValueMirror;

		Map<FieldOrPropertyMirror, Value<?>> map = valueMirror.map();

		if(map.put(myFieldOrPropertyMirror, newValue) == null)
		{
			return;
		}

		myValue = newValue;

		StructValueMirror newStructValue = new StructValueMirror(valueMirror.virtualMachine(), valueMirror.type(), map.values().toArray(new Value[map.size()]));

		myParentNode.setValueForVariable(newStructValue);   */
	}
}
