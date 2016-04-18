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

package org.mustbe.consulo.dotnet.debugger.nodes.objectReview;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetLocalVariableMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetMethodParameterMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetThisAsStructValueMirrorNode;
import org.mustbe.consulo.dotnet.debugger.proxy.DotNetStackFrameMirrorProxy;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.LocalVariableMirror;
import mono.debugger.MethodMirror;
import mono.debugger.MethodParameterMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.StructValueMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 27.03.2016
 */
public class DefaultStackFrameComputer implements StackFrameComputer
{
	@Override
	public boolean computeStackFrame(@NotNull DotNetDebugContext debugContext,
			@Nullable Value thisObject,
			@NotNull DotNetStackFrameMirrorProxy frameMirrorProxy,
			@NotNull Set<Object> visitedVariables,
			@NotNull XValueChildrenList childrenList)
	{
		MethodMirror method = frameMirrorProxy.location().method();

		final Value value = frameMirrorProxy.thisObject();

		if(value instanceof ObjectValueMirror)
		{
			TypeMirror type = value.type();
			assert type != null;

			DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, frameMirrorProxy.thread(), type);

			childrenList.add(new DotNetThisAsObjectValueMirrorNode(debugContext, frameMirrorProxy.thread(), type, (ObjectValueMirror) value));
		}
		else if(value instanceof StructValueMirror)
		{
			TypeMirror type = value.type();
			assert type != null;

			DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, frameMirrorProxy.thread(), type);

			childrenList.add(new DotNetThisAsStructValueMirrorNode(debugContext, frameMirrorProxy.thread(), type, (StructValueMirror) value));
		}
		else
		{
			DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, frameMirrorProxy.thread(), frameMirrorProxy.location().declaringType());
		}

		MethodParameterMirror[] parameters = method.parameters();

		for(MethodParameterMirror parameter : parameters)
		{
			DotNetMethodParameterMirrorNode parameterMirrorNode = new DotNetMethodParameterMirrorNode(debugContext, parameter, frameMirrorProxy);

			visitedVariables.add(parameter);

			childrenList.add(parameterMirrorNode);
		}

		try
		{
			LocalVariableMirror[] locals = method.locals(frameMirrorProxy.location().codeIndex());
			for(LocalVariableMirror local : locals)
			{
				if(StringUtil.isEmpty(local.name()))
				{
					continue;
				}
				visitedVariables.add(local);
				DotNetLocalVariableMirrorNode localVariableMirrorNode = new DotNetLocalVariableMirrorNode(debugContext, local, frameMirrorProxy);

				childrenList.add(localVariableMirrorNode);
			}
		}
		catch(IllegalArgumentException ignored)
		{
		}

		return true;
	}
}
