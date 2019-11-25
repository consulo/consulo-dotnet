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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetParameterList;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilCustomAttribute;
import consulo.msil.lang.psi.MsilCustomAttributeSignature;
import consulo.msil.lang.psi.MsilStubElements;
import consulo.msil.lang.psi.MsilStubTokenSets;
import consulo.msil.lang.psi.impl.elementType.stub.MsilCustomAttributeStub;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilCustomAttributeImpl extends MsilStubElementImpl<MsilCustomAttributeStub> implements MsilCustomAttribute
{
	public MsilCustomAttributeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilCustomAttributeImpl(@Nonnull MsilCustomAttributeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@Nullable
	public DotNetType getType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public DotNetParameterList getParameterList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.PARAMETER_LIST);
	}

	@Nonnull
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
		PsiElement resolve = type.toTypeRef().resolve().getElement();
		if(resolve instanceof DotNetTypeDeclaration)
		{
			return (DotNetTypeDeclaration) resolve;
		}
		return null;
	}

	@Nonnull
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
