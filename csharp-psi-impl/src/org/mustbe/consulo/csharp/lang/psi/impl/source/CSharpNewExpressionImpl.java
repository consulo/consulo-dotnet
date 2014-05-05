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
import org.mustbe.consulo.csharp.lang.psi.CSharpFieldOrPropertySet;
import org.mustbe.consulo.csharp.lang.psi.CSharpFieldOrPropertySetBlock;
import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.csharp.lang.psi.CSharpNewExpression;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpArrayTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpQualifiedTypeRef;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpNewExpressionImpl extends CSharpElementImpl implements CSharpNewExpression
{
	private DotNetTypeRef myAnonymType;

	public CSharpNewExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitNewExpression(this);
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromParent)
	{
		DotNetType type = getNewType();
		if(type == null)
		{
			if(myAnonymType == null)
			{
				myAnonymType = createAnonymType();
			}

			return myAnonymType == null ? DotNetTypeRef.ERROR_TYPE : myAnonymType;
		}
		else
		{
			DotNetTypeRef typeRef = type.toTypeRef();
			for(PsiElement ignored : findChildrenByType(CSharpTokens.LBRACKET))
			{
				typeRef = new CSharpArrayTypeRef(typeRef, 0);
			}
			return typeRef;
		}
	}

	private DotNetTypeRef createAnonymType()
	{
		CSharpFieldOrPropertySetBlock fieldOrPropertySetBlock = getFieldOrPropertySetBlock();
		if(fieldOrPropertySetBlock != null)
		{
			CSharpFieldOrPropertySet[] sets = fieldOrPropertySetBlock.getSets();

			StringBuilder builder = new StringBuilder();
			builder.append("public struct Anonym {");
			for(CSharpFieldOrPropertySet set : sets)
			{
				DotNetExpression nameReferenceExpression = set.getNameReferenceExpression();
				DotNetExpression valueReferenceExpression = set.getValueReferenceExpression();

				builder.append("public readonly ");
				if(valueReferenceExpression == null)
				{
					builder.append(DotNetTypes.System_Object);
				}
				else
				{
					builder.append(valueReferenceExpression.toTypeRef(true).getQualifiedText());
				}
				builder.append(" ");
				builder.append(nameReferenceExpression.getText());
				builder.append(";\n");
			}
			builder.append("}");

			DotNetTypeDeclaration typeDeclaration = CSharpFileFactory.createTypeDeclaration(getProject(), getResolveScope(), builder.toString());
			return new CSharpQualifiedTypeRef(typeDeclaration);
		}

		return null;
	}

	@Nullable
	@Override
	public DotNetType getNewType()
	{
		return findChildByClass(DotNetType.class);
	}

	@Nullable
	@Override
	public CSharpFieldOrPropertySetBlock getFieldOrPropertySetBlock()
	{
		return findChildByClass(CSharpFieldOrPropertySetBlock.class);
	}
}
