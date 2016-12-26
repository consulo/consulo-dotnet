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

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Getter;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XValueChildrenList;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import consulo.dotnet.debugger.nodes.DotNetFieldOrPropertyValueNode;
import consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueNode;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public class YieldOrAsyncStackFrameComputer implements StackFrameComputer
{
	@Override
	public boolean computeStackFrame(@NotNull final DotNetDebugContext debugContext,
			@Nullable final DotNetValueProxy thisObject,
			@NotNull final DotNetStackFrameProxy stackFrameMirror,
			@NotNull Set<Object> visitedVariables,
			@NotNull final XValueChildrenList childrenList)
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		if(thisObject instanceof DotNetObjectValueProxy)
		{
			DotNetTypeProxy type = thisObject.getType();

			assert type != null;

			if(DotNetDebuggerCompilerGenerateUtil.isYieldOrAsyncNestedType(type))
			{
				DotNetTypeProxy parentType = type.getDeclarationType();

				if(parentType == null)
				{
					return false;
				}

				DotNetThisAsObjectValueNode.addStaticNode(childrenList, debugContext, stackFrameMirror, parentType);

				DotNetFieldProxy[] fields = type.getFields();

				final DotNetFieldProxy thisFieldMirror = ContainerUtil.find(fields, new Condition<DotNetFieldProxy>()
				{
					@Override
					public boolean value(DotNetFieldProxy fieldMirror)
					{
						String name = fieldMirror.getName();
						return DotNetDebuggerCompilerGenerateUtil.isYieldOrAsyncThisField(name);
					}
				});

				if(thisFieldMirror != null)
				{
					childrenList.add(new DotNetThisAsObjectValueNode(debugContext, stackFrameMirror, parentType, new Getter<DotNetObjectValueProxy>()
					{
						@Nullable
						@Override
						public DotNetObjectValueProxy get()
						{
							return (DotNetObjectValueProxy) thisFieldMirror.getValue(stackFrameMirror, thisObject);
						}
					}));
				}

				for(final DotNetFieldProxy field : fields)
				{
					String name = DotNetDebuggerCompilerGenerateUtil.extractNotGeneratedName(field.getName());
					if(name == null)
					{
						continue;
					}

					visitedVariables.add(field);

					childrenList.add(new DotNetFieldOrPropertyValueNode(debugContext, field, name, stackFrameMirror, (DotNetObjectValueProxy) thisObject));
				}
				return true;
			}
		}
		return false;
	}
}
