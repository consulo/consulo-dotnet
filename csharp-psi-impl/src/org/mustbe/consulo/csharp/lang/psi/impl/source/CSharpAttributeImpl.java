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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.dotnet.psi.DotNetAttribute;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 19.12.13.
 */
public class CSharpAttributeImpl extends CSharpElementImpl implements DotNetAttribute
{
	public CSharpAttributeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitAttribute(this);
	}

	@Nullable
	@Override
	public DotNetTypeDeclaration resolveToType()
	{
		DotNetExpression childByClass = findChildByClass(DotNetExpression.class);
		if(childByClass == null)
		{
			return null;
		}
		DotNetTypeRef dotNetTypeRef = childByClass.toTypeRef();

		PsiElement resolve = dotNetTypeRef.resolve(this);
		if(resolve instanceof DotNetTypeDeclaration)
		{
			return (DotNetTypeDeclaration) resolve;
		}
		else if(resolve != null)
		{
			return (DotNetTypeDeclaration) resolve.getParent();
		}
		return null;
	}
}
