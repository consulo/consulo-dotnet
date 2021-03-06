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

package consulo.msil.lang.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.openapi.project.Project;
import consulo.dotnet.lang.psi.impl.IndexBasedDotNetNamespaceAsElement;
import consulo.dotnet.resolve.impl.IndexBasedDotNetPsiSearcher;
import consulo.msil.MsilLanguage;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public class MsilNamespaceAsElementImpl extends IndexBasedDotNetNamespaceAsElement
{
	public MsilNamespaceAsElementImpl(@Nonnull Project project,
			@Nonnull String indexKey,
			@Nonnull String qName,
			@Nonnull IndexBasedDotNetPsiSearcher searcher)
	{
		super(project, MsilLanguage.INSTANCE, indexKey, qName, searcher);
	}
}
