/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.packageSupport.Package;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.QualifiedName;

/**
 * @author VISTALL
 * @since 17.12.13.
 */
public class CollectScopeProcessor extends AbstractScopeProcessor
{
	private QualifiedName myQualifiedName;

	public CollectScopeProcessor(String qualifiedName)
	{
		myQualifiedName = qualifiedName == null ? QualifiedName.fromDottedString("") : QualifiedName.fromDottedString(qualifiedName);
	}

	@Override
	public boolean execute(@NotNull PsiElement element, ResolveState state)
	{
		if(element instanceof org.mustbe.consulo.packageSupport.Package)
		{
			QualifiedName qualifiedName = ((Package) element).getQualifiedName();
			addIfCan(qualifiedName, element);
		}
		else if(element instanceof DotNetTypeDeclaration)
		{
			String qName = ((DotNetTypeDeclaration) element).getQName();
			if(qName == null)
			{
				return true;
			}
			QualifiedName qualifiedName = QualifiedName.fromDottedString(qName);
			addIfCan(qualifiedName, element);
		}
		return true;
	}

	private void addIfCan(QualifiedName qualifiedName,  PsiElement element)
	{
		if(myQualifiedName.getComponentCount() == 0 && qualifiedName.getComponentCount() == 1)
		{
			myElements.add(element);
		}
		else if(qualifiedName.matchesPrefix(myQualifiedName))
		{
			myElements.add(element);
		}
	}
}
