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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class CSharpMacroBlockStopImpl extends CSharpElementImpl
{
	public CSharpMacroBlockStopImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public IElementType findStopElementType()
	{
		TokenSet tokenSet = TokenSet.create(CSharpTokens.MACRO_ENDIF_KEYWORD, CSharpTokens.MACRO_ENDREGION_KEYWORD);
		PsiElement notNullChildByType = findNotNullChildByType(tokenSet);
		return notNullChildByType.getNode().getElementType();
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitMacroBlockStop(this);
	}
}