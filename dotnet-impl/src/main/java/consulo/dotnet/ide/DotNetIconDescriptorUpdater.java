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

package consulo.dotnet.ide;

import javax.annotation.Nonnull;

import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.run.DotNetTestFrameworks;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredReadAction;
import consulo.ide.IconDescriptor;
import consulo.ide.IconDescriptorUpdater;

/**
 * @author VISTALL
 * @since 29.03.14
 */
public class DotNetIconDescriptorUpdater implements IconDescriptorUpdater
{
	@RequiredReadAction
	@Override
	public void updateIcon(@Nonnull IconDescriptor iconDescriptor, @Nonnull PsiElement element, int flags)
	{
		if(element instanceof DotNetTypeDeclaration)
		{
			if(DotNetTestFrameworks.isTestType((DotNetTypeDeclaration) element))
			{
				iconDescriptor.addLayerIcon(AllIcons.RunConfigurations.TestMark);
			}
		}
		else if(element instanceof DotNetLikeMethodDeclaration)
		{
			if(DotNetTestFrameworks.isTestMethod((DotNetLikeMethodDeclaration) element))
			{
				iconDescriptor.addLayerIcon(AllIcons.RunConfigurations.TestMark);
			}
		}
	}
}
