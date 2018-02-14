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

package consulo.dotnet.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.lang.Language;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiPackageBase;
import com.intellij.util.ArrayFactory;
import consulo.annotations.RequiredReadAction;
import consulo.module.extension.ModuleExtension;
import consulo.psi.PsiPackage;
import consulo.psi.PsiPackageManager;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class DotNetPackage extends PsiPackageBase
{
	public static final DotNetPackage[] EMPTY_ARRAY = new DotNetPackage[0];

	public static ArrayFactory<DotNetPackage> ARRAY_FACTORY = new ArrayFactory<DotNetPackage>()
	{
		@Nonnull
		@Override
		public DotNetPackage[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetPackage[count];
		}
	};

	public DotNetPackage(PsiManager manager, PsiPackageManager packageManager, Class<? extends ModuleExtension> extensionClass, String qualifiedName)
	{
		super(manager, packageManager, extensionClass, qualifiedName);
	}

	@Override
	protected ArrayFactory<? extends PsiPackage> getPackageArrayFactory()
	{
		return DotNetPackage.ARRAY_FACTORY;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public Language getLanguage()
	{
		return Language.ANY;
	}
}
