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

package consulo.dotnet.ui.chooser;

import org.consulo.lombok.annotations.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.ide.util.TreeChooser;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
@ProjectService
public abstract class DotNetTypeChooserFactory
{
	@NotNull
	public abstract TreeChooser<DotNetTypeDeclaration> createChooser(@NotNull GlobalSearchScope scope);

	@NotNull
	public abstract TreeChooser<DotNetTypeDeclaration> createInheriableChooser(@NotNull String vmQName, @NotNull GlobalSearchScope scope);
}
