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

package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetNativeType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilNativeTypeStub;
import consulo.msil.impl.lang.psi.impl.type.MsilNativeTypeRefImpl;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilNativeTypeImpl extends MsilTypeImpl<MsilNativeTypeStub> implements DotNetNativeType
{
	private static final Map<IElementType, String> ourTypes = new HashMap<IElementType, String>()
	{
		{
			put(MsilTokens.STRING_KEYWORD, "System.String");
			put(MsilTokens.OBJECT_KEYWORD, "System.Object");
			put(MsilTokens.INT_KEYWORD, "System.IntPtr");
			put(MsilTokens.UINT_KEYWORD, "System.UIntPtr");
			put(MsilTokens.CHAR_KEYWORD, "System.Char");
			put(MsilTokens.BOOL_KEYWORD, "System.Boolean");
			put(MsilTokens.INT8_KEYWORD, "System.SByte");
			put(MsilTokens.UINT8_KEYWORD, "System.Byte");
			put(MsilTokens.INT16_KEYWORD, "System.Int16");
			put(MsilTokens.UINT16_KEYWORD, "System.UInt16");
			put(MsilTokens.INT32_KEYWORD, "System.Int32");
			put(MsilTokens.UINT32_KEYWORD, "System.UInt32");
			put(MsilTokens.INT64_KEYWORD, "System.Int64");
			put(MsilTokens.UINT64_KEYWORD, "System.UInt64");
			put(MsilTokens.FLOAT32_KEYWORD, "System.Single");
			put(MsilTokens.FLOAT64_KEYWORD, "System.Double");
			put(MsilTokens.VOID_KEYWORD, "System.Void");
		}
	};

	public MsilNativeTypeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilNativeTypeImpl(@Nonnull MsilNativeTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public PsiElement getTypeElement()
	{
		return findNotNullChildByType(MsilTokenSets.NATIVE_TYPES);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public IElementType getTypeElementType()
	{
		IElementType elementType = null;
		MsilNativeTypeStub stub = getGreenStub();
		if(stub != null)
		{
			elementType = stub.getTypeElementType();
		}
		else
		{
			elementType = getTypeElement().getNode().getElementType();
		}
		return elementType;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRefImpl()
	{
		IElementType elementType = null;
		MsilNativeTypeStub stub = getGreenStub();
		if(stub != null)
		{
			elementType = stub.getTypeElementType();
		}
		else
		{
			elementType = getTypeElement().getNode().getElementType();
		}
		String ref = ourTypes.get(elementType);
		assert ref != null : elementType.toString();
		return new MsilNativeTypeRefImpl(getProject(), getResolveScope(), ref);
	}
}
