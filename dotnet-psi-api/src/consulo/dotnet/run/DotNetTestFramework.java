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

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.extensions.ExtensionPointName;

/**
 * @author VISTALL
 * @since 19.12.2015
 */
public abstract class DotNetTestFramework
{
	public static final ExtensionPointName<DotNetTestFramework> EP_NAME = new ExtensionPointName<>("consulo.dotnet.testFramework");

	@RequiredReadAction
	public boolean isTestType(@NotNull DotNetTypeDeclaration typeDeclaration)
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
	public abstract boolean isTestMethod(@NotNull DotNetLikeMethodDeclaration methodDeclaration);
}
