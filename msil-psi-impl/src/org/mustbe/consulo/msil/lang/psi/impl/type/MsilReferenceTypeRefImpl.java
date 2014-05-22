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
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeRefImpl extends DotNetTypeRef.Adapter
{
	private final Project myProject;
	private final String myRef;
	private final DotNetPsiFacade.TypeResoleKind myTypeResoleKind;

	public MsilReferenceTypeRefImpl(Project project, String ref, DotNetPsiFacade.TypeResoleKind typeResoleKind)
	{
		myProject = project;
		myRef = ref;
		myTypeResoleKind = typeResoleKind;
	}

	@Nullable
	@Override
	public PsiElement resolve(@NotNull PsiElement scope)
	{
		Collection<MsilClassEntry> elements = StubIndex.getElements(MsilIndexKeys.TYPE_BY_QNAME_INDEX, myRef, myProject, scope.getResolveScope(),
				MsilClassEntry.class);

		if(elements.isEmpty())
		{
			return null;
		}

		for(MsilClassEntry type : elements)
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
