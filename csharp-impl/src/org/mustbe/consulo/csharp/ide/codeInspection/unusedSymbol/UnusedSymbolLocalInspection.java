package org.mustbe.consulo.csharp.ide.codeInspection.unusedSymbol;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheck;
import org.mustbe.consulo.csharp.ide.highlight.check.impl.CS0168;
import org.mustbe.consulo.csharp.ide.highlight.check.impl.CS0219;
import org.mustbe.consulo.csharp.lang.psi.CSharpLocalVariable;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * @author VISTALL
 * @since 20.05.14
 */
public class UnusedSymbolLocalInspection extends LocalInspectionTool
{
	private static final Key<UnusedSymbolVisitor> KEY = Key.create("UnusedSymbolVisitor");

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(
			@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session)
	{
		UnusedSymbolVisitor visitor = session.getUserData(KEY);
		if(visitor == null)
		{
			session.putUserData(KEY, visitor = new UnusedSymbolVisitor());
		}
		return visitor;
	}

	@Override
	public void inspectionFinished(@NotNull LocalInspectionToolSession session, @NotNull ProblemsHolder problemsHolder)
	{
		UnusedSymbolVisitor visitor = session.getUserData(KEY);
		if(visitor == null)
		{
			return;
		}

		for(Map.Entry<PsiNameIdentifierOwner, Boolean> entry : visitor.getVariableStates())
		{
			if(entry.getValue() == Boolean.TRUE)
			{
				continue;
			}

			PsiNameIdentifierOwner key = entry.getKey();

			PsiElement nameIdentifier = key.getNameIdentifier();
			if(nameIdentifier == null)
			{
				continue;
			}

			problemsHolder.registerProblem(nameIdentifier, getDesc(key, nameIdentifier), ProblemHighlightType.LIKE_UNUSED_SYMBOL);
		}
	}

	private static String getDesc(PsiElement target, PsiElement name)
	{
		if(target instanceof CSharpLocalVariable)
		{
			DotNetExpression initializer = ((CSharpLocalVariable) target).getInitializer();
			return CompilerCheck.message(initializer == null ? CS0168.class : CS0219.class, name.getText());
		}
		else if(target instanceof DotNetParameter)
		{
			return "Parameter '" + name.getText() + "' is not used";
		}
		throw new IllegalArgumentException();
	}
}
