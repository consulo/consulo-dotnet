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

package org.mustbe.consulo.msil.lang.psi.impl.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import org.mustbe.consulo.msil.lang.psi.MsilMethodEntry;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilMethodGenericTypeRefImpl extends DotNetTypeRef.Adapter
{
	private final MsilMethodEntry myParent;
	private final int myIndex;

	public MsilMethodGenericTypeRefImpl(MsilMethodEntry parent, int index)
	{
		myParent = parent;
		myIndex = index;
	}

	@Nullable
	@Override
	public String getPresentableText()
	{
		DotNetGenericParameter resolve = resolve(myParent);
		return resolve == null ? String.valueOf(myIndex) : resolve.getName();
	}

	@Nullable
	@Override
	public DotNetGenericParameter resolve(@NotNull PsiElement scope)
	{
		return ArrayUtil2.safeGet(myParent.getGenericParameters(), myIndex);
	}
}
