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

	IElementType PROPERTY_DECLARATION = new IElementTypeAsPsiFactory("PROPERTY_DECLARATION", CSharpLanguage.INSTANCE,
			CSharpPropertyDeclarationImpl.class);

	IElementType GENERIC_PARAMETER_LIST = new IElementTypeAsPsiFactory("GENERIC_PARAMETER_LIST", CSharpLanguage.INSTANCE,
			CSharpGenericParameterListImpl.class);

	IElementType GENERIC_PARAMETER = new IElementTypeAsPsiFactory("GENERIC_PARAMETER", CSharpLanguage.INSTANCE, CSharpGenericParameterImpl.class);

	IElementType GENERIC_CONSTRAINT_LIST = new IElementTypeAsPsiFactory("GENERIC_CONSTRAINT_LIST", CSharpLanguage.INSTANCE,
			CSharpGenericConstraintListImpl.class);

	IElementType GENERIC_CONSTRAINT = new IElementTypeAsPsiFactory("GENERIC_CONSTRAINT", CSharpLanguage.INSTANCE, CSharpGenericConstraintImpl.class);

	IElementType NEW_GENERIC_CONSTRAINT_VALUE = new IElementTypeAsPsiFactory("NEW_GENERIC_CONSTRAINT_VALUE", CSharpLanguage.INSTANCE,
			CSharpNewGenericConstraintValueImpl.class);

	IElementType TYPE = new IElementTypeAsPsiFactory("TYPE", CSharpLanguage.INSTANCE, CSharpTypeImpl.class);

	IElementType CODE_BLOCK = new IElementTypeAsPsiFactory("CODE_BLOCK", CSharpLanguage.INSTANCE, CSharpCodeBlockImpl.class);

	IElementType MODIFIER_LIST = new IElementTypeAsPsiFactory("MODIFIER_LIST", CSharpLanguage.INSTANCE, CSharpModifierListImpl.class);

	IElementType EXTENDS_LIST = new IElementTypeAsPsiFactory("EXTENDS_LIST", CSharpLanguage.INSTANCE, CSharpTypeListImpl.class);

	IElementType REFERENCE_EXPRESSION = new IElementTypeAsPsiFactory("REFERENCE_EXPRESSION", CSharpLanguage.INSTANCE, CSharpReferenceExpressionImpl.class);
}
