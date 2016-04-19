package org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator;

import java.util.Iterator;

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
@Deprecated
public class IEnumeratorAsIterator implements Iterator<Value<?>>
{
	private ThreadMirror myThreadMirror;
	private Value<?> myValue;
	private MethodMirror myMoveNextMethod;
	private MethodMirror myCurrent;

	public IEnumeratorAsIterator(ThreadMirror threadMirror, Value<?> value) throws CantCreateException
	{
		myThreadMirror = threadMirror;
		myValue = value;

		TypeMirror typeMirror = myValue.type();

		myMoveNextMethod = MdbDebuggerUtil.findMethod("MoveNext", typeMirror);
		if(myMoveNextMethod == null)
		{
			throw new CantCreateException();
		}

		myCurrent = MdbDebuggerUtil.findGetterForProperty("Current", typeMirror);
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
			return myCurrent.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
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
