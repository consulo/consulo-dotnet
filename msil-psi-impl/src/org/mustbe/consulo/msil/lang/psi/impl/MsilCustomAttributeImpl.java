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

package org.mustbe.consulo.msil.lang.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttribute;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttributeSignature;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilStubTokenSets;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilCustomAttributeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilCustomAttributeImpl extends MsilStubElementImpl<MsilCustomAttributeStub> implements MsilCustomAttribute
{
	public MsilCustomAttributeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilCustomAttributeImpl(@NotNull MsilCustomAttributeStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@Nullable
	public DotNetType getType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@NotNull
	@Override
	@RequiredReadAction
	public DotNetParameterList getParameterList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.PARAMETER_LIST);
	}

	@NotNull
	@Override
	@RequiredReadAction
	public MsilCustomAttributeSignature getSignature()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.CUSTOM_ATTRIBUTE_SIGNATURE);
	}

	@Nullable
	@Override
	public DotNetTypeDeclaration resolveToType()
	{
		DotNetType type = getType();
		if(type == null)
		{
			return null;
		}
		PsiElement resolve = type.toTypeRef().resolve(this).getElement();
		if(resolve instanceof DotNetTypeDeclaration)
		{
			return (DotNetTypeDeclaration) resolve;
		}
		return null;
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		DotNetType type = getType();
		if(type == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}
		return type.toTypeRef();
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitCustomAttribute(this);
	}
}
