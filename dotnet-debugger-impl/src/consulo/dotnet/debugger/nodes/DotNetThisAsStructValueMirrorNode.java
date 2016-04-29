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

package consulo.dotnet.debugger.nodes;

import java.util.Map;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 05.01.16
 */
public class DotNetThisAsStructValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	@NotNull
	private final DotNetTypeProxy myTypeMirror;
	private final DotNetStructValueProxy myValue;

	public DotNetThisAsStructValueMirrorNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetThreadProxy threadMirror,
			@NotNull DotNetTypeProxy typeMirror,
			@NotNull DotNetStructValueProxy value)
	{
		super(debuggerContext, "this", threadMirror);
		myTypeMirror = typeMirror;
		myValue = value;
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
		return AllIcons.Debugger.Value;
	}

	@Nullable
	@Override
	public DotNetValueProxy getValueOfVariableImpl()
	{
		return myValue;
	}

	@Override
	public void setValueForVariableImpl(@NotNull DotNetValueProxy value)
	{
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		final XValueChildrenList childrenList = new XValueChildrenList();

		Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> map = myValue.getValues();
		for(Map.Entry<DotNetFieldOrPropertyProxy, DotNetValueProxy> entry : map.entrySet())
		{
			DotNetFieldOrPropertyProxy key = entry.getKey();
			DotNetValueProxy value = entry.getValue();

			DotNetStructValueInfo valueInfo = new DotNetStructValueInfo(myValue, this, key, value);

			childrenList.add(new DotNetFieldOrPropertyMirrorNode(myDebugContext, key, myThreadProxy, null, valueInfo));
		}

		node.addChildren(childrenList, true);
	}

	@NotNull
	@Override
	public DotNetTypeProxy getTypeOfVariable()
	{
		return myTypeMirror;
	}
}
