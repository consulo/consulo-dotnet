/*
 * Copyright 2013-2017 consulo.io
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

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.xdebugger.frame.XNavigatable;
import consulo.annotations.RequiredDispatchThread;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 19-Sep-17
 */
public class DotNetLocalVariableValueWrapperNode extends DotNetAbstractVariableValueNode
{
	private final DotNetFieldProxy myField;
	private final Supplier<DotNetObjectValueProxy> myObjectGetter;

	public DotNetLocalVariableValueWrapperNode(DotNetDebugContext debuggerContext, DotNetFieldProxy field, Supplier<DotNetObjectValueProxy> objectGetter, DotNetStackFrameProxy frameProxy)
	{
		super(debuggerContext, field.getName(), frameProxy);
		myField = field;
		myObjectGetter = objectGetter;
	}

	@Override
	public boolean canNavigateToSource()
	{
		return true;
	}

	@Override
	@RequiredDispatchThread
	public void computeSourcePosition(@NotNull XNavigatable navigatable)
	{
		DotNetLocalVariableValueNode.computeSourcePosition(navigatable, getName(), myDebugContext, myFrameProxy);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getTypeOfVariable()
	{
		return myField.getType();
	}

	@Nullable
	@Override
	public DotNetValueProxy getValueOfVariableImpl()
	{
		return myField.getValue(myFrameProxy, myObjectGetter.get());
	}

	@Override
	public void setValueForVariableImpl(@NotNull DotNetValueProxy value)
	{
		myField.setValue(myFrameProxy, myObjectGetter.get(), value);
	}
}
