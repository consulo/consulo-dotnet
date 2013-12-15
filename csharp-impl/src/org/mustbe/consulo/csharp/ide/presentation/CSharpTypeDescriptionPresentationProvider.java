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

package org.mustbe.consulo.csharp.ide.presentation;

import javax.swing.Icon;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpTypeDeclarationImpl;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public class CSharpTypeDescriptionPresentationProvider implements ItemPresentationProvider<CSharpTypeDeclarationImpl>
{
	public static class CSharpTypeDescriptionPresentation implements ItemPresentation
	{
		private final CSharpTypeDeclarationImpl myDeclaration;

		public CSharpTypeDescriptionPresentation(CSharpTypeDeclarationImpl declaration)
		{
			myDeclaration = declaration;
		}

		@Nullable
		@Override
		public String getPresentableText()
		{
			return myDeclaration.getName();
		}

		@Nullable
		@Override
		public String getLocationString()
		{
			PsiElement parent = myDeclaration.getParent();
			if(parent instanceof DotNetNamespaceDeclaration)
			{
				return ((DotNetNamespaceDeclaration) parent).getQName();
			}
			return null;
		}

		@Nullable
		@Override
		public Icon getIcon(boolean b)
		{
			return IconDescriptorUpdaters.getIcon(myDeclaration, 0);
		}
	}
	@Override
	public ItemPresentation getPresentation(CSharpTypeDeclarationImpl cSharpTypeDeclaration)
	{
		return new CSharpTypeDescriptionPresentation(cSharpTypeDeclaration);
	}
}
