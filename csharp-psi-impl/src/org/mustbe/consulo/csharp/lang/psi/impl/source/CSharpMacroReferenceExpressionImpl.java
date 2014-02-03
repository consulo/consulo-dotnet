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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.CSharpLookupElementBuilder;
import org.mustbe.consulo.csharp.lang.psi.CSharpMacroDefine;
import org.mustbe.consulo.csharp.lang.psi.CSharpMacroElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.impl.light.CSharpLightMacroDefine;
import org.mustbe.consulo.dotnet.module.MainConfigurationProfileEx;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import lombok.val;

/**
 * @author VISTALL
 * @since 24.01.14
 */
public class CSharpMacroReferenceExpressionImpl extends CSharpMacroElementImpl implements CSharpMacroExpression, PsiReference
{
	public CSharpMacroReferenceExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public PsiReference getReference()
	{
		return this;
	}

	@Override
	public void accept(@NotNull CSharpMacroElementVisitor visitor)
	{
		visitor.visitReferenceExpression(this);
	}

	@Override
	public PsiElement getElement()
	{
		return this;
	}

	@Override
	public TextRange getRangeInElement()
	{
		PsiElement element = getElement();
		return new TextRange(0, element.getTextLength());
	}

	@Nullable
	@Override
	public PsiElement resolve()
	{
		PsiFile containingFile = getContainingFile();
		if(!(containingFile instanceof CSharpMacroFileImpl))
		{
			return null;
		}

		val text = getText();

		DotNetModuleExtension<?> extension = ModuleUtilCore.getExtension(containingFile, DotNetModuleExtension.class);
		if(extension != null)
		{
			MainConfigurationProfileEx currentProfileEx = extension.getCurrentProfileEx(MainConfigurationProfileEx.KEY);
			if(currentProfileEx.getVariables().contains(text))
			{
				return new CSharpLightMacroDefine(extension.getModule(), text);
			}
		}

		for(CSharpMacroDefine macroDefine : ((CSharpMacroFileImpl) containingFile).getDefines())
		{
			if(Comparing.equal(text, macroDefine.getName()))
			{
				return macroDefine;
			}
		}
		return null;
	}

	@NotNull
	@Override
	public String getCanonicalText()
	{
		return getText();
	}

	@Override
	public PsiElement handleElementRename(String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element)
	{
		return resolve() == element;
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
		List<CSharpMacroDefine> list = new ArrayList<CSharpMacroDefine>();

		DotNetModuleExtension<?> extension = ModuleUtilCore.getExtension(this, DotNetModuleExtension.class);
		if(extension != null)
		{
			MainConfigurationProfileEx<?> currentProfileEx = extension.getCurrentProfileEx(MainConfigurationProfileEx.KEY);
			for(String varName : currentProfileEx.getVariables())
			{
				list.add(new CSharpLightMacroDefine(extension.getModule(), varName));
			}
		}

		Collections.addAll(list, ((CSharpMacroFileImpl) getContainingFile()).getDefines());
		return CSharpLookupElementBuilder.getInstance(getProject()).buildToLookupElements(list);
	}

	@Override
	public boolean isSoft()
	{
		return true;
	}
}
