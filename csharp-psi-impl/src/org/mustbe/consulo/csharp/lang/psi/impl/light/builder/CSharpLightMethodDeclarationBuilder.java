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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpModifier;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetModifierWithMask;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 08.05.14
 */
public class CSharpLightMethodDeclarationBuilder extends CSharpLightNamedElementBuilder<CSharpLightMethodDeclarationBuilder> implements
		CSharpMethodDeclaration
{
	private List<DotNetModifierWithMask> myModifiers = new ArrayList<DotNetModifierWithMask>();
	private List<DotNetParameter> myParameters = new ArrayList<DotNetParameter>();
	private String myParentQName;
	private DotNetTypeRef myReturnType;

	public CSharpLightMethodDeclarationBuilder(Project project)
	{
		super(project);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitMethodDeclaration(this);
	}

	@Override
	public boolean isDelegate()
	{
		return false;
	}

	@Override
	public boolean isOperator()
	{
		return false;
	}

	@Nullable
	@Override
	public IElementType getOperatorElementType()
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetType getReturnType()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetTypeRef getReturnTypeRef()
	{
		return myReturnType;
	}

	@Nullable
	@Override
	public PsiElement getCodeBlock()
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getGenericParameters()
	{
		return new DotNetGenericParameter[0];
	}

	@Override
	public int getGenericParametersCount()
	{
		return 0;
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifierWithMask modifier)
	{
		if(modifier == DotNetModifierWithMask.STATIC)
		{
			modifier = CSharpModifier.STATIC;
		}
		return myModifiers.contains(modifier);
	}

	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetTypeRef[] getParameterTypesForRuntime()
	{
		DotNetParameter[] parameters = getParameters();
		DotNetTypeRef[] typeRefs = new DotNetTypeRef[parameters.length];
		for(int i = 0; i < parameters.length; i++)
		{
			DotNetParameter parameter = parameters[i];
			typeRefs[i] = parameter.toTypeRef(false);
		}
		return typeRefs;
	}

	@Nullable
	@Override
	public DotNetParameterList getParameterList()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetParameter[] getParameters()
	{
		return ContainerUtil.toArray(myParameters, DotNetParameter.ARRAY_FACTORY);
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		PsiElement parent = getParent();
		if(parent instanceof DotNetQualifiedElement)
		{
			return ((DotNetQualifiedElement) parent).getPresentableQName();
		}
		return myParentQName;
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		String parentQName = getPresentableParentQName();
		if(StringUtil.isEmpty(parentQName))
		{
			return getName();
		}
		return parentQName + "." + getName();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return null;
	}

	@NotNull
	public CSharpLightMethodDeclarationBuilder withReturnType(DotNetTypeRef type)
	{
		myReturnType = type;
		return this;
	}

	@NotNull
	public CSharpLightMethodDeclarationBuilder withParentQName(String parentQName)
	{
		myParentQName = parentQName;
		return this;
	}

	@NotNull
	public CSharpLightMethodDeclarationBuilder addModifier(DotNetModifierWithMask modifierWithMask)
	{
		myModifiers.add(modifierWithMask);
		return this;
	}

	@NotNull
	public CSharpLightMethodDeclarationBuilder addParameter(DotNetParameter parameter)
	{
		if(parameter instanceof CSharpLightElementBuilder)
		{
			((CSharpLightElementBuilder) parameter).withParent(this);
		}
		myParameters.add(parameter);
		return this;
	}
}
