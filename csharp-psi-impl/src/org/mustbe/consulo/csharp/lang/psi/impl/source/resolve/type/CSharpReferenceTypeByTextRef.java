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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.QualifiedName;

/**
 * @author VISTALL
 * @since 15.01.14
 */
public class CSharpReferenceTypeByTextRef implements DotNetTypeRef
{
	private final String myText;

	private NotNullLazyValue<DotNetType> myType;

	public CSharpReferenceTypeByTextRef(final String text, final PsiElement owner)
	{
		myText = text;
		myType = new NotNullLazyValue<DotNetType>()
		{
			@NotNull
			@Override
			protected DotNetType compute()
			{
				return CSharpFileFactory.createType(owner, myText);
			}
		};
	}

	@Nullable
	@Override
	public String getPresentableText()
	{
		QualifiedName qualifiedName = QualifiedName.fromDottedString(myText);
		return qualifiedName.getLastComponent();
	}

	@Nullable
	@Override
	public String getQualifiedText()
	{
		return myType.getValue().toTypeRef().getQualifiedText();
	}

	@Override
	public boolean isNullable()
	{
		return true;
	}

	@Nullable
	@Override
	public PsiElement resolve()
	{
		return myType.getValue().toTypeRef().resolve();
	}

	@NotNull
	@Override
	public DotNetGenericExtractor getGenericExtractor()
	{
		return DotNetGenericExtractor.EMPTY;
	}
}
