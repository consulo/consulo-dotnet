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

package consulo.dotnet.debugger;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.component.extension.ExtensionPointCacheKey;
import consulo.dotnet.psi.DotNetCodeBlockOwner;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.Language;
import consulo.language.extension.ByLanguageValue;
import consulo.language.extension.LanguageExtension;
import consulo.language.extension.LanguageOneToOne;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author VISTALL
 * @since 19.07.2015
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface DotNetDebuggerSourceLineResolver extends LanguageExtension
{
	ExtensionPointCacheKey<DotNetDebuggerSourceLineResolver, ByLanguageValue<DotNetDebuggerSourceLineResolver>> KEY = ExtensionPointCacheKey.create("DotNetDebuggerSourceLineResolver",
			LanguageOneToOne.build(new DotNetDefaultDebuggerSourceLineResolver()));

	@Nonnull
	static DotNetDebuggerSourceLineResolver forLanguage(@Nonnull Language language)
	{
		return Application.get().getExtensionPoint(DotNetDebuggerSourceLineResolver.class).getOrBuildCache(KEY).requiredGet(language);
	}

	@Nullable
	@RequiredReadAction
	@SuppressWarnings("unused") // used in impl dotnet plugins
	default String resolveParentVmQName(@Nonnull PsiElement element)
	{
		DotNetCodeBlockOwner codeBlockOwner = PsiTreeUtil.getParentOfType(element, DotNetCodeBlockOwner.class, false);
		if(codeBlockOwner == null)
		{
			return null;
		}
		PsiElement codeBlock = codeBlockOwner.getCodeBlock().getElement();
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

	@Nonnull
	@RequiredReadAction
	default Set<PsiElement> getAllExecutableChildren(@Nonnull PsiElement root)
	{
		return Set.of();
	}
}
