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

import org.mustbe.consulo.csharp.lang.psi.impl.source.*;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpElementVisitor extends PsiElementVisitor
{
	public void visitCSharpFile(CSharpFileImpl file)
	{
		visitFile(file);
	}

	public void visitUsingList(CSharpUsingListImpl list)
	{
		visitElement(list);
	}

	public void visitCodeBlock(CSharpCodeBlockImpl block)
	{
		visitElement(block);
	}

	public void visitConstructorDeclaration(CSharpConstructorDeclarationImpl declaration)
	{
		visitMethodDeclaration(declaration);
	}

	public void visitMethodDeclaration(CSharpMethodDeclarationImpl methodDeclaration)
	{
		visitElement(methodDeclaration);
	}

	public void visitModifierList(CSharpModifierListImpl list)
	{
		visitElement(list);
	}

	public void visitNamespaceDeclaration(CSharpNamespaceDeclarationImpl declaration)
	{
		visitElement(declaration);
	}

	public void visitParameter(CSharpParameterImpl parameter)
	{
		visitElement(parameter);
	}

	public void visitParameterList(CSharpParameterListImpl list)
	{
		visitElement(list);
	}

	public void visitReferenceExpression(CSharpReferenceExpressionImpl expression)
	{
		visitElement(expression);
	}

	public void visitTypeDeclaration(CSharpTypeDeclarationImpl declaration)
	{
		visitElement(declaration);
	}

	public void visitReferenceType(CSharpReferenceTypeImpl type)
	{
		visitElement(type);
	}

	public void visitUsingStatement(CSharpUsingStatementImpl statement)
	{
		visitElement(statement);
	}

	public void visitGenericParameter(CSharpGenericParameterImpl parameter)
	{
		visitElement(parameter);
	}

	public void visitGenericParameterList(CSharpGenericParameterListImpl list)
	{
		visitElement(list);
	}

	public void visitGenericConstraintList(CSharpGenericConstraintListImpl list)
	{
		visitElement(list);
	}

	public void visitGenericConstraint(CSharpGenericConstraintImpl constraint)
	{
		visitElement(constraint);
	}

	public void visitNewGenericConstraintValue(CSharpNewGenericConstraintValueImpl value)
	{
		visitElement(value);
	}

	public void visitTypeList(CSharpTypeListImpl list)
	{
		visitElement(list);
	}

	public void visitEventDeclaration(CSharpEventDeclarationImpl declaration)
	{
		visitElement(declaration);
	}

	public void visitPropertyDeclaration(CSharpPropertyDeclarationImpl declaration)
	{
		visitElement(declaration);
	}

	public void visitEventAccessor(CSharpEventAccessorImpl accessor)
	{
		visitElement(accessor);
	}

	public void visitPropertyAccessor(CSharpPropertyAccessorImpl accessor)
	{
		visitElement(accessor);
	}

	public void visitFieldDeclaration(CSharpFieldDeclarationImpl declaration)
	{
		visitElement(declaration);
	}

	public void visitPointerType(CSharpPointerTypeImpl type)
	{
		visitElement(type);
	}

	public void visitNativeType(CSharpNativeTypeImpl type)
	{
		visitElement(type);
	}

	public void visitTypeWrapperWithTypeArguments(CSharpTypeWrapperWithTypeArgumentsImpl typeArguments)
	{
		visitElement(typeArguments);
	}

	public void visitArrayType(CSharpArrayTypeImpl type)
	{
		visitElement(type);
	}

	public void visitLocalVariable(CSharpLocalVariableImpl variable)
	{
		visitElement(variable);
	}

	public void visitConstantExpression(CSharpConstantExpressionImpl expression)
	{
		visitElement(expression);
	}

	public void visitLocalVariableDeclarationStatement(CSharpLocalVariableDeclarationStatement statement)
	{
		visitElement(statement);
	}

	public void visitExpressionStatement(CSharpExpressionStatementImpl statement)
	{
		visitElement(statement);
	}

	public void visitMethodCallExpression(CSharpMethodCallExpressionImpl expression)
	{
		visitElement(expression);
	}

	public void visitMethodCallParameterList(CSharpMethodCallParameterListImpl list)
	{
		visitElement(list);
	}

	public void visitTypeOfExpression(CSharpTypeOfExpressionImpl expression)
	{
		visitElement(expression);
	}

	public void visitMacroDefine(CSharpMacroDefineImpl cSharpMacroDefine)
	{
		visitElement(cSharpMacroDefine);
	}

	public void visitMacroBody(CSharpMacroBodyImpl block)
	{
		visitElement(block);
	}

	public void visitMacroBlockStart(CSharpMacroBlockStartImpl start)
	{
		visitElement(start);
	}

	public void visitMacroBlockStop(CSharpMacroBlockStopImpl stop)
	{
		visitElement(stop);
	}

	public void visitMacroBlock(CSharpMacroBlockImpl block)
	{
		visitElement(block);
	}
}
