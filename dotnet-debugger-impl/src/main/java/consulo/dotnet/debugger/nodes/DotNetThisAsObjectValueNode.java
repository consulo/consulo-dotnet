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

import consulo.application.AllIcons;
import consulo.application.util.function.CommonProcessors;
import consulo.application.util.function.Processor;
import consulo.debugger.frame.*;
import consulo.debugger.frame.presentation.XRegularValuePresentation;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerSearchUtil;
import consulo.dotnet.debugger.proxy.*;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.ui.image.Image;
import consulo.util.lang.ref.SimpleReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetThisAsObjectValueNode extends DotNetAbstractVariableValueNode
{
	public static void addStaticNode(@Nonnull XValueChildrenList list,
									 @Nonnull DotNetDebugContext debuggerContext,
									 @Nonnull DotNetStackFrameProxy frameProxy,
									 @Nonnull DotNetTypeProxy typeProxy)
	{
		boolean result = processFieldOrProperty(typeProxy, null, CommonProcessors.<DotNetFieldOrPropertyProxy>alwaysFalse());
		if(result)
		{
			return;
		}
		list.add(new DotNetThisAsObjectValueNode(debuggerContext, frameProxy, typeProxy, (DotNetObjectValueProxy) null));
	}

	@Nonnull
	private final DotNetTypeProxy myType;
	private final Supplier<DotNetObjectValueProxy> myObjectValueMirrorGetter;

	public DotNetThisAsObjectValueNode(@Nonnull DotNetDebugContext debuggerContext,
									   @Nonnull DotNetStackFrameProxy frameProxy,
									   @Nonnull DotNetTypeProxy type,
									   @Nullable final DotNetObjectValueProxy objectValueMirror)
	{
		this(debuggerContext, frameProxy, type, objectValueMirror == null ? null : () -> objectValueMirror);
	}

	public DotNetThisAsObjectValueNode(@Nonnull DotNetDebugContext debuggerContext,
									   @Nonnull DotNetStackFrameProxy frameProxy,
									   @Nonnull DotNetTypeProxy type,
									   @Nullable Supplier<DotNetObjectValueProxy> objectValueMirrorGetter)
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

	@Nonnull
	@Override
	public Image getIconForVariable(@Nullable SimpleReference<DotNetValueProxy> alreadyCalledValue)
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
	public void setValueForVariableImpl(@Nonnull DotNetValueProxy value)
	{
	}

	@Override
	protected void computePresentationImpl(@Nonnull XValueNode node, @Nonnull XValuePlace xValuePlace)
	{
		if(myObjectValueMirrorGetter == null)
		{
			node.setPresentation(getIconForVariable(null), new XRegularValuePresentation("", null, ""), true);
		}
		else
		{
			super.computePresentationImpl(node, xValuePlace);
		}
	}

	@Override
	public void computeChildren(@Nonnull XCompositeNode node)
	{
		final XValueChildrenList childrenList = new XValueChildrenList();

		final Set<String> visited = new HashSet<String>();
		processFieldOrProperty(myType, myObjectValueMirrorGetter, new Processor<DotNetFieldOrPropertyProxy>()
		{
			@Override
			public boolean process(DotNetFieldOrPropertyProxy fieldOrPropertyProxy)
			{
				if(!visited.add(fieldOrPropertyProxy.getName()))
				{
					return true;
				}
				childrenList.add(new DotNetFieldOrPropertyValueNode(myDebugContext, fieldOrPropertyProxy, myFrameProxy, fieldOrPropertyProxy.isStatic() ? null : myObjectValueMirrorGetter.get()));
				return true;
			}
		});
		node.addChildren(childrenList, true);
	}

	private static boolean processFieldOrProperty(@Nonnull DotNetTypeProxy proxy,
												  @Nullable Supplier<DotNetObjectValueProxy> objectValueMirrorGetter,
												  @Nonnull Processor<DotNetFieldOrPropertyProxy> processor)
	{
		DotNetFieldOrPropertyProxy[] fieldMirrors = DotNetDebuggerSearchUtil.getFieldAndProperties(proxy, true);
		for(DotNetFieldOrPropertyProxy fieldMirror : fieldMirrors)
		{
			if(!fieldMirror.isStatic() && objectValueMirrorGetter == null || fieldMirror.isStatic() && objectValueMirrorGetter != null)
			{
				continue;
			}

			if(isHiddenPropertyOrField(fieldMirror))
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

	public static boolean isHiddenPropertyOrField(DotNetFieldOrPropertyProxy proxy)
	{
		if(proxy instanceof DotNetPropertyProxy)
		{
			if(((DotNetPropertyProxy) proxy).isArrayProperty())
			{
				return true;
			}

			DotNetMethodProxy getMethod = ((DotNetPropertyProxy) proxy).getGetMethod();
			// if get accessor is abstract - it will generate dummy impl with backend field
			if(getMethod == null || getMethod.isAnnotatedBy(DotNetTypes.System.Runtime.CompilerServices.CompilerGeneratedAttribute))
			{
				return true;
			}
		}

		if(DotNetDebuggerCompilerGenerateUtil.needSkipVariableByName(proxy.getName()))
		{
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public DotNetTypeProxy getTypeOfVariableImpl()
	{
		return myType;
	}
}
