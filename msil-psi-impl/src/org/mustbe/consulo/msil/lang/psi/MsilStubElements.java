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
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.msil.lang.psi.impl.MsilPointerTypeImpl;
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
	MsilStubFileElementType FILE = new MsilStubFileElementType();
	MsilClassStubElementType CLASS = new MsilClassStubElementType();
	MsilCustomAttributeStubElementType CUSTOM_ATTRIBUTE = new MsilCustomAttributeStubElementType();
	MsilMethodStubElementType METHOD = new MsilMethodStubElementType();
	MsilPropertyStubElementType PROPERTY = new MsilPropertyStubElementType();
	MsilEventStubElementType EVENT = new MsilEventStubElementType();
	MsilFieldStubElementType FIELD = new MsilFieldStubElementType();
	MsilModifierListStubElementType MODIFIER_LIST = new MsilModifierListStubElementType();
	MsilTypeListStubElementType EXTENDS_TYPE_LIST = new MsilTypeListStubElementType("MSIL_EXTENDS_TYPE_LIST");
	MsilTypeListStubElementType IMPLEMENTS_TYPE_LIST = new MsilTypeListStubElementType("MSIL_IMPLEMENTS_TYPE_LIST");
	MsilTypeListStubElementType TYPE_ARGUMENTS_TYPE_LIST = new MsilTypeListStubElementType("MSIL_TYPE_ARGUMENTS_TYPE_LIST");
	MsilNativeTypeStubElementType NATIVE_TYPE = new MsilNativeTypeStubElementType();
	MsilReferenceTypeStubElementType REFERENCE_TYPE = new MsilReferenceTypeStubElementType();
	MsilParameterListStubElementType PARAMETER_LIST = new MsilParameterListStubElementType();
	MsilParameterStubElementType PARAMETER = new MsilParameterStubElementType();
	MsilEmpyTypeStubElementType POINTER_TYPE = new MsilEmpyTypeStubElementType("MSIL_POINTER_TYPE")
	{
		@NotNull
		@Override
		public DotNetType createPsi(@NotNull ASTNode astNode)
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
	MsilEmpyTypeStubElementType TYPE_WITH_TYPE_ARGUMENTS = new MsilEmpyTypeStubElementType("MSIL_TYPE_WRAPPER_WITH_TYPE_ARGUMENTS")
	{
		@NotNull
		@Override
		public DotNetType createPsi(@NotNull ASTNode astNode)
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
}
