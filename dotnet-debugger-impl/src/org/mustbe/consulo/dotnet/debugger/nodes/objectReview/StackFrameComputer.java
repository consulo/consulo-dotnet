/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.dotnet.debugger.nodes.objectReview;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.proxy.DotNetStackFrameMirrorProxy;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public interface StackFrameComputer
{
	boolean computeStackFrame(@NotNull DotNetDebugContext debugContext,
			@Nullable Value thisObject,
			@NotNull DotNetStackFrameMirrorProxy frameMirrorProxy,
			@NotNull Set<Object> visitedVariables,
			@NotNull XValueChildrenList childrenList);
}
