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

package consulo.msil.lang.psi.impl.resolve;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndexKey;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.impl.IndexBasedDotNetPsiSearcher;
import consulo.msil.lang.psi.impl.MsilNamespaceAsElementImpl;
import consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import consulo.msil.lang.psi.impl.elementType.stub.index.MsilTypeByQNameIndex;

/**
 * @author VISTALL
 * @since 13.07.14
 */
public class MsilPsiSearcher extends IndexBasedDotNetPsiSearcher
{
	public MsilPsiSearcher(Project project)
	{
		super(project);
	}

	@Nonnull
	@Override
	protected DotNetNamespaceAsElement createNamespace(@Nonnull String indexKey, @Nonnull String qName)
	{
		return new MsilNamespaceAsElementImpl(myProject, indexKey, qName, this);
	}

	@Nonnull
	@Override
	public StubIndexKey<String, DotNetQualifiedElement> getElementByQNameIndexKey()
	{
		return MsilIndexKeys.ELEMENT_BY_QNAME_INDEX;
	}

	@Nonnull
	@Override
	public StubIndexKey<String, DotNetQualifiedElement> getNamespaceIndexKey()
	{
		return MsilIndexKeys.NAMESPACE_INDEX;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public Collection<? extends DotNetTypeDeclaration> findTypesImpl(@Nonnull String vmQName, @Nonnull GlobalSearchScope scope)
	{
		if(DumbService.isDumb(myProject))
		{
			return Collections.emptyList();
		}
		return MsilTypeByQNameIndex.getInstance().get(vmQName, myProject, scope);
	}
}
