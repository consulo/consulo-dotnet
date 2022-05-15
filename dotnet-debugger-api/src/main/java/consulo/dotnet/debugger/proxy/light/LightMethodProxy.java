/*
 * Copyright 2013-2016 consulo.io
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

package consulo.dotnet.debugger.proxy.light;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.debugger.proxy.*;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.language.psi.PsiElement;
import consulo.project.Project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 28-Nov-16.
 */
public class LightMethodProxy implements DotNetMethodProxy
{
	private final String myName;
	private final DotNetTypeProxy myTypeProxy;

	public LightMethodProxy(DotNetMethodProxy method)
	{
		myName = method.getName();
		myTypeProxy = method.getDeclarationType().lightCopy();
	}

	@Override
	public boolean isStatic()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAbstract()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAnnotatedBy(@Nonnull String attributeVmQName)
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public DotNetTypeProxy getDeclarationType()
	{
		return myTypeProxy;
	}

	@Nonnull
	@Override
	public DotNetMethodParameterProxy[] getParameters()
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public DotNetLocalVariableProxy[] getLocalVariables(@Nonnull DotNetStackFrameProxy frameProxy)
	{
		throw new UnsupportedOperationException();
	}

	@Nullable
	@Override
	public DotNetValueProxy invoke(@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy thisObject,
			@Nonnull DotNetValueProxy... arguments) throws DotNetThrowValueException
	{
		throw new UnsupportedOperationException();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement findExecutableElementFromDebugInfo(@Nonnull Project project, int executableChildrenAtLineIndex)
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public String getName()
	{
		return myName;
	}
}
