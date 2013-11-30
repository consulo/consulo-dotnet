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

	public void visitType(CSharpTypeImpl type)
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
}
