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

package org.mustbe.consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.dotnet.psi.DotNetTypeWithTypeArguments;
import consulo.dotnet.psi.DotNetUserType;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.impl.MsilTypeListImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilTypeListStub;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeListStubElementType extends AbstractMsilStubElementType<MsilTypeListStub, DotNetTypeList>
{
	public MsilTypeListStubElementType(@NotNull @NonNls String debugName)
	{
		super(debugName);
	}

	@NotNull
	@Override
	public DotNetTypeList createElement(@NotNull ASTNode astNode)
	{
		return new MsilTypeListImpl(astNode);
	}

	@NotNull
	@Override
	public DotNetTypeList createPsi(@NotNull MsilTypeListStub msilTypeListStub)
	{
		return new MsilTypeListImpl(msilTypeListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilTypeListStub createStub(@NotNull DotNetTypeList dotNetTypeList, StubElement stubElement)
	{
		DotNetType[] types = dotNetTypeList.getTypes();
		List<StringRef> typeRefs = new ArrayList<StringRef>(types.length);
		for(DotNetType type : types)
		{
			if(type instanceof DotNetTypeWithTypeArguments)
			{
				collectTypeRefs(typeRefs, ((DotNetTypeWithTypeArguments) type).getInnerType());
			}
			else
			{
				collectTypeRefs(typeRefs, type);
			}
		}
		return new MsilTypeListStub(stubElement, this, ContainerUtil.toArray(typeRefs, StringRef.EMPTY_ARRAY));
	}

	private void collectTypeRefs(List<StringRef> typeRefs, DotNetType type)
	{
		if(type instanceof DotNetUserType)
		{
			String referenceText = ((DotNetUserType) type).getReferenceText();
			referenceText = StringUtil.unquoteString(referenceText, '\'');
			referenceText = referenceText.replace('/', '.');
			referenceText = StringUtil.getShortName(referenceText);
			typeRefs.add(StringRef.fromString(referenceText));
		}
	}

	@Override
	public void serialize(@NotNull MsilTypeListStub msilTypeListStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		String[] references = msilTypeListStub.geShortReferences();
		stubOutputStream.writeByte(references.length);
		for(String reference : references)
		{
			stubOutputStream.writeName(reference);
		}
	}

	@NotNull
	@Override
	public MsilTypeListStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		byte value = inputStream.readByte();
		StringRef[] refs = new StringRef[value];
		for(int i = 0; i < value; i++)
		{
			refs[i] = inputStream.readName();
		}
		return new MsilTypeListStub(stubElement, this, refs);
	}

	@Override
	public void indexStub(@NotNull MsilTypeListStub msilTypeListStub, @NotNull IndexSink indexSink)
	{
		if(this == MsilStubElements.EXTENDS_TYPE_LIST || this == MsilStubElements.IMPLEMENTS_TYPE_LIST)
		{
			for(String s : msilTypeListStub.geShortReferences())
			{
				indexSink.occurrence(MsilIndexKeys.EXTENDS_LIST_INDEX, s);
			}
		}
	}
}
