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

import org.consulo.util.pointers.Named;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetMethodProxy extends Named
{
	boolean isStatic();

	@NotNull
	DotNetTypeProxy getDeclarationType();

	@NotNull
	DotNetMethodParameterProxy[] getParameters();

	@NotNull
	DotNetLocalVariableProxy[] getLocalVariables(@NotNull DotNetStackFrameProxy frameProxy);

	@Nullable
	DotNetValueProxy invoke(@NotNull DotNetThreadProxy threadMirror, @NotNull DotNetValueProxy thisObject, @NotNull DotNetValueProxy... arguments) throws DotNetThrowValueException;

	@Nullable
	@RequiredReadAction
	PsiElement findExecutableElementFromDebugInfo(@NotNull Project project, int executableChildrenAtLineIndex);
}
