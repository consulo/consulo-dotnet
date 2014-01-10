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

package org.mustbe.consulo.dotnet.resolve.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class DotNetPsiFacadeImpl extends DotNetPsiFacade
{
	private static final ExtensionPointName<DotNetPsiFacade> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.psi.facade");

	private final DotNetPsiFacade[] myFacades;

	public DotNetPsiFacadeImpl()
	{
		myFacades = EP_NAME.getExtensions();
	}

	@NotNull
	@Override
	public DotNetTypeDeclaration[] findTypes(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
	{
		List<DotNetTypeDeclaration> typeDeclarations = new ArrayList<DotNetTypeDeclaration>();
		for(DotNetPsiFacade facade : myFacades)
		{
			Collections.addAll(typeDeclarations, facade.findTypes(qName, searchScope, genericCount));
		}
		return typeDeclarations.isEmpty() ? DotNetTypeDeclaration.EMPTY_ARRAY : typeDeclarations.toArray(new DotNetTypeDeclaration[typeDeclarations
				.size()]);
	}

	@Nullable
	@Override
	public DotNetTypeDeclaration findType(@NotNull String qName, @NotNull GlobalSearchScope searchScope, int genericCount)
	{
		for(DotNetPsiFacade facade : myFacades)
		{
			DotNetTypeDeclaration type = facade.findType(qName, searchScope, genericCount);
			if(type != null)
			{
				return type;
			}
		}
		return null;
	}
}