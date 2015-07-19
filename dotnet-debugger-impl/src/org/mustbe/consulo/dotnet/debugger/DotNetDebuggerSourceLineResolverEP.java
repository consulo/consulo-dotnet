/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetCodeBlockOwner;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.lang.LanguageExtension;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author VISTALL
 * @since 19.07.2015
 */
public class DotNetDebuggerSourceLineResolverEP extends LanguageExtension<DotNetDebuggerSourceLineResolver>
{
	public static final DotNetDebuggerSourceLineResolverEP INSTANCE = new DotNetDebuggerSourceLineResolverEP();

	public DotNetDebuggerSourceLineResolverEP()
	{
		super("org.mustbe.consulo.dotnet.core.debugger.sourceLineResolver", new DotNetDebuggerSourceLineResolver()
		{
			@RequiredReadAction
			@Override
			@Nullable
			public String resolveParentVmQName(@NotNull PsiElement element)
			{
				DotNetCodeBlockOwner codeBlockOwner = PsiTreeUtil.getParentOfType(element, DotNetCodeBlockOwner.class, false);
				if(codeBlockOwner == null)
				{
					return null;
				}
				PsiElement codeBlock = codeBlockOwner.getCodeBlock();
				if(codeBlock == null)
				{
					return null;
				}
				if(!PsiTreeUtil.isAncestor(codeBlock, element, false))
				{
					return null;
				}
				DotNetTypeDeclaration typeDeclaration = PsiTreeUtil.getParentOfType(codeBlockOwner, DotNetTypeDeclaration.class);
				if(typeDeclaration == null)
				{
					return null;
				}
				return typeDeclaration.getVmQName();
			}
		});
	}
}
