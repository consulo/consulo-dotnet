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

package consulo.msil.impl.lang.psi.impl.elementType.stub;

import consulo.dotnet.psi.DotNetModifier;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.impl.lang.psi.MsilModifierElementType;
import consulo.msil.lang.psi.MsilModifierList;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.util.collection.ArrayUtil;
import consulo.util.lang.BitUtil;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilModifierListStub extends StubBase<MsilModifierList>
{
	private final int myModifiers;

	public MsilModifierListStub(StubElement parent, IStubElementType elementType, MsilModifierList modifiers)
	{
		super(parent, elementType);

		int mod = 0;
		for(DotNetModifier dotNetModifier : modifiers.getModifiers())
		{
			mod |= BitUtil.set(mod, getMask((MsilModifierElementType) dotNetModifier), true);
		}
		myModifiers = mod;
	}

	public MsilModifierListStub(StubElement parent, IStubElementType elementType, int modifiers)
	{
		super(parent, elementType);
		myModifiers = modifiers;
	}

	public boolean hasModififer(MsilModifierElementType elementType)
	{
		return BitUtil.isSet(myModifiers, getMask(elementType));
	}

	private int getMask(MsilModifierElementType elementType)
	{
		int i = ArrayUtil.indexOf(MsilTokenSets.MODIFIERS_AS_ARRAY, elementType);
		assert i != -1;
		return 1 << i;
	}

	public int getModifiers()
	{
		return myModifiers;
	}
}
