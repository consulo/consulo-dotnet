/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.lang.psi;

import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import org.mustbe.consulo.csharp.lang.psi.impl.source.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IElementTypeAsPsiFactory;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public interface CSharpElements
{
	IElementType NAMESPACE_DECLARATION = CSharpStubElements.NAMESPACE_DECLARATION;

	IElementType USING_LIST = new IElementTypeAsPsiFactory("USING_LIST", CSharpLanguage.INSTANCE, CSharpUsingListImpl.class);

	IElementType USING_STATEMENT = new IElementTypeAsPsiFactory("USING_STATEMENT", CSharpLanguage.INSTANCE, CSharpUsingStatementImpl.class);

	IElementType METHOD_DECLARATION = CSharpStubElements.METHOD_DECLARATION;

	IElementType CONSTRUCTOR_DECLARATION = CSharpStubElements.CONSTRUCTOR_DECLARATION;

	IElementType PARAMETER_LIST = new IElementTypeAsPsiFactory("PARAMETER_LIST", CSharpLanguage.INSTANCE, CSharpParameterListImpl.class);

	IElementType PARAMETER = new IElementTypeAsPsiFactory("PARAMETER", CSharpLanguage.INSTANCE, CSharpParameterImpl.class);

	IElementType TYPE_DECLARATION = CSharpStubElements.TYPE_DECLARATION;

	IElementType EVENT_DECLARATION = CSharpStubElements.EVENT_DECLARATION;

	IElementType XXX_ACCESSOR = new IElementTypeAsPsiFactory("XXX_ACCESSOR", CSharpLanguage.INSTANCE, CSharpXXXAccessorImpl.class);

	IElementType FIELD_DECLARATION = CSharpStubElements.FIELD_DECLARATION;

	IElementType LOCAL_VARIABLE = new IElementTypeAsPsiFactory("LOCAL_VARIABLE", CSharpLanguage.INSTANCE, CSharpLocalVariableImpl.class);

	IElementType PROPERTY_DECLARATION = CSharpStubElements.PROPERTY_DECLARATION;

	IElementType GENERIC_PARAMETER_LIST = new IElementTypeAsPsiFactory("GENERIC_PARAMETER_LIST", CSharpLanguage.INSTANCE,
			CSharpGenericParameterListImpl.class);

	IElementType GENERIC_PARAMETER = new IElementTypeAsPsiFactory("GENERIC_PARAMETER", CSharpLanguage.INSTANCE, CSharpGenericParameterImpl.class);

	IElementType GENERIC_CONSTRAINT_LIST = new IElementTypeAsPsiFactory("GENERIC_CONSTRAINT_LIST", CSharpLanguage.INSTANCE,
			CSharpGenericConstraintListImpl.class);

	IElementType GENERIC_CONSTRAINT = new IElementTypeAsPsiFactory("GENERIC_CONSTRAINT", CSharpLanguage.INSTANCE, CSharpGenericConstraintImpl.class);

	IElementType NEW_GENERIC_CONSTRAINT_VALUE = new IElementTypeAsPsiFactory("NEW_GENERIC_CONSTRAINT_VALUE", CSharpLanguage.INSTANCE,
			CSharpNewGenericConstraintValueImpl.class);

	IElementType REFERENCE_TYPE = new IElementTypeAsPsiFactory("REFERENCE_TYPE", CSharpLanguage.INSTANCE, CSharpReferenceTypeImpl.class);

	IElementType POINTER_TYPE = new IElementTypeAsPsiFactory("POINTER_TYPE", CSharpLanguage.INSTANCE, CSharpPointerTypeImpl.class);

	IElementType NATIVE_TYPE = new IElementTypeAsPsiFactory("NATIVE_TYPE", CSharpLanguage.INSTANCE, CSharpNativeTypeImpl.class);

	IElementType ARRAY_TYPE = new IElementTypeAsPsiFactory("ARRAY_TYPE", CSharpLanguage.INSTANCE, CSharpArrayTypeImpl.class);

	IElementType TYPE_WRAPPER_WITH_TYPE_ARGUMENTS = new IElementTypeAsPsiFactory("TYPE_WRAPPER_WITH_TYPE_ARGUMENTS", CSharpLanguage.INSTANCE,
			CSharpTypeWrapperWithTypeArgumentsImpl.class);

	IElementType CODE_BLOCK = new IElementTypeAsPsiFactory("CODE_BLOCK", CSharpLanguage.INSTANCE, CSharpCodeBlockImpl.class);

	IElementType MODIFIER_LIST = new IElementTypeAsPsiFactory("MODIFIER_LIST", CSharpLanguage.INSTANCE, CSharpModifierListImpl.class);

	IElementType EXTENDS_LIST = new IElementTypeAsPsiFactory("EXTENDS_LIST", CSharpLanguage.INSTANCE, CSharpTypeListImpl.class);

	IElementType CONSTANT_EXPRESSION = new IElementTypeAsPsiFactory("CONSTANT_EXPRESSION", CSharpLanguage.INSTANCE,
			CSharpConstantExpressionImpl.class);

	IElementType REFERENCE_EXPRESSION = new IElementTypeAsPsiFactory("REFERENCE_EXPRESSION", CSharpLanguage.INSTANCE,
			CSharpReferenceExpressionImpl.class);

	IElementType METHOD_CALL_EXPRESSION = new IElementTypeAsPsiFactory("METHOD_CALL_EXPRESSION", CSharpLanguage.INSTANCE,
			CSharpMethodCallExpressionImpl.class);

	IElementType TYPE_OF_EXPRESSION = new IElementTypeAsPsiFactory("TYPE_OF_EXPRESSION", CSharpLanguage.INSTANCE, CSharpTypeOfExpressionImpl.class);

	IElementType METHOD_CALL_PARAMETER_LIST = new IElementTypeAsPsiFactory("METHOD_CALL_PARAMETER_LIST", CSharpLanguage.INSTANCE,
			CSharpMethodCallParameterListImpl.class);

	IElementType LOCAL_VARIABLE_DECLARATION_STATEMENT = new IElementTypeAsPsiFactory("LOCAL_VARIABLE_DECLARATION_STATEMENT",
			CSharpLanguage.INSTANCE, CSharpLocalVariableDeclarationStatementImpl.class);

	IElementType MACRO_DEFINE = new IElementTypeAsPsiFactory("MACRO_DEFINE", CSharpLanguage.INSTANCE, CSharpMacroDefineImpl.class);

	IElementType MACRO_BLOCK_START = new IElementTypeAsPsiFactory("MACRO_BLOCK_START", CSharpLanguage.INSTANCE,
			CSharpMacroBlockStartImpl.class);

	IElementType MACRO_BLOCK = new IElementTypeAsPsiFactory("MACRO_BLOCK", CSharpLanguage.INSTANCE,
			CSharpMacroBlockImpl.class);

	IElementType MACRO_BLOCK_STOP = new IElementTypeAsPsiFactory("MACRO_BLOCK_STOP", CSharpLanguage.INSTANCE,
			CSharpMacroBlockStopImpl.class);

	IElementType MACRO_BODY = new IElementTypeAsPsiFactory("MACRO_BODY", CSharpLanguage.INSTANCE,
			CSharpMacroBodyImpl.class);

	IElementType EXPRESSION_STATEMENT = new IElementTypeAsPsiFactory("EXPRESSION_STATEMENT", CSharpLanguage.INSTANCE, CSharpExpressionStatementImpl.class);

	IElementType ATTRIBUTE_LIST = new IElementTypeAsPsiFactory("ATTRIBUTE_LIST", CSharpLanguage.INSTANCE, CSharpAttributeListImpl.class);

	IElementType ATTRIBUTE = new IElementTypeAsPsiFactory("ATTRIBUTE", CSharpLanguage.INSTANCE, CSharpAttributeImpl.class);
}
