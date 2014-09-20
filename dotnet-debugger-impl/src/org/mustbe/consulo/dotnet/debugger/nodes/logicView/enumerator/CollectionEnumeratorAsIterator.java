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

package org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator;

import java.util.Iterator;

import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.MdbDebuggerUtil;
import mono.debugger.BooleanValueMirror;
import mono.debugger.InvokeFlags;
import mono.debugger.MethodMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.ThrowValueException;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class CollectionEnumeratorAsIterator implements Iterator<Value<?>>
{
	private ThreadMirror myThreadMirror;
	private Value<?> myValue;
	private MethodMirror myMoveNextMethod;
	private MethodMirror myCurrentMethod;

	public CollectionEnumeratorAsIterator(ThreadMirror threadMirror, Value<?> value, DotNetDebugContext debugContext) throws CantCreateException
	{
		myThreadMirror = threadMirror;
		myValue = value;

		TypeMirror typeMirror = myValue.type();

		myMoveNextMethod = MdbDebuggerUtil.findMethod("MoveNext", typeMirror);
		if(myMoveNextMethod == null)
		{
			throw new CantCreateException();
		}
		System.out.println(myMoveNextMethod.declaringType());

		myCurrentMethod = MdbDebuggerUtil.findGetterForProperty("Current", typeMirror);
		if(myCurrentMethod == null)
		{
			throw new CantCreateException();
		}
	}

	@Override
	public boolean hasNext()
	{
		try
		{
			Value<?> invoke = myMoveNextMethod.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
			return invoke instanceof BooleanValueMirror && ((BooleanValueMirror) invoke).value();
		}
		catch(ThrowValueException e)
		{
			//Value<?> throwExceptionValue = e.getThrowExceptionValue();
			//TypeMirror type = throwExceptionValue.type();

			//MethodMirror toString = type.findMethodByName("ToString", true);
			//System.out.println(toString.invoke(myThreadMirror, throwExceptionValue));
			return false;
		}
	}

	@Override
	public Value<?> next()
	{
		try
		{
			return myCurrentMethod.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
		}
		catch(ThrowValueException e)
		{
			//Value<?> throwExceptionValue = e.getThrowExceptionValue();
			//TypeMirror type = throwExceptionValue.type();

			//MethodMirror toString = type.findMethodByName("ToString", true);
			//System.out.println(toString.invoke(myThreadMirror, throwExceptionValue));
		}
		return null;
	}

	@Override
	public void remove()
	{

	}
}
