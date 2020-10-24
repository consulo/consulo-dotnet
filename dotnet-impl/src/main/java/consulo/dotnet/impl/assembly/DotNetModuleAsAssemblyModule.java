/*
 * Copyright 2013-2020 consulo.io
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

package consulo.dotnet.impl.assembly;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.assembly.AssemblyModule;
import consulo.dotnet.resolve.DotNetTypeRefUtil;
import consulo.internal.dotnet.asm.signature.TypeSignature;
import consulo.msil.lang.psi.MsilAssemblyEntry;
import consulo.msil.lang.psi.MsilCustomAttribute;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.lang.stubbing.MsilCustomAttributeArgumentList;
import consulo.msil.lang.stubbing.MsilCustomAttributeStubber;
import consulo.msil.lang.stubbing.values.MsiCustomAttributeValue;
import consulo.vfs.util.ArchiveVfsUtil;
import gnu.trove.THashSet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author VISTALL
 * @since 14-Jun-17
 */
class DotNetModuleAsAssemblyModule implements AssemblyModule
{
	private final Project myProject;
	private final VirtualFile myModuleFile;

	DotNetModuleAsAssemblyModule(Project project, VirtualFile moduleFile)
	{
		myProject = project;
		myModuleFile = moduleFile;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String getName()
	{
		return myModuleFile.getNameWithoutExtension();
	}

	@RequiredReadAction
	@Override
	public boolean isAllowedAssembly(@Nonnull String assemblyName)
	{
		return getBinaryAllowedAssemblies(myModuleFile).contains(assemblyName);
	}

	@Override
	public boolean equals(@Nonnull AssemblyModule module)
	{
		return module instanceof DotNetModuleAsAssemblyModule && myModuleFile.equals(((DotNetModuleAsAssemblyModule) module).myModuleFile);
	}

	@Nonnull
	private Set<String> getBinaryAllowedAssemblies(VirtualFile moduleFile)
	{
		return CachedValuesManager.getManager(myProject).getCachedValue(myProject, () -> CachedValueProvider.Result.create(calcBinaryAllowedAssemblies(myProject, moduleFile), VirtualFileManager
				.VFS_STRUCTURE_MODIFICATIONS));
	}

	@Nonnull
	@RequiredReadAction
	private static Set<String> calcBinaryAllowedAssemblies(Project project, VirtualFile moduleFile)
	{
		VirtualFile archiveRootForLocalFile = ArchiveVfsUtil.getArchiveRootForLocalFile(moduleFile);
		if(archiveRootForLocalFile == null)
		{
			return Collections.emptySet();
		}

		VirtualFile assemblyFile = archiveRootForLocalFile.findChild("AssemblyInfo.msil");
		if(assemblyFile == null)
		{
			return Collections.emptySet();
		}
		PsiFile file = PsiManager.getInstance(project).findFile(assemblyFile);
		if(!(file instanceof MsilFile))
		{
			return Collections.emptySet();
		}

		Set<String> assemblies = new THashSet<>();
		for(PsiElement psiElement : ((MsilFile) file).getMembers())
		{
			if(psiElement instanceof MsilAssemblyEntry)
			{
				MsilAssemblyEntry assemblyEntry = (MsilAssemblyEntry) psiElement;

				MsilCustomAttribute[] attributes = assemblyEntry.getAttributes();
				for(MsilCustomAttribute attribute : attributes)
				{
					if(DotNetTypeRefUtil.isVmQNameEqual(attribute.toTypeRef(), DotNetTypes.System.Runtime.CompilerServices.InternalsVisibleToAttribute))
					{
						MsilCustomAttributeArgumentList argumentList = MsilCustomAttributeStubber.build(attribute);
						List<MsiCustomAttributeValue> constructorArguments = argumentList.getConstructorArguments();
						if(constructorArguments.size() != 1)
						{
							continue;
						}

						MsiCustomAttributeValue msiCustomAttributeValue = constructorArguments.get(0);
						if(msiCustomAttributeValue.getTypeSignature() == TypeSignature.STRING)
						{
							assemblies.add((String) msiCustomAttributeValue.getValue());
						}
					}
				}
			}
		}
		return assemblies;
	}
}
