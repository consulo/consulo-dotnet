package consulo.dotnet.debugger.nodes;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 05.01.2016
 */
public class DotNetStructValueInfo
{
	private DotNetStructValueProxy myValueMirror;
	@Nullable
	private DotNetAbstractVariableValueNode myParentNode;
	private DotNetFieldOrPropertyProxy myFieldOrPropertyMirror;
	private DotNetValueProxy myValue;

	public DotNetStructValueInfo(@NotNull DotNetStructValueProxy valueMirror,
			@Nullable DotNetAbstractVariableValueNode parentNode,
			@NotNull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@NotNull DotNetValueProxy value)
	{
		myValueMirror = valueMirror;
		myParentNode = parentNode;
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myValue = value;
	}

	public boolean canSetValue()
	{
		if(myParentNode == null)
		{
			return false;
		}
		// how set value for struct this object?
		return !(myParentNode instanceof DotNetThisAsStructValueNode);
	}

	public DotNetValueProxy getValue()
	{
		return myValue;
	}

	public void setValue(DotNetValueProxy newValue)
	{
		assert myParentNode != null;

		DotNetStructValueProxy valueMirror = myValueMirror;

		Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map = valueMirror.getValues();

		if(map.put(myFieldOrPropertyMirror, newValue) == null)
		{
			return;
		}

		myValue = newValue;

		DotNetStructValueProxy newStructValue = valueMirror.createNewStructValue(map);

		myParentNode.setValueForVariable(newStructValue);
	}
}
