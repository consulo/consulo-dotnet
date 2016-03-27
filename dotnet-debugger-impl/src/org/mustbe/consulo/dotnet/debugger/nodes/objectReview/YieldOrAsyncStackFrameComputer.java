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
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetFieldOrPropertyMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueMirrorNode;
import org.mustbe.consulo.dotnet.debugger.proxy.DotNetStackFrameMirrorProxy;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Getter;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.FieldMirror;
import mono.debugger.InvalidObjectException;
import mono.debugger.ObjectValueMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public class YieldOrAsyncStackFrameComputer implements StackFrameComputer
{
	@Override
	public boolean computeStackFrame(@NotNull final DotNetDebugContext debugContext,
			@Nullable final Value thisObject,
			@NotNull final DotNetStackFrameMirrorProxy stackFrameMirror,
			@NotNull Set<Object> visitedVariables,
			@NotNull final XValueChildrenList childrenList)
	{
		if(thisObject instanceof ObjectValueMirror)
		{
			TypeMirror type;
			try
			{
				type = thisObject.type();
			}
			catch(InvalidObjectException e)
			{
				return false;
			}

			assert type != null;

			if(DotNetDebuggerCompilerGenerateUtil.isYieldOrAsyncNestedType(type))
			{
				TypeMirror parentType = type.parentType();

				if(parentType == null)
				{
					return false;
				}

				DotNetThisAsObjectValueMirrorNode.addStaticNode(childrenList, debugContext, stackFrameMirror.thread(), parentType);

				FieldMirror[] fields = type.fields();

				final FieldMirror thisFieldMirror = ContainerUtil.find(fields, new Condition<FieldMirror>()
				{
					@Override
					public boolean value(FieldMirror fieldMirror)
					{
						String name = fieldMirror.name();
						return DotNetDebuggerCompilerGenerateUtil.isYieldOrAsyncThisField(name);
					}
				});

				if(thisFieldMirror != null)
				{
					childrenList.add(new DotNetThisAsObjectValueMirrorNode(debugContext, stackFrameMirror.thread(), parentType, new Getter<ObjectValueMirror>()
					{
						@Nullable
						@Override
						public ObjectValueMirror get()
						{
							return (ObjectValueMirror) thisFieldMirror.value(stackFrameMirror.thread(), (ObjectValueMirror) thisObject);
						}
					}));
				}

				for(final FieldMirror field : fields)
				{
					String name = DotNetDebuggerCompilerGenerateUtil.extractNotGeneratedName(field.name());
					if(name == null)
					{
						continue;
					}

					visitedVariables.add(field);

					childrenList.add(new DotNetFieldOrPropertyMirrorNode(debugContext, field, name, stackFrameMirror.thread(), (ObjectValueMirror) thisObject));
				}
				return true;
			}
		}
		return false;
	}
}
