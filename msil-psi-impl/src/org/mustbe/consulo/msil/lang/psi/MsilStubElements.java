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

package org.mustbe.consulo.msil.lang.psi;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.msil.lang.psi.impl.MsilArrayTypeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.MsilPointerTypeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.MsilTypeByRefImpl;
import org.mustbe.consulo.msil.lang.psi.impl.MsilTypeWithTypeArgumentsImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.*;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public interface MsilStubElements
{
	MsilFileStubElementType FILE = new MsilFileStubElementType();
	MsilClassStubElementType CLASS = new MsilClassStubElementType();
	MsilCustomAttributeStubElementType CUSTOM_ATTRIBUTE = new MsilCustomAttributeStubElementType();
	MsilMethodStubElementType METHOD = new MsilMethodStubElementType();
	MsilPropertyStubElementType PROPERTY = new MsilPropertyStubElementType();
	MsilEventStubElementType EVENT = new MsilEventStubElementType();
	MsilFieldStubElementType FIELD = new MsilFieldStubElementType();
	MsilAssemblyStubElementType ASSEMBLY = new MsilAssemblyStubElementType();
	MsilModifierListStubElementType MODIFIER_LIST = new MsilModifierListStubElementType();
	MsilTypeListStubElementType EXTENDS_TYPE_LIST = new MsilTypeListStubElementType("MSIL_EXTENDS_TYPE_LIST");
	MsilTypeListStubElementType IMPLEMENTS_TYPE_LIST = new MsilTypeListStubElementType("MSIL_IMPLEMENTS_TYPE_LIST");
	MsilTypeListStubElementType GENERIC_PARAM_EXTENDS_LIST = new MsilTypeListStubElementType("MSIL_GENERIC_PARAM_EXTENDS_LIST");
	MsilTypeListStubElementType TYPE_ARGUMENTS_TYPE_LIST = new MsilTypeListStubElementType("MSIL_TYPE_ARGUMENTS_TYPE_LIST");
	MsilNativeTypeStubElementType NATIVE_TYPE = new MsilNativeTypeStubElementType();
	MsilReferenceTypeStubElementType REFERENCE_TYPE = new MsilReferenceTypeStubElementType();
	MsilParameterListStubElementType PARAMETER_LIST = new MsilParameterListStubElementType();
	MsilParameterStubElementType PARAMETER = new MsilParameterStubElementType();
	MsilParameterAttributeListStubElementType PARAMETER_ATTRIBUTE_LIST = new MsilParameterAttributeListStubElementType();
	MsilTypeParameterAttributeListStubElementType TYPE_PARAMETER_ATTRIBUTE_LIST = new MsilTypeParameterAttributeListStubElementType();
	MsilMethodGenericTypeStubElementType METHOD_GENERIC_TYPE = new MsilMethodGenericTypeStubElementType();
	MsilClassGenericTypeStubElementType CLASS_GENERIC_TYPE = new MsilClassGenericTypeStubElementType();
	MsilGenericParameterListStubElementType GENERIC_PARAMETER_LIST = new MsilGenericParameterListStubElementType();
	MsilGenericParameterStubElementType GENERIC_PARAMETER = new MsilGenericParameterStubElementType();
	MsilXXXAccessorStubElementType XXX_ACCESSOR = new MsilXXXAccessorStubElementType();
	MsilArrayDimensionStubElementType ARRAY_DIMENSION = new MsilArrayDimensionStubElementType();
	MsilCustomAttributeSignatureElementType CUSTOM_ATTRIBUTE_SIGNATURE = new MsilCustomAttributeSignatureElementType();
	MsilConstantValueStubElementType CONSTANT_VALUE = new MsilConstantValueStubElementType();
	MsilEmptyTypeStubElementType POINTER_TYPE = new MsilEmptyTypeStubElementType("MSIL_POINTER_TYPE")
	{
		@NotNull
		@Override
		public DotNetType createElement(@NotNull ASTNode astNode)
		{
			return new MsilPointerTypeImpl(astNode);
		}

		@NotNull
		@Override
		public DotNetType createPsi(@NotNull MsilEmptyTypeStub msilEmptyTypeStub)
		{
			return new MsilPointerTypeImpl(msilEmptyTypeStub, this);
		}
	};
	MsilEmptyTypeStubElementType TYPE_BY_REF = new MsilEmptyTypeStubElementType("MSIL_TYPE_BY_REF")
	{
		@NotNull
		@Override
		public DotNetType createElement(@NotNull ASTNode astNode)
		{
			return new MsilTypeByRefImpl(astNode);
		}

		@NotNull
		@Override
		public DotNetType createPsi(@NotNull MsilEmptyTypeStub msilEmptyTypeStub)
		{
			return new MsilTypeByRefImpl(msilEmptyTypeStub, this);
		}
	};
	MsilEmptyTypeStubElementType TYPE_WITH_TYPE_ARGUMENTS = new MsilEmptyTypeStubElementType("MSIL_TYPE_WRAPPER_WITH_TYPE_ARGUMENTS")
	{
		@NotNull
		@Override
		public DotNetType createElement(@NotNull ASTNode astNode)
		{
			return new MsilTypeWithTypeArgumentsImpl(astNode);
		}

		@NotNull
		@Override
		public DotNetType createPsi(@NotNull MsilEmptyTypeStub msilEmptyTypeStub)
		{
			return new MsilTypeWithTypeArgumentsImpl(msilEmptyTypeStub, this);
		}
	};
	MsilEmptyTypeStubElementType ARRAY_TYPE = new MsilEmptyTypeStubElementType("ARRAY_TYPE")
	{
		@NotNull
		@Override
		public DotNetType createElement(@NotNull ASTNode astNode)
		{
			return new MsilArrayTypeImpl(astNode);
		}

		@NotNull
		@Override
		public DotNetType createPsi(@NotNull MsilEmptyTypeStub msilEmptyTypeStub)
		{
			return new MsilArrayTypeImpl(msilEmptyTypeStub, this);
		}
	};
}
