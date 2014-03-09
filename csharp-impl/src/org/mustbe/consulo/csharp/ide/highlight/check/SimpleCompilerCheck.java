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
import org.mustbe.consulo.csharp.ide.CSharpErrorBundle;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 09.03.14
 */
public class SimpleCompilerCheck<T extends PsiElement> implements CompilerCheckEx<T>
{
	@NotNull
	public static <T extends PsiElement> SimpleCompilerCheck<T> of(@NotNull HighlightInfoType type, @NotNull Processor<T> processor)
	{
		return new SimpleCompilerCheck<T>(type, processor);
	}

	private final HighlightInfoType myType;
	private final Processor<T> myProcessor;
	private String myId;

	public SimpleCompilerCheck(HighlightInfoType type, Processor<T> processor)
	{
		myType = type;
		myProcessor = processor;
	}

	@Override
	public void add(@NotNull T element, @NotNull HighlightInfoHolder holder)
	{
		if(myProcessor.process(element))
		{
			String message = CSharpErrorBundle.message(myId);
			if(ApplicationManager.getApplication().isInternal())
			{
				message = myId + ": " + message;
			}
			HighlightInfo highlightInfo = HighlightInfo.newHighlightInfo(myType).descriptionAndTooltip(message).range(element).create();
			holder.add(highlightInfo);
		}
	}

	@Override
	public void setId(@NotNull String id)
	{
		assert myId == null;
		myId = id;
	}
}
