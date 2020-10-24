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

package consulo.dotnet;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetMethodDeclaration;
import consulo.dotnet.psi.DotNetModifier;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetArrayTypeRef;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeRefUtil;

/**
 * @author VISTALL
 * @since 04.01.14.
 */
public class DotNetRunUtil
{
	@RequiredReadAction
	public static boolean hasEntryPoint(DotNetTypeDeclaration typeDeclaration)
	{
		for(DotNetNamedElement dotNetNamedElement : typeDeclaration.getMembers())
		{
			if(dotNetNamedElement instanceof DotNetMethodDeclaration && isEntryPoint((DotNetMethodDeclaration) dotNetNamedElement))
			{
				return true;
			}
		}
		return false;
	}

	@RequiredReadAction
	public static boolean isEntryPoint(DotNetMethodDeclaration methodDeclaration)
	{
		if(!methodDeclaration.hasModifier(DotNetModifier.STATIC))
		{
			return false;
		}
		String name = methodDeclaration.getName();
		if(!"Main".equals(name))
		{
			return false;
		}
		DotNetTypeRef[] parameterTypesForRuntime = methodDeclaration.getParameterTypeRefs();
		if(parameterTypesForRuntime.length == 0)
		{
			return true;
		}

		if(parameterTypesForRuntime.length != 1)
		{
			return false;
		}
		DotNetTypeRef dotNetTypeRef = parameterTypesForRuntime[0];
		return dotNetTypeRef instanceof DotNetArrayTypeRef && DotNetTypeRefUtil.isVmQNameEqual(((DotNetArrayTypeRef) dotNetTypeRef).getInnerTypeRef(), DotNetTypes.System.String);
	}
}
