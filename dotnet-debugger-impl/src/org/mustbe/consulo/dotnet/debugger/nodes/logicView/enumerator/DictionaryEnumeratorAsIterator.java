package org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.openapi.util.Pair;
import mono.debugger.BooleanValueMirror;
import mono.debugger.InvokeFlags;
import mono.debugger.MethodMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.ThrowValueException;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DictionaryEnumeratorAsIterator implements Iterator<Pair<Value<?>, Value<?>>>
{
	private ThreadMirror myThreadMirror;
	private Value<?> myValue;
	private MethodMirror myMoveNextMethod;
	private MethodMirror myKeyMethod;
	private MethodMirror myValueMethod;

	public DictionaryEnumeratorAsIterator(ThreadMirror threadMirror, Value<?> value, DotNetDebugContext debugContext) throws CantCreateException
	{
		myThreadMirror = threadMirror;
		myValue = value;

		TypeMirror typeMirror = myValue.type();

		myMoveNextMethod = findMethod("MoveNext", typeMirror);
		if(myMoveNextMethod == null)
		{
			throw new CantCreateException();
		}
		System.out.println(myMoveNextMethod.declaringType());

		myKeyMethod = findGetterForProperty("Key", typeMirror);
		if(myKeyMethod == null)
		{
			throw new CantCreateException();
		}
		myValueMethod = findGetterForProperty("Value", typeMirror);
		if(myValueMethod == null)
		{
			throw new CantCreateException();
		}
	}

	private static MethodMirror findMethod(String method, TypeMirror typeMirror)
	{
		MethodMirror[] methods = typeMirror.methods();
		for(MethodMirror methodMirror : methods)
		{
			if(methodMirror.name().equals(method) && methodMirror.parameters().length == 0)
			{
				return methodMirror;
			}
		}

		List<TypeMirror> mirrors = new ArrayList<TypeMirror>(5);
		TypeMirror b = typeMirror.baseType();
		if(b != null)
		{
			mirrors.add(b);
		}

		Collections.addAll(mirrors, typeMirror.getInterfaces());
		for(TypeMirror mirror : mirrors)
		{
			MethodMirror temp = findMethod(method, mirror);
			if(temp != null)
			{
				return temp;
			}
		}
		return null;
	}

	private static MethodMirror findGetterForProperty(String property, TypeMirror typeMirror)
	{
		for(PropertyMirror propertyMirror : typeMirror.properties())
		{
			if(propertyMirror.name().equals(property))
			{
				return propertyMirror.methodGet();
			}
		}

		TypeMirror baseType = typeMirror.baseType();
		if(baseType == null)
		{
			return null;
		}
		MethodMirror getterForProperty = findGetterForProperty(property, baseType);
		if(getterForProperty != null)
		{
			return getterForProperty;
		}
		return null;
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
	public Pair<Value<?>, Value<?>> next()
	{
		try
		{
			Value<?> keyValue = myKeyMethod.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
			Value<?> valueValue = myValueMethod.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
			return Pair.<Value<?>, Value<?>>create(keyValue, valueValue);
		}
		catch(ThrowValueException e)
		{
			//Value<?> throwExceptionValue = e.getThrowExceptionValue();
			//TypeMirror type = throwExceptionValue.type();

			//MethodMirror toString = type.findMethodByName("ToString", true);
			//System.out.println(toString.invoke(myThreadMirror, throwExceptionValue));
		}
		return Pair.create(null, null);
	}

	@Override
	public void remove()
	{

	}
}
