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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.CSharpFileType;
import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceHelper;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.CSharpIndexKeys;
import org.mustbe.consulo.dotnet.psi.DotNetFile;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpFileImpl extends PsiFileBase implements DotNetFile
{
	public CSharpFileImpl(@NotNull FileViewProvider viewProvider)
	{
		super(viewProvider, CSharpLanguage.INSTANCE);
	}

	@Override
	public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException
	{
		DotNetNamedElement singleElement = CSharpPsiUtilImpl.findSingleElement(this);
		if(singleElement != null && (singleElement == first || singleElement == last))
		{
			delete();
		}
		else
		{
			super.deleteChildRange(first, last);
		}
	}

	@Override
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof CSharpElementVisitor)
		{
			((CSharpElementVisitor) visitor).visitCSharpFile(this);
		}
		else
		{
			super.accept(visitor);
		}
	}

	@Override
	public boolean processDeclarations(@NotNull final PsiScopeProcessor processor, @NotNull final ResolveState state, PsiElement lastParent,
			@NotNull PsiElement place)
	{
		for(PsiElement psiElement : getChildren())
		{
			if(!CSharpStubElements.QUALIFIED_MEMBERS_WITH_USING.contains(psiElement.getNode().getElementType()))
			{
				continue;
			}

			if(psiElement instanceof CSharpUsingNamespaceListImpl)
			{
				if(!psiElement.processDeclarations(processor, state, lastParent, place))
				{
					return false;
				}
			}
			else
			{
				if(!processor.execute(psiElement, state))
				{
					return false;
				}
			}
		}

		return StubIndex.getInstance().process(CSharpIndexKeys.MEMBER_BY_NAMESPACE_QNAME_INDEX, CSharpNamespaceHelper.ROOT, getProject(),
				getResolveScope(), new Processor<DotNetNamedElement>()

		{
			@Override
			public boolean process(DotNetNamedElement dotNetNamespaceDeclaration)
			{
				return processor.execute(dotNetNamespaceDeclaration, state);
			}
		});
	}

	@NotNull
	@Override
	public FileType getFileType()
	{
		return CSharpFileType.INSTANCE;
	}

	@NotNull
	@Override
	public DotNetQualifiedElement[] getMembers()
	{
		StubElement<?> stub = getStub();
		if(stub != null)
		{
			return stub.getChildrenByType(CSharpStubElements.QUALIFIED_MEMBERS, DotNetQualifiedElement.ARRAY_FACTORY);
		}
		return findChildrenByClass(DotNetQualifiedElement.class);
	}
}
