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

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.compiler.DotNetMacroUtil;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import lombok.val;
import mono.debugger.AssemblyMirror;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 21.12.14
 */
public class DefaultDotNetDebugHelper implements DotNetDebugHelper
{
	@Override
	public TypeMirror findTypeMirrorFromAssemblies(String vmQualifiedName,
			@NotNull AssemblyMirror[] assemblies,
			@NotNull DotNetTypeDeclaration typeDeclaration)
	{
		File outputFile = getOutputFile(typeDeclaration);
		if(outputFile == null)
		{
			return null;
		}

		for(AssemblyMirror assembly : assemblies)
		{
			File file = new File(assembly.location());
			if(FileUtil.filesEqual(outputFile, file))
			{
				return assembly.findTypeByQualifiedName(vmQualifiedName, false);
			}
		}
		return null;
	}

	public static File getOutputFile(PsiElement element)
	{
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(element, DotNetModuleExtension.class);
		if(extension == null)
		{
			return null;
		}
		val exeFile = DotNetMacroUtil.expandOutputFile(extension);
		return new File(exeFile);
	}
}
