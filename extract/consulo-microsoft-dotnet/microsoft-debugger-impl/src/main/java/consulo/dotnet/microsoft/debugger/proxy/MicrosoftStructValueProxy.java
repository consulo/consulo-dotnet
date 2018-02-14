package consulo.dotnet.microsoft.debugger.proxy;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mssdw.FieldMirror;
import mssdw.FieldOrPropertyMirror;
import mssdw.PropertyMirror;
import mssdw.StructValueMirror;
import mssdw.Value;

/**
 * @author VISTALL
 * @since 5/10/2016
 */
public class MicrosoftStructValueProxy extends MicrosoftValueProxyBase<StructValueMirror> implements DotNetStructValueProxy
{
	public MicrosoftStructValueProxy(StructValueMirror value)
	{
		super(value);
	}

	@Nonnull
	@Override
	public DotNetStructValueProxy createNewStructValue(@Nonnull Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map)
	{
		throw new UnsupportedOperationException();
		/*Collection<DotNetValueProxy> proxies = map.values();
		List<Value> values = ContainerUtil.map(proxies, new Function<DotNetValueProxy, Value>()
		{
			@Override
			public Value fun(DotNetValueProxy proxy)
			{
				return ((MonoValueProxyBase) proxy).getMirror();
			}
		});

		return new MicrosoftStructValueProxy(new StructValueMirror(myValue.virtualMachine(), myValue.type(), values.toArray(new Value[values.size()]))); */
	}

	@Nonnull
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
				proxy = new MicrosoftFieldProxy((FieldMirror) key);
			}
			else if(key instanceof PropertyMirror)
			{
				proxy = new MicrosoftPropertyProxy((PropertyMirror) key);
			}
			proxyMap.put(proxy, MicrosoftValueProxyUtil.wrap(entry.getValue()));
		}
		return proxyMap;
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitStructValue(this);
	}
}
