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

package org.mustbe.consulo.dotnet.psi.impl;

import org.consulo.lombok.annotations.ArrayFactoryFields;
import org.consulo.module.extension.ModuleExtension;
import org.consulo.psi.PsiPackage;
import org.consulo.psi.PsiPackageManager;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.lang.Language;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiPackageBase;
import com.intellij.util.ArrayFactory;

/**
 * @author VISTALL
 * @since 10.01.14
 */
@ArrayFactoryFields
public class DotNetPackage extends PsiPackageBase
{
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
	@NotNull
	@Override
	public Language getLanguage()
	{
		return Language.ANY;
	}
}
