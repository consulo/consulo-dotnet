/*
 * Copyright 2013-2017 consulo.io
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

package consulo.dotnet.debugger.impl.proxy;

import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetErrorValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import consulo.util.lang.ObjectUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 10-Oct-17
 */
public class DotNetErrorValueProxyImpl implements DotNetErrorValueProxy
{
	private final Throwable myThrowable;
	@Nullable
	private DotNetValueProxy myThrowObjectProxy;

	public DotNetErrorValueProxyImpl(@Nonnull Throwable throwable, @Nullable DotNetValueProxy throwObjectProxy)
	{
		myThrowable = throwable;
		myThrowObjectProxy = throwObjectProxy;
	}

	@Nonnull
	@Override
	public String getErrorMessage()
	{
		return ObjectUtil.notNull(myThrowable.getMessage(), "Unknown error");
	}

	@Nullable
	@Override
	public DotNetValueProxy getThrowObject()
	{
		return myThrowObjectProxy;
	}

	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return null;
	}

	@Nonnull
	@Override
	public Object getValue()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitErrorValue(this);
	}
}
