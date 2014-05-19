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

package org.mustbe.consulo.csharp.ide.highlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpLocalVariable;
import org.mustbe.consulo.csharp.lang.psi.CSharpRecursiveElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.UpdateHighlightersUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;

/**
 * @author VISTALL
 * @since 19.05.14
 */
public class CSharpTextEditorHighlightingPass extends TextEditorHighlightingPass
{
	@NotNull
	private final CSharpFileImpl myFile;

	private Map<PsiNameIdentifierOwner, Boolean> myVariableStates = new HashMap<PsiNameIdentifierOwner, Boolean>();

	public CSharpTextEditorHighlightingPass(@NotNull CSharpFileImpl file, @Nullable Document document)
	{
		super(file.getProject(), document);
		myFile = file;
	}

	@Override
	public void doCollectInformation(@NotNull ProgressIndicator progress)
	{
		myFile.accept(new CSharpRecursiveElementVisitor()
		{
			@Override
			public void visitLocalVariable(CSharpLocalVariable variable)
			{
				myVariableStates.put(variable, Boolean.FALSE);
			}

			@Override
			public void visitParameter(DotNetParameter parameter)
			{
				myVariableStates.put(parameter, Boolean.FALSE);
			}

			@Override
			public void visitReferenceExpression(CSharpReferenceExpressionImpl expression)
			{
				PsiElement resolve = expression.resolve();
				if(!(resolve instanceof PsiNameIdentifierOwner))
				{
					return;
				}
				Boolean aBoolean = myVariableStates.get(resolve);
				if(aBoolean != null)
				{
					myVariableStates.put((PsiNameIdentifierOwner) resolve, Boolean.TRUE);
				}
			}
		});
	}

	@Override
	public void doApplyInformationToEditor()
	{
		List<HighlightInfo> list = new ArrayList<HighlightInfo>();
		for(Map.Entry<PsiNameIdentifierOwner, Boolean> entry : myVariableStates.entrySet())
		{
			if(entry.getValue() == Boolean.FALSE)
			{
				PsiElement nameIdentifier = entry.getKey().getNameIdentifier();
				if(nameIdentifier == null)
				{
					continue;
				}
				HighlightInfo highlightInfo = HighlightInfo.newHighlightInfo(HighlightInfoType.UNUSED_SYMBOL).range(nameIdentifier)
						.descriptionAndTooltip(getDesc(entry.getKey(), nameIdentifier)).create();
				if(highlightInfo != null)
				{
					list.add(highlightInfo);
				}
			}
		}
		UpdateHighlightersUtil.setHighlightersToEditor(myProject, myDocument, 0, myFile.getTextLength(), list, getColorsScheme(), getId());
	}

	private static String getDesc(PsiElement target, PsiElement name)
	{
		if(target instanceof CSharpLocalVariable)
		{
			return "Variable '" + name.getText() + "' is not used";
		}
		else if(target instanceof DotNetParameter)
		{
			return "Parameter '" + name.getText() + "' is not used";
		}
		throw new IllegalArgumentException();
	}
}
