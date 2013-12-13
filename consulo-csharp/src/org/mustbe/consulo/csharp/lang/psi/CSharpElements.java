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
	IElementType NAMESPACE_DECLARATION = new IElementTypeAsPsiFactory("NAMESPACE_DECLARATION", CSharpLanguage.INSTANCE,
			CSharpNamespaceDeclarationImpl.class);

	IElementType USING_LIST = new IElementTypeAsPsiFactory("USING_LIST", CSharpLanguage.INSTANCE, CSharpUsingListImpl.class);

	IElementType USING_STATEMENT = new IElementTypeAsPsiFactory("USING_STATEMENT", CSharpLanguage.INSTANCE, CSharpUsingStatementImpl.class);

	IElementType METHOD_DECLARATION = new IElementTypeAsPsiFactory("METHOD_DECLARATION", CSharpLanguage.INSTANCE, CSharpMethodDeclarationImpl.class);

	IElementType CONSTRUCTOR_DECLARATION = new IElementTypeAsPsiFactory("CONSTRUCTOR_DECLARATION", CSharpLanguage.INSTANCE,
			CSharpConstructorDeclarationImpl.class);

	IElementType PARAMETER_LIST = new IElementTypeAsPsiFactory("PARAMETER_LIST", CSharpLanguage.INSTANCE, CSharpParameterListImpl.class);

	IElementType PARAMETER = new IElementTypeAsPsiFactory("PARAMETER", CSharpLanguage.INSTANCE, CSharpParameterImpl.class);

	IElementType TYPE_DECLARATION = new IElementTypeAsPsiFactory("TYPE_DECLARATION", CSharpLanguage.INSTANCE, CSharpTypeDeclarationImpl.class);

	IElementType EVENT_DECLARATION = new IElementTypeAsPsiFactory("EVENT_DECLARATION", CSharpLanguage.INSTANCE, CSharpEventDeclarationImpl.class);

	IElementType EVENT_ACCESSOR = new IElementTypeAsPsiFactory("EVENT_ACCESSOR", CSharpLanguage.INSTANCE, CSharpEventAccessorImpl.class);

	IElementType FIELD_DECLARATION = new IElementTypeAsPsiFactory("FIELD_DECLARATION", CSharpLanguage.INSTANCE, CSharpFieldDeclarationImpl.class);

	IElementType PROPERTY_DECLARATION = new IElementTypeAsPsiFactory("PROPERTY_DECLARATION", CSharpLanguage.INSTANCE,
			CSharpPropertyDeclarationImpl.class);

	IElementType PROPERTY_ACCESSOR = new IElementTypeAsPsiFactory("PROPERTY_ACCESSOR", CSharpLanguage.INSTANCE, CSharpPropertyAccessorImpl.class);

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

	IElementType REFERENCE_EXPRESSION = new IElementTypeAsPsiFactory("REFERENCE_EXPRESSION", CSharpLanguage.INSTANCE, CSharpReferenceExpressionImpl.class);
}
