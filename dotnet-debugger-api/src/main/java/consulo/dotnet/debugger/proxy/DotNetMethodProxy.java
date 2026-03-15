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

package consulo.dotnet.debugger.proxy;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.debugger.proxy.light.LightMethodProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.language.psi.PsiElement;
import consulo.project.Project;

import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetMethodProxy
{
	String getName();

	boolean isStatic();

	boolean isAbstract();

	boolean isAnnotatedBy(String attributeVmQName);

	DotNetTypeProxy getDeclarationType();

	DotNetMethodParameterProxy[] getParameters();

	DotNetLocalVariableProxy[] getLocalVariables(DotNetStackFrameProxy frameProxy);

	@Nullable
	DotNetValueProxy invoke(DotNetStackFrameProxy frameProxy,
							@Nullable DotNetValueProxy thisObject,
							DotNetValueProxy... arguments) throws DotNetThrowValueException, DotNetNotSuspendedException;

	default DotNetMethodInvokeResult invokeAdvanced(DotNetStackFrameProxy frameProxy,
													@Nullable DotNetValueProxy thisObject,
													DotNetValueProxy... arguments) throws DotNetThrowValueException, DotNetNotSuspendedException
	{
		DotNetValueProxy valueProxy = invoke(frameProxy, thisObject, arguments);
		assert valueProxy != null;
		return new DotNetMethodInvokeResult(valueProxy, null);
	}

	@Nullable
	@RequiredReadAction
	PsiElement findExecutableElementFromDebugInfo(Project project, int executableChildrenAtLineIndex);

	default DotNetMethodProxy lightCopy()
	{
		return new LightMethodProxy(this);
	}
}
