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

package org.mustbe.consulo.dotnet;

import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.util.Comparing;

/**
 * @author VISTALL
 * @since 04.01.14.
 */
public class DotNetRunUtil
{
	private static final String STRING_ARRAY = DotNetTypes.System_String + "[]";

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
		DotNetTypeRef[] parameterTypesForRuntime = methodDeclaration.getParameterTypesForRuntime();
		if(parameterTypesForRuntime.length == 0)
		{
			return true;
		}

		if(parameterTypesForRuntime.length != 1)
		{
			return false;
		}
		return Comparing.equal(parameterTypesForRuntime[0].getQualifiedText(), STRING_ARRAY);
	}
}
