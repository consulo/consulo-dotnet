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

package consulo.msil.lang.psi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.ArrayUtil;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetAttribute;
import consulo.dotnet.psi.DotNetModifier;
import consulo.msil.lang.psi.*;
import consulo.msil.lang.psi.impl.elementType.stub.MsilModifierListStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilModifierListImpl extends MsilStubElementImpl<MsilModifierListStub> implements MsilModifierList
{
	private static Map<DotNetModifier, MsilModifierElementType> ourReplaceMap = new HashMap<DotNetModifier, MsilModifierElementType>()
	{
		{
			put(DotNetModifier.STATIC, MsilTokens.STATIC_KEYWORD);
			put(DotNetModifier.PRIVATE, MsilTokens.PRIVATE_KEYWORD);
			put(DotNetModifier.PUBLIC, MsilTokens.PUBLIC_KEYWORD);
			put(DotNetModifier.PROTECTED, MsilTokens.PROTECTED_KEYWORD);
			put(DotNetModifier.INTERNAL, MsilTokens.ASSEMBLY_KEYWORD);
			put(DotNetModifier.ABSTRACT, MsilTokens.ABSTRACT_KEYWORD);
			put(DotNetModifier.SEALED, MsilTokens.SEALED_KEYWORD);
		}
	};

	public MsilModifierListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilModifierListImpl(@NotNull MsilModifierListStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitModifierList(this);
	}

	@Override
	public void addModifier(@NotNull DotNetModifier modifier)
	{

	}

	@Override
	public void removeModifier(@NotNull DotNetModifier modifier)
	{

	}

	@NotNull
	@Override
	public DotNetModifier[] getModifiers()
	{
		List<DotNetModifier> modifiers = new ArrayList<>();
		for(MsilModifierElementType modifierElementType : MsilTokenSets.MODIFIERS_AS_ARRAY)
		{
			if(hasModifier(modifierElementType))
			{
				modifiers.add(modifierElementType);
			}
		}
		return modifiers.toArray(new DotNetModifier[modifiers.size()]);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetAttribute[] getAttributes()
	{
		PsiElement parentByStub = getParentByStub();
		if(parentByStub instanceof MsilClassEntry)
		{
			return ((MsilClassEntry) parentByStub).getAttributes();
		}
		else if(parentByStub instanceof MsilMethodEntry)
		{
			return ((MsilMethodEntry) parentByStub).getAttributes();
		}
		else if(parentByStub instanceof MsilFieldEntry)
		{
			return ((MsilFieldEntry) parentByStub).getAttributes();
		}
		else if(parentByStub instanceof MsilPropertyEntry)
		{
			return ((MsilPropertyEntry) parentByStub).getAttributes();
		}
		else if(parentByStub instanceof MsilParameter)
		{
			MsilParameterList parameterList = getStubOrPsiParentOfType(MsilParameterList.class);
			MsilMethodEntry methodEntry = getStubOrPsiParentOfType(MsilMethodEntry.class);

			assert parameterList != null;
			assert methodEntry != null;
			int i = ArrayUtil.indexOf(parameterList.getParameters(), parentByStub);
			assert i != -1;
			return methodEntry.getParameterAttributes(i);
		}

		return DotNetAttribute.EMPTY_ARRAY;
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		PsiElement parent = getParent();
		if(parent instanceof MsilClassEntry)
		{
			boolean nonNested = parent.getParent() instanceof MsilFile;

			// special case for internal
			if(modifier == DotNetModifier.INTERNAL && nonNested && hasModifier(MsilTokens.PRIVATE_KEYWORD))
			{
				return true;
			}

			// skip PRIVATE for non nested classes, due it internal modifier
			if((modifier == DotNetModifier.PRIVATE || modifier == MsilTokens.PRIVATE_KEYWORD) && nonNested)
			{
				return false;
			}
		}

		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return false;
		}

		MsilModifierListStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.hasModififer(elementType);
		}
		return hasModifierInTree(elementType);
	}

	@Override
	public boolean hasModifierInTree(@NotNull DotNetModifier modifier)
	{
		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return false;
		}
		return findChildByType(elementType) != null;
	}

	@Nullable
	@Override
	public PsiElement getModifierElement(DotNetModifier modifier)
	{
		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return null;
		}
		return findChildByType(elementType);
	}

	@NotNull
	@Override
	public List<PsiElement> getModifierElements(@NotNull DotNetModifier modifier)
	{
		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return Collections.emptyList();
		}
		return findChildrenByType(elementType);
	}

	@Override
	public PsiElement getParent()
	{
		return getParentByStub();
	}

	@Nullable
	private static MsilModifierElementType asMsilModifier(DotNetModifier modifier)
	{
		if(modifier instanceof MsilModifierElementType)
		{
			return (MsilModifierElementType) modifier;
		}
		else
		{
			MsilModifierElementType msilModifierElementType = ourReplaceMap.get(modifier);
			if(msilModifierElementType != null)
			{
				return msilModifierElementType;
			}
			return null;
		}
	}
}
