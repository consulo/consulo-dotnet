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

package org.mustbe.consulo.dotnet.debugger.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import mono.debugger.TypeMirror;

/**
 * @author VISTALL
 * @since 18.04.14
 */
public abstract class AbstractTypedMirrorNode extends XNamedValue
{
	@NotNull
	protected final DotNetDebugContext myDebugContext;

	public AbstractTypedMirrorNode(@NotNull DotNetDebugContext debugContext, @NotNull String name)
	{
		super(name);
		myDebugContext = debugContext;
	}

	@Nullable
	public abstract TypeMirror getTypeOfVariable();

	@Override
	public void computeTypeSourcePosition(@NotNull XNavigatable navigatable)
	{
		TypeMirror typeOfVariable = getTypeOfVariable();
		assert typeOfVariable != null;
		DotNetTypeDeclaration[] types = findTypesByQualifiedName(typeOfVariable);
		if(types.length == 0)
		{
			return;
		}
		DotNetTypeDeclaration type = types[0];
		PsiElement nameIdentifier = type.getNameIdentifier();
		if(nameIdentifier == null)
		{
			return;
		}
		navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(type.getContainingFile().getVirtualFile(),
				nameIdentifier.getTextOffset()));
	}

	@NotNull
	public DotNetTypeDeclaration[] findTypesByQualifiedName(@NotNull TypeMirror typeMirror)
	{
		return DotNetVirtualMachineUtil.findTypesByQualifiedName(typeMirror, myDebugContext);
	}

	@Override
	public boolean canNavigateToTypeSource()
	{
		return getTypeOfVariable() != null;
	}
}
