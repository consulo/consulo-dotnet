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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XNavigatable;
import consulo.annotations.RequiredDispatchThread;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.psi.DotNetTypeDeclaration;

/**
 * @author VISTALL
 * @since 18.04.14
 */
public abstract class AbstractTypedValueNode extends XNamedValue
{
	@NotNull
	protected final DotNetDebugContext myDebugContext;

	@Nullable
	private Object myTypeProxy;

	public AbstractTypedValueNode(@NotNull DotNetDebugContext debugContext, @NotNull String name)
	{
		super(name);
		myDebugContext = debugContext;
	}

	@Nullable
	public DotNetTypeProxy getTypeOfVariable()
	{
		if(myTypeProxy != null)
		{
			Object typeProxy = myTypeProxy;
			return typeProxy == ObjectUtil.NULL ? null : (DotNetTypeProxy) typeProxy;
		}
		DotNetTypeProxy proxy = getTypeOfVariableImpl();
		if(proxy == null)
		{
			myTypeProxy = ObjectUtil.NULL;
			return null;
		}
		else
		{
			myTypeProxy = proxy;
			return proxy;
		}
	}

	@Nullable
	public abstract DotNetTypeProxy getTypeOfVariableImpl();

	@Override
	@RequiredDispatchThread
	public void computeTypeSourcePosition(@NotNull XNavigatable navigatable)
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

	@NotNull
	@RequiredDispatchThread
	public DotNetTypeDeclaration[] findTypesByQualifiedName(@NotNull DotNetTypeProxy typeMirror)
	{
		return DotNetVirtualMachineUtil.findTypesByQualifiedName(typeMirror, myDebugContext);
	}

	@Override
	public boolean canNavigateToTypeSource()
	{
		return getTypeOfVariable() != null;
	}
}
