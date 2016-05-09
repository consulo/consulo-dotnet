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
import consulo.dotnet.debugger.nodes.DotNetLocalVariableValueNode;
import consulo.dotnet.debugger.nodes.DotNetMethodParameterValueNode;
import consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueNode;
import consulo.dotnet.debugger.nodes.DotNetThisAsStructValueNode;
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
			@NotNull DotNetStackFrameProxy frameProxy,
			@NotNull Set<Object> visitedVariables,
			@NotNull XValueChildrenList childrenList) throws DotNetInvalidObjectException, DotNetInvalidStackFrameException, DotNetAbsentInformationException
	{
		DotNetSourceLocation sourceLocation = frameProxy.getSourceLocation();
		if(sourceLocation == null)
		{
			return true;
		}

		DotNetMethodProxy method = sourceLocation.getMethod();

		DotNetValueProxy value = frameProxy.getThisObject();

		if(value instanceof DotNetObjectValueProxy)
		{
			DotNetTypeProxy type = value.getType();
			assert type != null;

			DotNetThisAsObjectValueNode.addStaticNode(childrenList, debugContext, frameProxy, type);

			childrenList.add(new DotNetThisAsObjectValueNode(debugContext, frameProxy, type, (DotNetObjectValueProxy) value));
		}
		else if(value instanceof DotNetStructValueProxy)
		{
			DotNetTypeProxy type = value.getType();
			assert type != null;

			DotNetThisAsObjectValueNode.addStaticNode(childrenList, debugContext, frameProxy, type);

			childrenList.add(new DotNetThisAsStructValueNode(debugContext, frameProxy, type, (DotNetStructValueProxy) value));
		}
		else
		{
			DotNetThisAsObjectValueNode.addStaticNode(childrenList, debugContext, frameProxy, sourceLocation.getMethod().getDeclarationType());
		}

		DotNetMethodParameterProxy[] parameters = method.getParameters();

		for(DotNetMethodParameterProxy parameter : parameters)
		{
			DotNetMethodParameterValueNode parameterMirrorNode = new DotNetMethodParameterValueNode(debugContext, parameter, frameProxy);

			visitedVariables.add(parameter);

			childrenList.add(parameterMirrorNode);
		}

		DotNetLocalVariableProxy[] localVariables = method.getLocalVariables(frameProxy);
		for(DotNetLocalVariableProxy local : localVariables)
		{
			if(StringUtil.isEmpty(local.getName()))
			{
				continue;
			}

			visitedVariables.add(local);

			DotNetLocalVariableValueNode localVariableMirrorNode = new DotNetLocalVariableValueNode(debugContext, local, frameProxy);

			childrenList.add(localVariableMirrorNode);
		}

		return true;
	}
}
