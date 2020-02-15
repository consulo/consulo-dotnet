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

package consulo.dotnet.debugger.nodes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.ref.SimpleReference;

/**
 * @author VISTALL
 * @since 18.04.14
 */
public abstract class AbstractTypedValueNode extends XNamedValue
{
	@Nonnull
	protected final DotNetDebugContext myDebugContext;

	@Nullable
	private SimpleReference<DotNetTypeProxy> myTypeProxy;

	public AbstractTypedValueNode(@Nonnull DotNetDebugContext debugContext, @Nonnull String name)
	{
		super(name);
		myDebugContext = debugContext;
	}

	@Nullable
	public DotNetTypeProxy getTypeOfVariable()
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		SimpleReference<DotNetTypeProxy> typeProxy = myTypeProxy;
		if(typeProxy != null)
		{
			return typeProxy.get();
		}
		else
		{
			DotNetTypeProxy value = getTypeOfVariableImpl();
			typeProxy = SimpleReference.create(value);
			myTypeProxy = typeProxy;
			return value;
		}
	}

	@Nullable
	public abstract DotNetTypeProxy getTypeOfVariableImpl();

	@Override
	@RequiredUIAccess
	public void computeTypeSourcePosition(@Nonnull XNavigatable navigatable)
	{
		DotNetTypeProxy typeOfVariable = getTypeOfVariable();
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
		navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(type.getContainingFile().getVirtualFile(), nameIdentifier.getTextOffset()));
	}

	@Nonnull
	@RequiredUIAccess
	public DotNetTypeDeclaration[] findTypesByQualifiedName(@Nonnull DotNetTypeProxy typeMirror)
	{
		return DotNetVirtualMachineUtil.findTypesByQualifiedName(typeMirror, myDebugContext);
	}

	@Override
	public boolean canNavigateToTypeSource()
	{
		return myTypeProxy != ObjectUtil.NULL;
	}
}
