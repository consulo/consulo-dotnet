/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.msil.MsilHelper;
import com.intellij.openapi.project.Project;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 19.04.14
 */
public class DotNetVirtualMachineUtil
{
	@NotNull
	public static DotNetTypeDeclaration[] findTypesByQualifiedName(@NotNull TypeMirror typeMirror, @NotNull DotNetDebugContext debugContext)
	{
		String qualifiedName = typeMirror.qualifiedName();
		Project project = debugContext.getProject();
		return DotNetPsiSearcher.getInstance(project).findTypes(qualifiedName, debugContext.getResolveScope(),
				DotNetPsiSearcher.TypeResoleKind.UNKNOWN);
	}

	@NotNull
	public static String toVMQualifiedName(DotNetTypeDeclaration qualifiedElement)
	{
		String vmQName = qualifiedElement.getVmQName();
		assert vmQName != null;
		return vmQName;
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
			builder.append(MsilHelper.prepareForUser(typeMirror.qualifiedName()));
			return;
		}

		builder.append(MsilHelper.prepareForUser(typeMirror.qualifiedName()));

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
