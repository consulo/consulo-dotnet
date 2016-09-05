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

package org.mustbe.consulo.dotnet.run;

import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class DotNetConfigurationProducer extends RunConfigurationProducer<DotNetConfiguration>
{
	public DotNetConfigurationProducer()
	{
		super(DotNetConfigurationType.getInstance().getConfigurationFactories()[0]);
	}

	@Override
	protected boolean setupConfigurationFromContext(DotNetConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement)
	{
		Trinity<Module, String, PsiFile> contextInfo = getModuleForRun(context);
		if(contextInfo != null)
		{
			configuration.setName(contextInfo.getFirst().getName());
			configuration.setModule(contextInfo.getFirst());
			configuration.setWorkingDirectory(contextInfo.getSecond());
			sourceElement.set(contextInfo.getThird());
			return true;
		}
		return false;
	}

	@Override
	public boolean isConfigurationFromContext(DotNetConfiguration configuration, ConfigurationContext context)
	{
		Trinity<Module, String, PsiFile> contextInfo = getModuleForRun(context);
		if(contextInfo == null)
		{
			return false;
		}

		return configuration.getConfigurationModule().getModule() == contextInfo.getFirst();
	}

	private Trinity<Module, String, PsiFile> getModuleForRun(ConfigurationContext configurationContext)
	{
		PsiElement psiLocation = configurationContext.getPsiLocation();
		if(psiLocation == null)
		{
			return null;
		}

		Module module = configurationContext.getModule();
		if(module == null)
		{
			return null;
		}
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(extension != null)
		{
			if(extension.getTarget() == DotNetTarget.EXECUTABLE || extension.getTarget() == DotNetTarget.WIN_EXECUTABLE)
			{
				PsiFile containingFile = psiLocation.getContainingFile();
				if(containingFile == null)
				{
					return null;
				}

				DotNetModuleLangExtension langExtension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
				if(langExtension == null || langExtension.getFileType() != containingFile.getFileType())
				{
					return null;
				}
				return Trinity.create(module, extension.getOutputDir(), containingFile);
			}
			return null;
		}
		return null;
	}
}
