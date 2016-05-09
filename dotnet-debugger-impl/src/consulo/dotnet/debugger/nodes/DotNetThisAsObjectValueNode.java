/*
 * Copyright 2013-2014 must-be.org
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

package consulo.dotnet.debugger.nodes;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.Getter;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetThisAsObjectValueNode extends DotNetAbstractVariableValueNode
{
	public static void addStaticNode(@NotNull XValueChildrenList list,
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetStackFrameProxy frameProxy,
			@NotNull DotNetTypeProxy typeProxy)
	{
		boolean result = processFieldOrProperty(typeProxy, null, CommonProcessors.<DotNetFieldOrPropertyProxy>alwaysFalse());
		if(result)
		{
			return;
		}
		list.add(new DotNetThisAsObjectValueNode(debuggerContext, frameProxy, typeProxy, (DotNetObjectValueProxy) null));
	}

	@NotNull
	private final DotNetTypeProxy myType;
	private final Getter<DotNetObjectValueProxy> myObjectValueMirrorGetter;

	public DotNetThisAsObjectValueNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetStackFrameProxy frameProxy,
			@NotNull DotNetTypeProxy type,
			@Nullable final DotNetObjectValueProxy objectValueMirror)
	{
		this(debuggerContext, frameProxy, type, objectValueMirror == null ? null : new Getter<DotNetObjectValueProxy>()
		{
			@Nullable
			@Override
			public DotNetObjectValueProxy get()
			{
				return objectValueMirror;
			}
		});
	}

	public DotNetThisAsObjectValueNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetStackFrameProxy frameProxy,
			@NotNull DotNetTypeProxy type,
			@Nullable Getter<DotNetObjectValueProxy> objectValueMirrorGetter)
	{
		super(debuggerContext, objectValueMirrorGetter == null ? "static" : "this", frameProxy);
		myType = type;
		myObjectValueMirrorGetter = objectValueMirrorGetter;
	}

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		return null;
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		return myObjectValueMirrorGetter == null ? AllIcons.Nodes.Static : AllIcons.Debugger.Value;
	}

	@Nullable
	@Override
	public DotNetValueProxy getValueOfVariableImpl()
	{
		return myObjectValueMirrorGetter == null ? null : myObjectValueMirrorGetter.get();
	}

	@Override
	public void setValueForVariableImpl(@NotNull DotNetValueProxy value)
	{
	}

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		if(myObjectValueMirrorGetter == null)
		{
			xValueNode.setPresentation(getIconForVariable(), new XRegularValuePresentation("", null, ""), true);
		}
		else
		{
			super.computePresentation(xValueNode, xValuePlace);
		}
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		final XValueChildrenList childrenList = new XValueChildrenList();

		processFieldOrProperty(myType, myObjectValueMirrorGetter, new Processor<DotNetFieldOrPropertyProxy>()
		{
			@Override
			public boolean process(DotNetFieldOrPropertyProxy fieldOrPropertyMirror)
			{
				childrenList.add(new DotNetFieldOrPropertyValueNode(myDebugContext, fieldOrPropertyMirror, myFrameProxy, fieldOrPropertyMirror.isStatic() ? null : myObjectValueMirrorGetter.get()));
				return true;
			}
		});
		node.addChildren(childrenList, true);
	}

	private static boolean processFieldOrProperty(@NotNull DotNetTypeProxy proxy,
			@Nullable Getter<DotNetObjectValueProxy> objectValueMirrorGetter,
			@NotNull Processor<DotNetFieldOrPropertyProxy> processor)
	{
		DotNetFieldOrPropertyProxy[] fieldMirrors = DotNetDebuggerSearchUtil.getFieldAndProperties(proxy, true);
		for(DotNetFieldOrPropertyProxy fieldMirror : fieldMirrors)
		{
			if(!fieldMirror.isStatic() && objectValueMirrorGetter == null || fieldMirror.isStatic() && objectValueMirrorGetter != null)
			{
				continue;
			}

			if(fieldMirror instanceof DotNetPropertyProxy && ((DotNetPropertyProxy) fieldMirror).isArrayProperty())
			{
				continue;
			}

			if(DotNetDebuggerCompilerGenerateUtil.needSkipVariableByName(fieldMirror.getName()))
			{
				continue;
			}

			if(!processor.process(fieldMirror))
			{
				return false;
			}
		}
		return true;
	}

	@NotNull
	@Override
	public DotNetTypeProxy getTypeOfVariable()
	{
		return myType;
	}
}
