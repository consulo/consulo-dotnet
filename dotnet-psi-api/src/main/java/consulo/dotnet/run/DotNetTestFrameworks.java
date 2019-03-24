/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.run;

import javax.annotation.Nonnull;

import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetTypeDeclaration;

/**
 * @author VISTALL
 * @since 17.01.2016
 */
public class DotNetTestFrameworks
{
	@RequiredReadAction
	public static boolean isTestType(@Nonnull DotNetTypeDeclaration typeDeclaration)
	{
		for(DotNetTestFramework framework : DotNetTestFramework.EP_NAME.getExtensionList())
		{
			if(framework.isTestType(typeDeclaration))
			{
				return true;
			}
		}
		return false;
	}

	@RequiredReadAction
	public static boolean isTestMethod(@Nonnull DotNetLikeMethodDeclaration methodDeclaration)
	{
		for(DotNetTestFramework framework : DotNetTestFramework.EP_NAME.getExtensionList())
		{
			if(framework.isTestMethod(methodDeclaration))
			{
				return true;
			}
		}
		return false;
	}
}
