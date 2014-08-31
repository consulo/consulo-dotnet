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

package org.mustbe.consulo.dotnet.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class DotNetTypeRefUtil
{
	@Nullable
	public static PsiElement resolve(@Nullable DotNetType type)
	{
		if(type == null)
		{
			return null;
		}

		DotNetTypeRef typeRef = type.toTypeRef();
		return typeRef.resolve(type);
	}

	@NotNull
	public static DotNetTypeRef[] toArray(@NotNull DotNetType[] arguments)
	{
		if(arguments.length == 0)
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}

		DotNetTypeRef[] rArguments = new DotNetTypeRef[arguments.length];
		for(int i = 0; i < arguments.length; i++)
		{
			DotNetType argument = arguments[i];
			rArguments[i] = argument.toTypeRef();
		}
		return rArguments;
	}
}
