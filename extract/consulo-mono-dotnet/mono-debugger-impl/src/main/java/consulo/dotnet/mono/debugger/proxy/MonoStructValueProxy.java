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

package consulo.dotnet.mono.debugger.proxy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mono.debugger.FieldMirror;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.StructValueMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
public class MonoStructValueProxy extends MonoValueProxyBase<StructValueMirror> implements DotNetStructValueProxy
{
	public MonoStructValueProxy(StructValueMirror value)
	{
		super(value);
	}

	@NotNull
	@Override
	public DotNetStructValueProxy createNewStructValue(@NotNull Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map)
	{
		Collection<DotNetValueProxy> proxies = map.values();
		List<Value> values = ContainerUtil.map(proxies, new Function<DotNetValueProxy, Value>()
		{
			@Override
			public Value fun(DotNetValueProxy proxy)
			{
				return ((MonoValueProxyBase) proxy).getMirror();
			}
		});

		return new MonoStructValueProxy(new StructValueMirror(myValue.virtualMachine(), myValue.type(), values.toArray(new Value[values.size()])));
	}

	@NotNull
	@Override
	public Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> getValues()
	{
		Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> proxyMap = new LinkedHashMap<DotNetFieldOrPropertyProxy, DotNetValueProxy>();

		Map<FieldOrPropertyMirror, Value<?>> map = myValue.map();
		for(Map.Entry<FieldOrPropertyMirror, Value<?>> entry : map.entrySet())
		{
			DotNetFieldOrPropertyProxy proxy = null;
			FieldOrPropertyMirror key = entry.getKey();
			if(key instanceof FieldMirror)
			{
				proxy = new MonoFieldProxy((FieldMirror) key);
			}
			else if(key instanceof PropertyMirror)
			{
				proxy = new MonoPropertyProxy((PropertyMirror) key);
			}
			proxyMap.put(proxy, MonoValueProxyUtil.wrap(entry.getValue()));
		}
		return proxyMap;
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitStructValue(this);
	}
}
