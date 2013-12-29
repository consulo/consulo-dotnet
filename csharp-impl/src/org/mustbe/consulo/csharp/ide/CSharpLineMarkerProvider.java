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

package org.mustbe.consulo.csharp.ide;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.psi.PsiElement;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import lombok.val;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpLineMarkerProvider implements LineMarkerProvider
{
	@Nullable
	@Override
	public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element)
	{
		return null;
	}

	@Override
	public void collectSlowLineMarkers(@NotNull List<PsiElement> psiElements, @NotNull Collection<LineMarkerInfo> lineMarkerInfos)
	{
		if(psiElements.isEmpty())
		{
			return;
		}


		for(PsiElement psiElement : psiElements)
		{
			if(psiElement.getParent() instanceof CSharpTypeDeclaration && psiElement.getNode().getElementType() == CSharpTokens.IDENTIFIER)
			{
				List<DotNetTypeDeclaration> children = findChildren((CSharpTypeDeclaration) psiElement.getParent());
				if(!children.isEmpty())
				{

					val lineMarkerInfo = new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), AllIcons.Gutter.OverridenMethod,
							Pass.UPDATE_ALL, new Function<PsiElement, String>()
					{
						@Override
						public String fun(PsiElement element)
						{
							return "Class";
						}
					}, new GutterIconNavigationHandler<PsiElement>()
					{
						@Override
						public void navigate(MouseEvent mouseEvent, PsiElement element)
						{
							List<DotNetTypeDeclaration> children1 = findChildren((CSharpTypeDeclaration) element.getParent());
							JBPopup popup = NavigationUtil.getPsiElementPopup(children1.toArray(new DotNetTypeDeclaration[children1.size()]),
									"Open");
							popup.show(new RelativePoint(mouseEvent));
						}
					}, GutterIconRenderer.Alignment.LEFT
					);
					lineMarkerInfos.add(lineMarkerInfo);
				}
			}
		}
	}

	private static List<DotNetTypeDeclaration> findChildren(final CSharpTypeDeclaration type)
	{
		val list = new ArrayList<DotNetTypeDeclaration>();

		/*val project = type.getProject();

		TypeIndex.getInstance().processAllKeys(project, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				Collection<DotNetTypeDeclaration> dotNetTypeDeclarations = TypeIndex.getInstance().get(s, project,
						GlobalSearchScope.allScope(project));
				for(DotNetTypeDeclaration dotNetTypeDeclaration : dotNetTypeDeclarations)
				{
					if(CSharpInheritUtil.isInherit(type, dotNetTypeDeclaration))
					{
						list.add(dotNetTypeDeclaration);
					}
				}
				return true;
			}
		});   */
		return list;
	}
}
