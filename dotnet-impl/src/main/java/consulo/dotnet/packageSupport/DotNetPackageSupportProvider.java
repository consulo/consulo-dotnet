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

package consulo.dotnet.packageSupport;

import javax.annotation.Nonnull;

import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.psi.impl.DotNetPackage;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiManager;
import consulo.module.extension.ModuleExtension;
import consulo.psi.PsiPackage;
import consulo.psi.PsiPackageManager;
import consulo.psi.PsiPackageSupportProvider;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class DotNetPackageSupportProvider implements PsiPackageSupportProvider
{
	@Override
	public boolean isSupported(@Nonnull ModuleExtension moduleExtension)
	{
		return moduleExtension instanceof DotNetModuleExtension && ((DotNetModuleExtension) moduleExtension).isAllowSourceRoots();
	}

	@Override
	public boolean isValidPackageName(@Nonnull Module module, @Nonnull String packageName)
	{
		return true;
	}

	@Nonnull
	@Override
	public PsiPackage createPackage(@Nonnull PsiManager psiManager, @Nonnull PsiPackageManager psiPackageManager, @Nonnull Class<? extends
			ModuleExtension> aClass, @Nonnull String s)
	{
		return new DotNetPackage(psiManager, psiPackageManager, aClass, s);
	}
}
