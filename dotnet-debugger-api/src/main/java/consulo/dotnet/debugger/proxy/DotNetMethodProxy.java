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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.debugger.proxy.light.LightMethodProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.util.pointers.Named;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetMethodProxy extends Named
{
	boolean isStatic();

	boolean isAbstract();

	boolean isAnnotatedBy(@Nonnull String attributeVmQName);

	@Nonnull
	DotNetTypeProxy getDeclarationType();

	@Nonnull
	DotNetMethodParameterProxy[] getParameters();

	@Nonnull
	DotNetLocalVariableProxy[] getLocalVariables(@Nonnull DotNetStackFrameProxy frameProxy);

	@Nullable
	DotNetValueProxy invoke(@Nonnull DotNetStackFrameProxy frameProxy,
			@Nullable DotNetValueProxy thisObject,
			@Nonnull DotNetValueProxy... arguments) throws DotNetThrowValueException, DotNetNotSuspendedException;

	@Nullable
	@RequiredReadAction
	PsiElement findExecutableElementFromDebugInfo(@Nonnull Project project, int executableChildrenAtLineIndex);

	@Nonnull
	default DotNetMethodProxy lightCopy()
	{
		return new LightMethodProxy(this);
	}
}
