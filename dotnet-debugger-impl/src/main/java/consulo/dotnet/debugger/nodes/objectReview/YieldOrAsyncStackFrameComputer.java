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
import consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import consulo.dotnet.debugger.nodes.DotNetFieldOrPropertyValueNode;
import consulo.dotnet.debugger.nodes.DotNetThisAsObjectValueNode;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.util.collection.ContainerUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public class YieldOrAsyncStackFrameComputer implements StackFrameComputer
{
	@Override
	public boolean computeStackFrame(@Nonnull final DotNetDebugContext debugContext,
			@Nullable final DotNetValueProxy thisObject,
			@Nonnull final DotNetStackFrameProxy stackFrameMirror,
			@Nonnull Set<Object> visitedVariables,
			@Nonnull final XValueChildrenList childrenList)
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

				final DotNetFieldProxy thisFieldMirror = ContainerUtil.find(fields, fieldMirror -> DotNetDebuggerCompilerGenerateUtil.isYieldOrAsyncThisField(fieldMirror.getName()));

				if(thisFieldMirror != null)
				{
					childrenList.add(new DotNetThisAsObjectValueNode(debugContext, stackFrameMirror, parentType, () -> (DotNetObjectValueProxy) thisFieldMirror.getValue(stackFrameMirror, thisObject)));
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
