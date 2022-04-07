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

package consulo.dotnet.debugger.nodes.objectReview;

import consulo.debugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetAbsentInformationException;
import consulo.dotnet.debugger.proxy.DotNetInvalidObjectException;
import consulo.dotnet.debugger.proxy.DotNetInvalidStackFrameException;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public interface StackFrameComputer
{
	boolean computeStackFrame(@Nonnull DotNetDebugContext debugContext,
			@Nullable DotNetValueProxy thisObject,
			@Nonnull DotNetStackFrameProxy frameMirrorProxy,
			@Nonnull Set<Object> visitedVariables,
			@Nonnull XValueChildrenList childrenList) throws DotNetInvalidObjectException, DotNetAbsentInformationException, DotNetInvalidStackFrameException;
}
