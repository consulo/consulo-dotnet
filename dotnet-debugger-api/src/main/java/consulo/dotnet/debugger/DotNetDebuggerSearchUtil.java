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

import consulo.dotnet.debugger.proxy.*;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.*;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class DotNetDebuggerSearchUtil
{
	public static DotNetFieldOrPropertyProxy[] getFieldAndProperties(@Nonnull DotNetTypeProxy proxy, boolean deep)
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

	public static void rethrow(DotNetStackFrameProxy mirror, Exception t)
	{
		if(!(t instanceof DotNetThrowValueException))
		{
			return;
		}
		throw new IllegalArgumentException(t.getMessage());
	}

	@Nullable
	public static String toStringValue(@Nonnull DotNetStackFrameProxy threadProxy, @Nonnull DotNetValueProxy valueProxy)
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
		catch(DotNetThrowValueException | DotNetNotSuspendedException ignored)
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

	@Nullable
	public static DotNetMethodProxy findMethodImplementation(DotNetVirtualMachineProxy proxy, String vmQName, String methodName, DotNetTypeProxy typeMirror)
	{
		DotNetTypeProxy typeInCorlib = proxy.findTypeInCorlib(vmQName);
		if(typeInCorlib == null)
		{
			return null;
		}

		String hideImplMethodName = getVmQTypeNameForHideImplementation(typeInCorlib, vmQName) + "." + methodName;

		Map<String, DotNetMethodProxy> result = new LinkedHashMap<>();

		collectMethodImplementation0(hideImplMethodName, methodName, typeMirror, result);

		// first return hide impl
		for(DotNetMethodProxy impl : result.values())
		{
			int i = impl.getName().indexOf('.');
			if(i > 0)
			{
				return impl;
			}
		}
		return ContainerUtil.getFirstItem(result.values());
	}

	private static void collectMethodImplementation0(String hideImplMethodName, String methodName, DotNetTypeProxy typeMirror, Map<String, DotNetMethodProxy> result)
	{
		DotNetMethodProxy[] methods = typeMirror.getMethods();
		for(DotNetMethodProxy methodMirror : methods)
		{
			String name = methodMirror.getName();
			if(name.equals(methodName) && methodMirror.getParameters().length == 0)
			{
				result.putIfAbsent(methodMirror.getName(), methodMirror);
			}
			// hide implementation
			else if(name.equals(hideImplMethodName))
			{
				result.putIfAbsent(methodMirror.getName(), methodMirror);
			}
		}

		DotNetTypeProxy baseType = typeMirror.getBaseType();
		if(baseType != null)
		{
			collectMethodImplementation0(hideImplMethodName, methodName, baseType, result);
		}
	}

	@Nonnull
	private static String getVmQTypeNameForHideImplementation(DotNetTypeProxy mirror, String vmQName)
	{
		String[] genericParameters = mirror.getGenericParameters();
		if(genericParameters.length > 0)
		{
			return MsilHelper.cutGenericMarker(vmQName) + "<" + StringUtil.join(genericParameters, ",") + ">";
		}
		else
		{
			return mirror.getFullName();
		}
	}

	@Nullable
	public static DotNetMethodProxy findGetterForPropertyImplementation(DotNetVirtualMachineProxy proxy, String vmQName, String property, DotNetTypeProxy typeMirror)
	{
		return findMethodImplementation(proxy, vmQName, "get_" + property, typeMirror);
	}

	@Deprecated
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

	@Deprecated
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
