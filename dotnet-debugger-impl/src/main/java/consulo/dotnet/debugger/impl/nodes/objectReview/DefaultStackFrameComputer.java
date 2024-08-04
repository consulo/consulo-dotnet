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

package consulo.dotnet.debugger.impl.nodes.objectReview;

import consulo.dotnet.debugger.impl.nodes.*;
import consulo.execution.debug.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.impl.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.proxy.*;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Set;

/**
 * @author VISTALL
 * @since 27.03.2016
 */
public class DefaultStackFrameComputer implements StackFrameComputer
{
	@Override
	public boolean computeStackFrame(@Nonnull DotNetDebugContext debugContext,
			@Nullable DotNetValueProxy value,
			@Nonnull DotNetStackFrameProxy frameProxy,
			@Nonnull Set<Object> visitedVariables,
			@Nonnull XValueChildrenList childrenList) throws DotNetInvalidObjectException, DotNetInvalidStackFrameException, DotNetAbsentInformationException
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		DotNetSourceLocation sourceLocation = frameProxy.getSourceLocation();
		if(sourceLocation == null)
		{
			return true;
		}

		DotNetMethodProxy method = sourceLocation.getMethod();

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
			String name = local.getName();
			if(StringUtil.isEmpty(name))
			{
				continue;
			}

			visitedVariables.add(local);

			if(DotNetDebuggerCompilerGenerateUtil.isLocalVarWrapper(name))
			{
				DotNetTypeProxy type = local.getType();
				if(type == null)
				{
					continue;
				}

				DotNetFieldProxy[] fields = type.getFields();

				if(fields.length == 0)
				{
					continue;
				}

				childrenList.add(new DotNetLocalVariableValueWrapperNode(debugContext, fields[0], () -> frameProxy.getLocalValue(local), frameProxy));
			}
			else
			{

				childrenList.add(new DotNetLocalVariableValueNode(debugContext, local, frameProxy));
			}
		}

		return true;
	}
}
