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

package consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayUtil;
import com.intellij.util.io.StringRef;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.dotnet.psi.DotNetTypeWithTypeArguments;
import consulo.dotnet.psi.DotNetUserType;
import consulo.msil.lang.psi.MsilStubElements;
import consulo.msil.lang.psi.impl.MsilTypeListImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilTypeListStub;
import consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeListStubElementType extends AbstractMsilStubElementType<MsilTypeListStub, DotNetTypeList>
{
	public MsilTypeListStubElementType(@Nonnull @NonNls String debugName)
	{
		super(debugName);
	}

	@Nonnull
	@Override
	public DotNetTypeList createElement(@Nonnull ASTNode astNode)
	{
		return new MsilTypeListImpl(astNode);
	}

	@Nonnull
	@Override
	public DotNetTypeList createPsi(@Nonnull MsilTypeListStub msilTypeListStub)
	{
		return new MsilTypeListImpl(msilTypeListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilTypeListStub createStub(@Nonnull DotNetTypeList dotNetTypeList, StubElement stubElement)
	{
		DotNetType[] types = dotNetTypeList.getTypes();
		List<String> typeRefs = new ArrayList<>(types.length);
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
		return new MsilTypeListStub(stubElement, this, ArrayUtil.toStringArray(typeRefs));
	}

	private void collectTypeRefs(List<String> typeRefs, DotNetType type)
	{
		if(type instanceof DotNetUserType)
		{
			String referenceText = ((DotNetUserType) type).getReferenceText();
			referenceText = StringUtil.unquoteString(referenceText, '\'');
			referenceText = referenceText.replace('/', '.');
			referenceText = StringUtil.getShortName(referenceText);
			typeRefs.add(referenceText);
		}
	}

	@Override
	public void serialize(@Nonnull MsilTypeListStub msilTypeListStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		String[] references = msilTypeListStub.geShortReferences();
		stubOutputStream.writeByte(references.length);
		for(String reference : references)
		{
			stubOutputStream.writeName(reference);
		}
	}

	@Nonnull
	@Override
	public MsilTypeListStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		byte value = inputStream.readByte();
		String[] strings = new String[value];
		for(int i = 0; i < value; i++)
		{
			strings[i] = StringRef.toString(inputStream.readName());
		}
		return new MsilTypeListStub(stubElement, this, strings);
	}

	@Override
	public void indexStub(@Nonnull MsilTypeListStub msilTypeListStub, @Nonnull IndexSink indexSink)
	{
		if(this == MsilStubElements.EXTENDS_TYPE_LIST || this == MsilStubElements.IMPLEMENTS_TYPE_LIST)
		{
			for(String ref : msilTypeListStub.geShortReferences())
			{
				indexSink.occurrence(MsilIndexKeys.EXTENDS_LIST_INDEX, ref);
			}
		}
	}
}
