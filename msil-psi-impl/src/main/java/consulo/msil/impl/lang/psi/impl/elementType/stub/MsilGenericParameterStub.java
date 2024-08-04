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

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.DotNetModifier;
import consulo.index.io.StringRef;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.lang.psi.MsilGenericParameter;
import consulo.msil.lang.psi.MsilUserType;
import consulo.util.lang.BitUtil;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterStub extends StubBase<DotNetGenericParameter>
{
	private static final int COVARIANT = 1 << 0;
	private static final int CONTRAVARIANT = 1 << 1;
	private static final int HAS_DEFAULT_CONSTRUCTOR = 1 << 2;

	private String myName;
	private int myModifierMask;
	private int myTargetIndex;

	public MsilGenericParameterStub(StubElement parent, IStubElementType elementType, @Nullable StringRef name, int modifierMask, int targetIndex)
	{
		super(parent, elementType);
		myModifierMask = modifierMask;
		myTargetIndex = targetIndex;
		myName = StringRef.toString(name);
	}

	public MsilGenericParameterStub(StubElement parent, IStubElementType elementType, @Nullable String name, int modifierMask, @Nullable MsilUserType.Target typeKind)
	{
		super(parent, elementType);
		myModifierMask = modifierMask;
		myName = name;
		myTargetIndex = typeKind == null ? -1 : typeKind.ordinal();
	}

	@RequiredReadAction
	public static int toModifiers(MsilGenericParameter parameter)
	{
		int i = 0;
		i = BitUtil.set(i, COVARIANT, parameter.hasModifier(DotNetModifier.COVARIANT));
		i = BitUtil.set(i, CONTRAVARIANT, parameter.hasModifier(DotNetModifier.CONTRAVARIANT));
		i = BitUtil.set(i, HAS_DEFAULT_CONSTRUCTOR, parameter.hasDefaultConstructor());
		return i;
	}

	public int getModifierMask()
	{
		return myModifierMask;
	}

	public int getTargetIndex()
	{
		return myTargetIndex;
	}

	@Nullable
	public String getName()
	{
		return myName;
	}

	@Nullable
	public MsilUserType.Target getTarget()
	{
		return myTargetIndex == -1 ? null : MsilUserType.Target.VALUES[myTargetIndex];
	}

	public boolean hasDefaultConstructor()
	{
		return BitUtil.isSet(myModifierMask, HAS_DEFAULT_CONSTRUCTOR);
	}

	public boolean hasModifier(DotNetModifier modifier)
	{
		if(modifier == DotNetModifier.COVARIANT)
		{
			return BitUtil.isSet(myModifierMask, COVARIANT);
		}
		else if(modifier == DotNetModifier.CONTRAVARIANT)
		{
			return BitUtil.isSet(myModifierMask, CONTRAVARIANT);
		}
		return false;
	}
}
