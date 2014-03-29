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

package org.mustbe.consulo.nunit;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetAttributeUtil;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetModifierListOwner;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.nunit.module.extension.NUnitModuleExtension;
import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.lang.Language;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import com.intellij.testIntegration.TestFramework;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class NUnitTestFramework implements TestFramework
{
	@NotNull
	@Override
	public String getName()
	{
		return "NUnit";
	}

	@NotNull
	@Override
	public Icon getIcon()
	{
		return NUnitIcons.NUnit;
	}

	@Override
	public boolean isLibraryAttached(@NotNull Module module)
	{
		return false;
	}

	@NotNull
	@Override
	public String getLibraryPath()
	{
		return "";
	}

	@Nullable
	@Override
	public String getDefaultSuperClass()
	{
		return null;
	}

	@Override
	public boolean isTestClass(@NotNull PsiElement element)
	{
		NUnitModuleExtension extension = ModuleUtilCore.getExtension(element, NUnitModuleExtension.class);
		if(extension == null)
		{
			return false;
		}

		if(element instanceof DotNetTypeDeclaration)
		{
			return DotNetAttributeUtil.hasAttribute((DotNetTypeDeclaration) element, NUnitTypes.TestFixtureAttribute);
		}
		return false;
	}

	@Override
	public boolean isPotentialTestClass(@NotNull PsiElement element)
	{
		return false;
	}

	@Nullable
	@Override
	public PsiElement findSetUpMethod(@NotNull PsiElement element)
	{
		return null;
	}

	@Nullable
	@Override
	public PsiElement findTearDownMethod(@NotNull PsiElement element)
	{
		return null;
	}

	@Nullable
	@Override
	public PsiElement findOrCreateSetUpMethod(@NotNull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public FileTemplateDescriptor getSetUpMethodFileTemplateDescriptor()
	{
		return null;
	}

	@Override
	public FileTemplateDescriptor getTearDownMethodFileTemplateDescriptor()
	{
		return null;
	}

	@Override
	public FileTemplateDescriptor getTestMethodFileTemplateDescriptor()
	{
		return null;
	}

	@Override
	public boolean isIgnoredMethod(PsiElement element)
	{
		return false;
	}

	@Override
	public boolean isTestMethod(PsiElement element)
	{
		NUnitModuleExtension extension = ModuleUtilCore.getExtension(element, NUnitModuleExtension.class);
		if(extension == null)
		{
			return false;
		}

		if(element instanceof DotNetMethodDeclaration)
		{
			return DotNetAttributeUtil.hasAttribute((DotNetModifierListOwner) element, NUnitTypes.TestAttribute);
		}
		return false;
	}

	@NotNull
	@Override
	public Language getLanguage()
	{
		return Language.ANY;
	}
}
