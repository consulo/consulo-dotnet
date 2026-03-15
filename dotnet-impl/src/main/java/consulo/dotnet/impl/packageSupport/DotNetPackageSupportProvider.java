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

package consulo.dotnet.impl.packageSupport;

import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.impl.psi.impl.DotNetPackage;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.language.psi.PsiManager;
import consulo.language.psi.PsiPackage;
import consulo.language.psi.PsiPackageManager;
import consulo.language.psi.PsiPackageSupportProvider;
import consulo.module.Module;
import consulo.module.extension.ModuleExtension;


/**
 * @author VISTALL
 * @since 10.01.14
 */
@ExtensionImpl
public class DotNetPackageSupportProvider implements PsiPackageSupportProvider
{
	@Override
	public boolean isSupported(ModuleExtension moduleExtension)
	{
		return moduleExtension instanceof DotNetModuleExtension && ((DotNetModuleExtension) moduleExtension).isAllowSourceRoots();
	}

	@Override
	public boolean isValidPackageName(Module module, String packageName)
	{
		return true;
	}

	@Override
	public PsiPackage createPackage(PsiManager psiManager, PsiPackageManager psiPackageManager, Class<? extends
			ModuleExtension> aClass, String s)
	{
		return new DotNetPackage(psiManager, psiPackageManager, aClass, s);
	}
}
