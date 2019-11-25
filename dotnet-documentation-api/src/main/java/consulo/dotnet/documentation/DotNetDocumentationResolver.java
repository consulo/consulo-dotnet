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

package consulo.dotnet.documentation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.emonic.base.documentation.IDocumentation;
import consulo.annotation.access.RequiredReadAction;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 13.05.14
 */
public interface DotNetDocumentationResolver
{
	ExtensionPointName<DotNetDocumentationResolver> EP_NAME = ExtensionPointName.create("consulo.dotnet.documentationResolver");

	@Nullable
	@RequiredReadAction
	IDocumentation resolveDocumentation(@Nonnull List<VirtualFile> orderEntryFiles, @Nonnull PsiElement element);
}
