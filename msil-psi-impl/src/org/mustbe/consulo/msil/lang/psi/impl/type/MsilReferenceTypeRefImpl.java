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

package org.mustbe.consulo.msil.lang.psi.impl.type;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeRefImpl extends DotNetTypeRef.Adapter
{
	private final Project myProject;
	private final String myRef;
	private final String myNestedRef;
	private final DotNetPsiFacade.TypeResoleKind myTypeResoleKind;

	public MsilReferenceTypeRefImpl(Project project, String ref, String nestedRef, DotNetPsiFacade.TypeResoleKind typeResoleKind)
	{
		myProject = project;
		myRef = ref;
		myNestedRef = nestedRef;
		myTypeResoleKind = typeResoleKind;
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		return StringUtil.getShortName(myRef);
	}

	@NotNull
	@Override
	public String getQualifiedText()
	{
		return myRef;
	}

	@Nullable
	@Override
	public PsiElement resolve(@NotNull PsiElement scope)
	{
		if(DumbService.isDumb(scope.getProject()))
		{
			return null;
		}
		final Collection<MsilClassEntry> elements = StubIndex.getElements(MsilIndexKeys.TYPE_BY_QNAME_INDEX, myRef, myProject,
				scope.getResolveScope(), MsilClassEntry.class);

		Collection<MsilClassEntry> forSearch = elements;
		if(forSearch.isEmpty())
		{
			return null;
		}

		if(!StringUtil.isEmpty(myNestedRef))
		{
			forSearch = new SmartList<MsilClassEntry>();
			for(MsilClassEntry element : elements)
			{
				for(DotNetNamedElement dotNetNamedElement : element.getMembers())
				{
					if(dotNetNamedElement instanceof MsilClassEntry && Comparing.equal(dotNetNamedElement.getName(), myNestedRef))
					{
						forSearch.add((MsilClassEntry) dotNetNamedElement);
					}
				}
			}
		}

		for(MsilClassEntry type : forSearch)
		{
			switch(myTypeResoleKind)
			{
				case CLASS:
					if(type.isStruct())
					{
						continue;
					}
					break;
				case STRUCT:
					if(!type.isStruct())
					{
						continue;
					}
					break;
			}
			return type;
		}
		return null;
	}
}
