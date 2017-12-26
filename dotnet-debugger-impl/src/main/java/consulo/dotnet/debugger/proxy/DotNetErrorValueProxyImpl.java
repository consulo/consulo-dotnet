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

package consulo.dotnet.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.value.DotNetErrorValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;

/**
 * @author VISTALL
 * @since 10-Oct-17
 */
public class DotNetErrorValueProxyImpl implements DotNetErrorValueProxy
{
	private final Throwable myThrowable;
	@Nullable
	private DotNetValueProxy myThrowObjectProxy;

	public DotNetErrorValueProxyImpl(@NotNull Throwable throwable, @Nullable DotNetValueProxy throwObjectProxy)
	{
		myThrowable = throwable;
		myThrowObjectProxy = throwObjectProxy;
	}

	@NotNull
	@Override
	public String getErrorMessage()
	{
		return myThrowable.getMessage();
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

	@NotNull
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
