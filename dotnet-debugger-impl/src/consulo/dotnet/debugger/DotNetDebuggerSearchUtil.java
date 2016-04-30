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

package consulo.dotnet.debugger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.containers.ContainerUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetThrowValueException;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class DotNetDebuggerSearchUtil
{
	public static DotNetFieldOrPropertyProxy[] getFieldAndProperties(@NotNull DotNetTypeProxy proxy, boolean deep)
	{
		List<DotNetFieldOrPropertyProxy> proxies = new ArrayList<DotNetFieldOrPropertyProxy>();

		collectFieldsAndProperties(proxy, proxies);

		Collections.sort(proxies, new Comparator<DotNetFieldOrPropertyProxy>()
		{
			@Override
			public int compare(DotNetFieldOrPropertyProxy o1, DotNetFieldOrPropertyProxy o2)
			{
				return weight(o2) - weight(o1);
			}

			private int weight(DotNetFieldOrPropertyProxy p)
			{
				return p instanceof DotNetPropertyProxy ? 2 : 1;
			}
		});

		return ContainerUtil.toArray(proxies, DotNetFieldOrPropertyProxy.ARRAY_FACTORY);
	}

	private static void collectFieldsAndProperties(DotNetTypeProxy proxy, List<DotNetFieldOrPropertyProxy> list)
	{
		Collections.addAll(list, proxy.getFields());
		Collections.addAll(list, proxy.getProperties());

		DotNetTypeProxy baseType = proxy.getBaseType();
		if(baseType != null)
		{
			collectFieldsAndProperties(baseType, list);
		}
	}

	public static void rethrow(DotNetThreadProxy mirror, Exception t)
	{
		if(!(t instanceof DotNetThrowValueException))
		{
			return;
		}
		DotNetValueProxy throwExceptionValue = ((DotNetThrowValueException) t).getThrowExceptionValue();

		String value = toStringValue(mirror, throwExceptionValue);
		if(value != null)
		{
			throw new IllegalArgumentException(value);
		}
	}

	@Nullable
	public static String toStringValue(@NotNull DotNetThreadProxy threadProxy, @NotNull DotNetValueProxy valueProxy)
	{
		try
		{
			DotNetTypeProxy type = valueProxy.getType();
			if(type == null)
			{
				return null;
			}
			DotNetMethodProxy toString = type.findMethodByName("ToString", true);
			DotNetValueProxy invoke = null;
			assert toString != null;
			invoke = toString.invoke(threadProxy, valueProxy);
			if(!(invoke instanceof DotNetStringValueProxy))
			{
				return null;
			}
			return (String) invoke.getValue();
		}
		catch(DotNetThrowValueException ignored)
		{
		}
		return null;
	}

	public static boolean isInImplementList(DotNetTypeProxy typeMirror, String qName)
	{
		for(DotNetTypeProxy mirror : typeMirror.getInterfaces())
		{
			if(mirror.getFullName().equals(qName))
			{
				return true;
			}
		}

		DotNetTypeProxy b = typeMirror.getBaseType();
		return b != null && isInImplementList(b, qName);
	}

	public static DotNetMethodProxy findMethod(String method, DotNetTypeProxy typeMirror)
	{
		DotNetMethodProxy[] methods = typeMirror.getMethods();
		for(DotNetMethodProxy methodMirror : methods)
		{
			if(methodMirror.getName().equals(method) && methodMirror.getParameters().length == 0)
			{
				return methodMirror;
			}
		}

		List<DotNetTypeProxy> mirrors = new ArrayList<DotNetTypeProxy>(5);
		DotNetTypeProxy b = typeMirror.getBaseType();
		if(b != null)
		{
			mirrors.add(b);
		}

		Collections.addAll(mirrors, typeMirror.getInterfaces());
		for(DotNetTypeProxy mirror : mirrors)
		{
			DotNetMethodProxy temp = findMethod(method, mirror);
			if(temp != null)
			{
				return temp;
			}
		}
		return null;
	}

	public static DotNetMethodProxy findGetterForProperty(String property, DotNetTypeProxy typeMirror)
	{
		for(DotNetPropertyProxy propertyMirror : typeMirror.getProperties())
		{
			if(propertyMirror.getName().equals(property))
			{
				return propertyMirror.getGetMethod();
			}
		}

		DotNetTypeProxy baseType = typeMirror.getBaseType();
		if(baseType == null)
		{
			return null;
		}
		DotNetMethodProxy getterForProperty = findGetterForProperty(property, baseType);
		if(getterForProperty != null)
		{
			return getterForProperty;
		}
		return null;
	}
}
