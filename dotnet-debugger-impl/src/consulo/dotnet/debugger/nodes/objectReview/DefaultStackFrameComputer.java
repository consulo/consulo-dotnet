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

package consulo.dotnet.debugger.nodes.objectReview;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.DotNetLocalVariableMirrorNode;
import consulo.dotnet.debugger.nodes.DotNetMethodParameterMirrorNode;
import consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueMirrorNode;
import consulo.dotnet.debugger.nodes.DotNetThisAsStructValueMirrorNode;
import consulo.dotnet.debugger.proxy.DotNetAbsentInformationException;
import consulo.dotnet.debugger.proxy.DotNetInvalidObjectException;
import consulo.dotnet.debugger.proxy.DotNetInvalidStackFrameException;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 27.03.2016
 */
public class DefaultStackFrameComputer implements StackFrameComputer
{
	@Override
	public boolean computeStackFrame(@NotNull DotNetDebugContext debugContext,
			@Nullable DotNetValueProxy thisObject,
			@NotNull DotNetStackFrameProxy frameMirrorProxy,
			@NotNull Set<Object> visitedVariables,
			@NotNull XValueChildrenList childrenList) throws DotNetInvalidObjectException, DotNetInvalidStackFrameException, DotNetAbsentInformationException
	{
		DotNetSourceLocation sourceLocation = frameMirrorProxy.getSourceLocation();
		if(sourceLocation == null)
		{
			return true;
		}

		DotNetMethodProxy method = sourceLocation.getMethod();

		DotNetValueProxy value = frameMirrorProxy.getThisObject();

		if(value instanceof DotNetObjectValueProxy)
		{
			DotNetTypeProxy type = value.getType();
			assert type != null;

			DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, frameMirrorProxy.getThread(), type);

			childrenList.add(new DotNetThisAsObjectValueMirrorNode(debugContext, frameMirrorProxy.getThread(), type, (DotNetObjectValueProxy) value));
		}
		else if(value instanceof DotNetStructValueProxy)
		{
			DotNetTypeProxy type = value.getType();
			assert type != null;

			DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, frameMirrorProxy.getThread(), type);

			childrenList.add(new DotNetThisAsStructValueMirrorNode(debugContext, frameMirrorProxy.getThread(), type, (DotNetStructValueProxy) value));
		}
		else
		{
			DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, frameMirrorProxy.getThread(), sourceLocation.getMethod().getDeclarationType());
		}

		DotNetMethodParameterProxy[] parameters = method.getParameters();

		for(DotNetMethodParameterProxy parameter : parameters)
		{
			DotNetMethodParameterMirrorNode parameterMirrorNode = new DotNetMethodParameterMirrorNode(debugContext, parameter, frameMirrorProxy);

			visitedVariables.add(parameter);

			childrenList.add(parameterMirrorNode);
		}

		DotNetLocalVariableProxy[] localVariables = method.getLocalVariables(frameMirrorProxy);
		for(DotNetLocalVariableProxy local : localVariables)
		{
			if(StringUtil.isEmpty(local.getName()))
			{
				continue;
			}

			visitedVariables.add(local);

			DotNetLocalVariableMirrorNode localVariableMirrorNode = new DotNetLocalVariableMirrorNode(debugContext, local, frameMirrorProxy);

			childrenList.add(localVariableMirrorNode);
		}

		return true;
	}
}
