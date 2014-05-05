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

package org.mustbe.consulo.csharp.ide.highlight.check;

import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 11.03.14
 */
public abstract class AbstractCompilerCheck<T extends PsiElement> implements CompilerCheckEx<T>
{
	protected final HighlightInfoType myType;
	protected final Processor<T> myProcessor;
	protected String myId;

	public AbstractCompilerCheck(HighlightInfoType type, Processor<T> processor)
	{
		myType = type;
		myProcessor = processor;
	}

	public boolean accept(@NotNull T element)
	{
		assert myProcessor != null;
		return myProcessor.process(element);
	}

	@Override
	public void add(@NotNull T element, @NotNull HighlightInfoHolder holder)
	{
		if(accept(element))
		{
			String message = makeMessage(element);
			if(ApplicationManager.getApplication().isInternal())
			{
				message = myId + ": " + message;
			}
			HighlightInfo highlightInfo = HighlightInfo.newHighlightInfo(myType).descriptionAndTooltip(message).range(makeRange(element)).create();
			holder.add(highlightInfo);
		}
	}

	protected abstract String makeMessage(@NotNull T element);

	protected abstract TextRange makeRange(@NotNull T element);

	@Override
	public void setId(@NotNull String id)
	{
		assert myId == null;
		myId = id;
	}
}
