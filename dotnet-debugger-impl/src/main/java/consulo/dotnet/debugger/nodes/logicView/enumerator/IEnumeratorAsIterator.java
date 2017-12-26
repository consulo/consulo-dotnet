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

import java.util.Iterator;

import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetNotSuspendedException;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThrowValueException;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class IEnumeratorAsIterator implements Iterator<DotNetValueProxy>
{
	private DotNetStackFrameProxy myFrameProxy;
	private DotNetValueProxy myValue;
	private DotNetMethodProxy myMoveNextMethod;
	private DotNetMethodProxy myCurrent;

	public IEnumeratorAsIterator(DotNetStackFrameProxy frameProxy, DotNetValueProxy value) throws CantCreateException
	{
		myFrameProxy = frameProxy;
		myValue = value;

		DotNetTypeProxy typeMirror = myValue.getType();

		myMoveNextMethod = DotNetDebuggerSearchUtil.findMethod("MoveNext", typeMirror);
		if(myMoveNextMethod == null)
		{
			throw new CantCreateException();
		}

		myCurrent = DotNetDebuggerSearchUtil.findGetterForProperty("Current", typeMirror);
		if(myCurrent == null)
		{
			throw new CantCreateException();
		}
	}

	@Override
	public boolean hasNext()
	{
		try
		{
			DotNetValueProxy invoke = myMoveNextMethod.invoke(myFrameProxy, myValue);
			return invoke instanceof DotNetBooleanValueProxy && ((DotNetBooleanValueProxy) invoke).getValue();
		}
		catch(DotNetThrowValueException | DotNetNotSuspendedException ignored)
		{
			return false;
		}
	}

	@Override
	public DotNetValueProxy next()
	{
		try
		{
			return myCurrent.invoke(myFrameProxy, myValue);
		}
		catch(DotNetThrowValueException | DotNetNotSuspendedException ignored)
		{
		}
		return null;
	}

	@Override
	public void remove()
	{
	}
}
