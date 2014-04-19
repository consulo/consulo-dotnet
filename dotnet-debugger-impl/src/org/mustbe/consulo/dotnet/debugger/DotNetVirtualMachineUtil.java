package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.dll.vfs.builder.util.XStubUtil;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 19.04.14
 */
public class DotNetVirtualMachineUtil
{
	@NotNull
	public static String toVMQualifiedName(DotNetTypeDeclaration qualifiedElement)
	{
		String presentableQName = qualifiedElement.getPresentableQName();
		int genericParametersCount = qualifiedElement.getGenericParametersCount();
		if(genericParametersCount > 0)
		{
			presentableQName = presentableQName + XStubUtil.GENERIC_MARKER_IN_NAME + genericParametersCount;
		}
		assert presentableQName != null;
		return presentableQName;
	}

	@NotNull
	public static String formatNameWithGeneric(@NotNull TypeMirror typeMirror)
	{
		StringBuilder builder = new StringBuilder();
		formatNameWithGeneric(builder, typeMirror);
		return builder.toString();
	}

	public static void formatNameWithGeneric(@NotNull StringBuilder builder, @NotNull TypeMirror typeMirror)
	{
		TypeMirror original = typeMirror.original();
		if(original == null)
		{
			builder.append(typeMirror.qualifiedName());
			return;
		}

		builder.append(XStubUtil.getUserTypeDefName(typeMirror.originalQualifiedName())); // cut to `
		builder.append("<");
		TypeMirror[] typeMirrors = typeMirror.genericArguments();
		for(int i = 0; i < typeMirrors.length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			TypeMirror mirror = typeMirrors[i];
			formatNameWithGeneric(builder, mirror);
		}
		builder.append(">");
	}
}
