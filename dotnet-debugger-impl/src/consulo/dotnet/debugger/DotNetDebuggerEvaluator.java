/*
 * Copyright 2013-2017 consulo.io
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

package consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;

/**
 * @author VISTALL
 * @since 16-Oct-17
 */
public class DotNetDebuggerEvaluator extends XDebuggerEvaluator
{
	private DotNetStackFrameProxy myFrameProxy;
	private DotNetDebugContext myDebuggerContext;

	public DotNetDebuggerEvaluator(DotNetStackFrameProxy frameProxy, DotNetDebugContext debuggerContext)
	{
		myFrameProxy = frameProxy;
		myDebuggerContext = debuggerContext;
	}

	@Nullable
	@Override
	@RequiredReadAction
	public TextRange getExpressionRangeAtOffset(Project project, Document document, int offset, boolean sideEffectsAllowed)
	{
		PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
		if(psiFile == null)
		{
			return null;
		}

		DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(psiFile.getLanguage());
		if(provider != null)
		{
			return provider.getExpressionRangeAtOffset(psiFile, offset, sideEffectsAllowed);
		}
		return null;
	}

	@Override
	public boolean isCodeFragmentEvaluationSupported()
	{
		return false;
	}

	@Override
	public void evaluate(@NotNull XExpression expression, @NotNull XEvaluationCallback callback, @Nullable XSourcePosition expressionPosition)
	{
		DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(expression.getLanguage());
		if(provider != null)
		{
			if(provider.getEditorLanguage() == expression.getLanguage())
			{
				provider.evaluate(myFrameProxy, myDebuggerContext, expression.getExpression(), null, callback, expressionPosition);
			}
		}
	}

	@Override
	public void evaluate(@NotNull String expression, @NotNull XEvaluationCallback callback, @Nullable XSourcePosition expressionPosition)
	{
		if(expressionPosition == null)
		{
			return;
		}

		FileType fileType = expressionPosition.getFile().getFileType();
		if(fileType instanceof LanguageFileType)
		{
			DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(((LanguageFileType) fileType).getLanguage());
			if(provider != null)
			{
				provider.evaluate(myFrameProxy, myDebuggerContext, expression, null, callback, expressionPosition);
			}
		}
	}
}
