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

package org.mustbe.consulo.dotnet.debugger.nodes.logicView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mono.debugger.InvokeFlags;
import mono.debugger.MethodMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.StringValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.ThrowValueException;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
@Deprecated
public class MdbDebuggerUtil
{
	public static void rethrow(ThreadMirror mirror, Exception t)
	{
		if(!(t instanceof ThrowValueException))
		{
			return;
		}
		Value<?> throwExceptionValue = ((ThrowValueException) t).getThrowExceptionValue();
		TypeMirror type = throwExceptionValue.type();

		MethodMirror toString = type.findMethodByName("ToString", true);
		Value<?> invoke = toString.invoke(mirror, InvokeFlags.DISABLE_BREAKPOINTS, throwExceptionValue);
		if(!(invoke instanceof StringValueMirror))
		{
			return;
		}
		throw new IllegalArgumentException(((StringValueMirror) invoke).value());
	}
	public static boolean isInImplementList(TypeMirror typeMirror, String qName)
	{
		for(TypeMirror mirror : typeMirror.getInterfaces())
		{
			if(mirror.qualifiedName().equals(qName))
			{
				return true;
			}
		}

		TypeMirror b = typeMirror.baseType();
		return b != null && isInImplementList(b, qName);
	}

	public static MethodMirror findMethod(String method, TypeMirror typeMirror)
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

	public static MethodMirror findGetterForProperty(String property, TypeMirror typeMirror)
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

}
