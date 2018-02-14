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

package consulo.dotnet.microsoft.debugger.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mssdw.MethodMirror;
import mssdw.ObjectValueMirror;
import mssdw.PropertyMirror;

/**
 * @author VISTALL
 * @since 5/10/2016
 */
public class MicrosoftPropertyProxy extends MicrosoftVariableProxyBase<PropertyMirror> implements DotNetPropertyProxy
{
	public MicrosoftPropertyProxy(@Nonnull PropertyMirror mirror)
	{
		super(mirror);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return MicrosoftTypeProxy.of(myMirror.type());
	}

	@Nonnull
	@Override
	public DotNetTypeProxy getParentType()
	{
		return MicrosoftTypeProxy.of(myMirror.parent());
	}

	@Override
	public boolean isStatic()
	{
		return myMirror.isStatic();
	}

	@Nullable
	@Override
	public DotNetValueProxy getValue(@Nonnull DotNetStackFrameProxy frameProxy, @Nullable DotNetValueProxy proxy)
	{
		MicrosoftStackFrameProxy msStackFrameProxy = (MicrosoftStackFrameProxy) frameProxy;
		MicrosoftValueProxyBase<?> msValueProxy = (MicrosoftValueProxyBase<?>) proxy;
		return MicrosoftValueProxyUtil.wrap(myMirror.value(msStackFrameProxy.getFrameMirror(), msValueProxy == null ? null : (ObjectValueMirror) msValueProxy.getMirror()));
	}

	@Override
	public void setValue(@Nonnull DotNetStackFrameProxy threadProxy, @Nullable DotNetValueProxy proxy, @Nonnull DotNetValueProxy newValueProxy)
	{
		//MonoThreadProxy monoThreadProxy = (MonoThreadProxy) threadProxy.getThread();
		//MonoObjectValueProxy monoValueProxyBase = (MonoObjectValueProxy) proxy;
		//MonoValueProxyBase<?> monoNewValueProxyBase = (MonoValueProxyBase<?>) newValueProxy;

		//myMirror.setValue(monoThreadProxy.getThreadMirror(), monoValueProxyBase == null ? null : monoValueProxyBase.getMirror(), monoNewValueProxyBase.getMirror());
	}

	@Override
	public boolean isArrayProperty()
	{
		return myMirror.isArrayProperty();
	}

	@Nullable
	@Override
	public DotNetMethodProxy getGetMethod()
	{
		MethodMirror methodMirror = myMirror.methodGet();
		return methodMirror == null ? null : new MicrosoftMethodProxy(methodMirror);
	}
}
