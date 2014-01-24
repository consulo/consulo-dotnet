package org.mustbe.consulo.csharp.lang.psi;

import org.mustbe.consulo.csharp.lang.CSharpMacroLanguage;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMacroBlockImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMacroBlockStartImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMacroBlockStopImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMacroDefineImpl;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IElementTypeAsPsiFactory;

/**
 * @author VISTALL
 * @since 24.01.14
 */
public interface CSharpMacroElements
{

	IElementType MACRO_DEFINE = new IElementTypeAsPsiFactory("MACRO_DEFINE", CSharpMacroLanguage.INSTANCE, CSharpMacroDefineImpl.class);

	IElementType MACRO_BLOCK_START = new IElementTypeAsPsiFactory("MACRO_BLOCK_START", CSharpMacroLanguage.INSTANCE,
			CSharpMacroBlockStartImpl.class);

	IElementType MACRO_BLOCK = new IElementTypeAsPsiFactory("MACRO_BLOCK", CSharpMacroLanguage.INSTANCE, CSharpMacroBlockImpl.class);

	IElementType MACRO_BLOCK_STOP = new IElementTypeAsPsiFactory("MACRO_BLOCK_STOP", CSharpMacroLanguage.INSTANCE, CSharpMacroBlockStopImpl.class);

	IElementType PREFIX_EXPRESSION = new IElementTypeAsPsiFactory("PREFIX_EXPRESSION", CSharpMacroLanguage.INSTANCE, ASTWrapperPsiElement.class);

	IElementType POLYADIC_EXPRESSION = new IElementTypeAsPsiFactory("POLYADIC_EXPRESSION", CSharpMacroLanguage.INSTANCE, ASTWrapperPsiElement.class);

	IElementType BINARY_EXPRESSION = new IElementTypeAsPsiFactory("BINARY_EXPRESSION", CSharpMacroLanguage.INSTANCE, ASTWrapperPsiElement.class);

	IElementType REFERENCE_EXPRESSION = new IElementTypeAsPsiFactory("REFERENCE_EXPRESSION", CSharpMacroLanguage.INSTANCE,
			ASTWrapperPsiElement.class);

	IElementType PARENTHESES_EXPRESSION = new IElementTypeAsPsiFactory("PARENTHESES_EXPRESSION", CSharpMacroLanguage.INSTANCE,
			ASTWrapperPsiElement.class);
}
