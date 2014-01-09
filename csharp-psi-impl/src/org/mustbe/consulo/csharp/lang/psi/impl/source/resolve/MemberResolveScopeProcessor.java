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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;

/**
 * @author VISTALL
 * @since 17.12.13.
 */
public class MemberResolveScopeProcessor extends AbstractScopeProcessor
{
	private final Condition<PsiNamedElement> myCond;
	private final boolean myIncomplete;

	public MemberResolveScopeProcessor(Condition<PsiNamedElement> condition, boolean incomplete)
	{
		myCond = condition;
		myIncomplete = incomplete;
	}

	@Override
	public boolean execute(@NotNull PsiElement element, ResolveState state)
	{
		if(element instanceof DotNetNamedElement)
		{
			if(myCond.value((DotNetNamedElement) element))
			{
				addElement(element);
				return myIncomplete;
			}
		}
		return true;
	}
}
