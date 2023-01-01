/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.debugger.impl.breakpoint;

import consulo.application.ApplicationManager;
import consulo.application.util.function.Computable;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XDebuggerUtil;
import consulo.execution.debug.breakpoint.XExpression;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.evaluation.XDebuggerEvaluator;
import consulo.execution.debug.frame.XValue;
import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerProvider;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.impl.nodes.DotNetAbstractVariableValueNode;
import consulo.dotnet.debugger.proxy.DotNetNotSuspendedException;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.execution.ui.console.ConsoleView;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.util.lang.ref.SimpleReference;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 30.04.2016
 */
public class DotNetBreakpointEngine
{
	@Nullable
	private XValue evaluateBreakpointExpression(@Nonnull final DotNetStackFrameProxy frameProxy,
												@Nonnull final XLineBreakpoint<?> breakpoint,
												@Nullable final XExpression conditionExpression,
												@Nonnull final DotNetDebugContext debugContext)
	{
		if(conditionExpression == null)
		{
			return null;
		}

		final VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
		if(virtualFile == null)
		{
			return null;
		}

		final DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(conditionExpression.getLanguage());
		if(provider != null)
		{
			return ApplicationManager.getApplication().runReadAction(new Computable<XValue>()
			{
				@Override
				public XValue compute()
				{
					Document document = virtualFile.isValid() ? FileDocumentManager.getInstance().getDocument(virtualFile) : null;
					if(document == null)
					{
						return null;
					}
					int line = breakpoint.getLine();

					int offset = line < document.getLineCount() ? document.getLineStartOffset(line) : -1;
					PsiFile file = PsiManager.getInstance(debugContext.getProject()).findFile(virtualFile);
					if(file == null)
					{
						return null;
					}
					PsiElement elementAt = offset >= 0 ? file.findElementAt(offset) : null;
					if(elementAt == null)
					{
						return null;
					}
					final SimpleReference<XValue> valueRef = SimpleReference.create();
					provider.evaluate(frameProxy, debugContext, conditionExpression.getExpression(), elementAt, new XDebuggerEvaluator.XEvaluationCallback()
					{
						@Override
						public void evaluated(@Nonnull XValue result)
						{
							valueRef.set(result);
						}

						@Override
						public void errorOccurred(@Nonnull String errorMessage)
						{
						}
					}, XDebuggerUtil.getInstance().createPositionByElement(elementAt));
					return valueRef.get();
				}
			});
		}
		return null;
	}

	@Nullable
	public String tryEvaluateBreakpointLogMessage(@Nonnull DotNetThreadProxy threadProxy, final XLineBreakpoint<?> breakpoint, final DotNetDebugContext debugContext) throws
			DotNetNotSuspendedException
	{
		XExpression logExpressionObject = breakpoint.getLogExpressionObject();
		if(logExpressionObject == null)
		{
			return null;
		}

		XDebugSession session = debugContext.getSession();
		ConsoleView consoleView = session.getConsoleView();
		if(consoleView == null)
		{
			return null;
		}

		final DotNetStackFrameProxy frame = threadProxy.getFrame(0);
		if(frame == null)
		{
			return null;
		}

		XValue value = evaluateBreakpointExpression(frame, breakpoint, logExpressionObject, debugContext);
		if(value instanceof DotNetAbstractVariableValueNode)
		{
			DotNetValueProxy valueOfVariableSafe = ((DotNetAbstractVariableValueNode) value).getValueOfVariable();
			if(valueOfVariableSafe != null)
			{
				String toStringValue = DotNetDebuggerSearchUtil.toStringValue(frame, valueOfVariableSafe);
				if(toStringValue != null)
				{
					return toStringValue;
				}
			}
		}
		return null;
	}

	public boolean tryEvaluateBreakpointCondition(@Nonnull DotNetThreadProxy threadProxy, final XLineBreakpoint<?> breakpoint, final DotNetDebugContext debugContext) throws Exception
	{
		final XExpression conditionExpression = breakpoint.getConditionExpression();
		if(conditionExpression == null)
		{
			return true;
		}

		DotNetStackFrameProxy frame = threadProxy.getFrame(0);
		if(frame == null)
		{
			return true;
		}

		XValue value = evaluateBreakpointExpression(frame, breakpoint, conditionExpression, debugContext);
		if(value instanceof DotNetAbstractVariableValueNode)
		{
			DotNetValueProxy valueOfVariableSafe = ((DotNetAbstractVariableValueNode) value).getValueOfVariable();
			if(valueOfVariableSafe instanceof DotNetBooleanValueProxy)
			{
				return ((DotNetBooleanValueProxy) valueOfVariableSafe).getValue();
			}
		}
		return true;
	}
}
