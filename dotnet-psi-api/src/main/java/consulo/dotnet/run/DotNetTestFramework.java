/*
 * Copyright 2013-2015 must-be.org
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

import consulo.annotation.access.RequiredReadAction;
import consulo.component.extension.ExtensionPointName;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 19.12.2015
 */
public abstract class DotNetTestFramework
{
	public static final ExtensionPointName<DotNetTestFramework> EP_NAME = ExtensionPointName.create("consulo.dotnet.testFramework");

	@RequiredReadAction
	public boolean isTestType(@Nonnull DotNetTypeDeclaration typeDeclaration)
	{
		DotNetNamedElement[] members = typeDeclaration.getMembers();
		for(DotNetNamedElement member : members)
		{
			if(member instanceof DotNetLikeMethodDeclaration && isTestMethod((DotNetLikeMethodDeclaration) member))
			{
				return true;
			}
		}
		return false;
	}

	@RequiredReadAction
	public abstract boolean isTestMethod(@Nonnull DotNetLikeMethodDeclaration methodDeclaration);
}
