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
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetModifierWithMask;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 08.05.14
 */
public class CSharpLightTypeDeclarationBuilder extends CSharpLightNamedElementBuilder<CSharpLightTypeDeclarationBuilder> implements
		CSharpTypeDeclaration
{
	private List<DotNetNamedElement> myMembers = new ArrayList<DotNetNamedElement>();
	private List<DotNetModifierWithMask> myModifiers = new ArrayList<DotNetModifierWithMask>();

	public CSharpLightTypeDeclarationBuilder(Project manager, Language language)
	{
		super(manager, language);
	}

	public CSharpLightTypeDeclarationBuilder(PsiElement element)
	{
		super(element);
	}

	@Override
	public boolean hasExtensions()
	{
		return false;
	}

	@Override
	public PsiElement getLeftBrace()
	{
		return null;
	}

	@Override
	public PsiElement getRightBrace()
	{
		return null;
	}

	@Override
	public boolean isInterface()
	{
		return false;
	}

	@Override
	public boolean isStruct()
	{
		return false;
	}

	@Override
	public boolean isEnum()
	{
		return false;
	}

	@Override
	public boolean isInheritAllowed()
	{
		return false;
	}

	@Nullable
	@Override
	public DotNetTypeList getExtendList()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetType[] getExtends()
	{
		return new DotNetType[0];
	}

	@Override
	public boolean isInheritor(@NotNull DotNetTypeDeclaration other, boolean deep)
	{
		return false;
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

	@NotNull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return ContainerUtil.toArray(myMembers, DotNetNamedElement.ARRAY_FACTORY);
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifierWithMask modifier)
	{
		return myModifiers.contains(modifier);
	}

	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		return null;
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return null;
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		return getName();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return null;
	}

	public void addModifier(DotNetModifierWithMask modifierWithMask)
	{
		myModifiers.add(modifierWithMask);
	}

	public void addMember(@NotNull DotNetNamedElement element)
	{
		if(element instanceof CSharpLightElementBuilder)
		{
			((CSharpLightElementBuilder) element).withParent(this);
		}
		myMembers.add(element);
	}
}
