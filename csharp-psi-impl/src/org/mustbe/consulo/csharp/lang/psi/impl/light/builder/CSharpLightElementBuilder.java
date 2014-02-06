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

package org.mustbe.consulo.csharp.lang.psi.impl.light.builder;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.light.LightElement;

/**
 * @author VISTALL
 * @since 06.02.14
 */
public abstract class CSharpLightElementBuilder<T extends CSharpLightElementBuilder<T>> extends LightElement
{
	private PsiElement myParent;

	public CSharpLightElementBuilder(PsiElement element)
	{
		super(element.getManager(), element.getLanguage());
	}

	public CSharpLightElementBuilder(Project project, Language language)
	{
		super(PsiManager.getInstance(project), language);
	}

	@SuppressWarnings("unchecked")
	public T withParent(PsiElement parent)
	{
		myParent = parent;
		return (T) this;
	}

	@Override
	public PsiFile getContainingFile()
	{
		return myParent == null ? null : myParent.getContainingFile();
	}

	@Override
	public PsiElement getParent()
	{
		return myParent;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}

	@Override
	public boolean isValid()
	{
		return true;
	}
}
