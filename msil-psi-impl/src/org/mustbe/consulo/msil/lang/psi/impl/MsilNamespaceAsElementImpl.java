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

package org.mustbe.consulo.msil.lang.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.lang.psi.impl.IndexBasedDotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.msil.MsilLanguage;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilAllNamespaceIndex;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilNamespaceIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StringStubIndexExtension;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public class MsilNamespaceAsElementImpl extends IndexBasedDotNetNamespaceAsElement
{
	public MsilNamespaceAsElementImpl(@NotNull Project project, @NotNull String indexKey, @NotNull String qName)
	{
		super(project, MsilLanguage.INSTANCE, indexKey, qName);
	}

	@NotNull
	@Override
	public StringStubIndexExtension<? extends PsiElement> getHardIndexExtension()
	{
		return MsilNamespaceIndex.getInstance();
	}

	@NotNull
	@Override
	public StringStubIndexExtension<? extends PsiElement> getSoftIndexExtension()
	{
		return MsilAllNamespaceIndex.getInstance();
	}

	@Override
	public DotNetNamespaceAsElement createNamespace(@NotNull String indexKey, @NotNull String qName)
	{
		return new MsilNamespaceAsElementImpl(myProject, indexKey, qName);
	}
}
