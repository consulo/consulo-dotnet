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

package org.mustbe.consulo.dotnet.documentation;

import org.emonic.base.documentation.IDocumentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 13.05.14
 */
public interface DotNetDocumentationResolver
{
	ExtensionPointName<DotNetDocumentationResolver> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.documentationResolver");

	@Nullable
	IDocumentation resolveDocumentation(@NotNull VirtualFile virtualFile, @NotNull PsiElement element);
}
