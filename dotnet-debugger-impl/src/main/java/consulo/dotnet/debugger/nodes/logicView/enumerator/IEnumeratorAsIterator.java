/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.debugger.nodes.logicView.enumerator;

import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.proxy.DotNetMethodInvokeResult;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetNotSuspendedException;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class IEnumeratorAsIterator
{
	private DotNetStackFrameProxy myFrameProxy;
	private DotNetValueProxy myValue;
	private DotNetMethodProxy myMoveNextMethod;
	private DotNetMethodProxy myCurrent;

	public IEnumeratorAsIterator(DotNetVirtualMachineProxy virtualMachineProxy, DotNetStackFrameProxy frameProxy, DotNetValueProxy value, DotNetTypeProxy typeOfValue) throws CantCreateException
	{
		myFrameProxy = frameProxy;
		myValue = value;

		myMoveNextMethod = DotNetDebuggerSearchUtil.findMethodImplementation(virtualMachineProxy, DotNetTypes.System.Collections.IEnumerator, "MoveNext", typeOfValue);
		if(myMoveNextMethod == null)
		{
			throw new CantCreateException();
		}

		myCurrent = DotNetDebuggerSearchUtil.findGetterForPropertyImplementation(virtualMachineProxy, DotNetTypes.System.Collections.IEnumerator, "Current", typeOfValue);

		if(myCurrent == null)
		{
			throw new CantCreateException();
		}
	}

	public boolean hasNext() throws DotNetNotSuspendedException
	{
		DotNetMethodInvokeResult result = myMoveNextMethod.invokeAdvanced(myFrameProxy, myValue);
		DotNetValueProxy invoke = result.getResult();
		boolean hasNext = invoke instanceof DotNetBooleanValueProxy && ((DotNetBooleanValueProxy) invoke).getValue();
		DotNetValueProxy outThis = result.getOutThis();
		if(outThis != null)
		{
			myValue = outThis;
		}
		return hasNext;
	}

	public DotNetValueProxy next() throws DotNetNotSuspendedException
	{
		DotNetMethodInvokeResult result = myCurrent.invokeAdvanced(myFrameProxy, myValue);
		DotNetValueProxy outThis = result.getOutThis();
		if(outThis != null)
		{
			myValue = outThis;
		}
		return result.getResult();
	}
}
