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

package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclarationUtil;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 25.04.14
 */
@Deprecated
public class DotNetDebuggerUtil
{
	@NotNull
	public static String getVmQName(@NotNull TypeMirror typeMirror)
	{
		String fullName = typeMirror.fullName();

		// System.List`1[String]
		int i = fullName.indexOf('[');
		if(i != -1)
		{
			fullName = fullName.substring(0, i);
		}

		// change + to / separator
		fullName = fullName.replace('+', DotNetTypeDeclarationUtil.NESTED_SEPARATOR_IN_NAME);
		return fullName;
	}
}
