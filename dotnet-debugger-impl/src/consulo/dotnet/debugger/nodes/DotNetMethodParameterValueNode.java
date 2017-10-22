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
import consulo.annotations.RequiredDispatchThread;
import consulo.dotnet.psi.DotNetCodeBlockOwner;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.psi.DotNetParameterListOwner;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XNavigatable;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetMethodParameterValueNode extends DotNetAbstractVariableValueNode
{
	private final DotNetMethodParameterProxy myParameter;
	private final DotNetStackFrameProxy myFrame;

	public DotNetMethodParameterValueNode(DotNetDebugContext debuggerContext, DotNetMethodParameterProxy parameter, DotNetStackFrameProxy frame)
	{
		super(debuggerContext, parameter.getName(), frame);
		myParameter = parameter;
		myFrame = frame;
	}

	@NotNull
	@Override
	public Icon getIconForVariable(@Nullable DotNetValueProxy alreadyCalledValue)
	{
		return AllIcons.Nodes.Parameter;
	}

	@Override
	@RequiredDispatchThread
	public void computeSourcePosition(@NotNull XNavigatable navigatable)
	{
		PsiElement psiElement = DotNetSourcePositionUtil.resolveTargetPsiElement(myDebugContext, myFrame);
		if(psiElement == null)
		{
			return;
		}
		DotNetCodeBlockOwner codeBlockOwner = PsiTreeUtil.getParentOfType(psiElement, DotNetCodeBlockOwner.class);
		if(codeBlockOwner == null)
		{
			return;
		}

		// search parameterlist owner
		DotNetParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(codeBlockOwner, DotNetParameterListOwner.class, false);
		if(parameterListOwner == null)
		{
			return;
		}

		DotNetParameter[] psiParameters = parameterListOwner.getParameters();

		DotNetSourceLocation sourceLocation = myFrame.getSourceLocation();
		if(sourceLocation == null)
		{
			return;
		}

		DotNetParameter parameter = psiParameters[myParameter.getIndex()];
		PsiElement nameIdentifier = parameter.getNameIdentifier();
		if(nameIdentifier == null)
		{
			return;
		}
		navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(parameter.getContainingFile().getVirtualFile(), nameIdentifier.getTextOffset()));
	}

	@Nullable
	@Override
	public DotNetTypeProxy getTypeOfVariableImpl()
	{
		return myParameter.getType();
	}

	@Nullable
	@Override
	public DotNetValueProxy getValueOfVariableImpl()
	{
		return myFrame.getParameterValue(myParameter);
	}

	@Override
	public void setValueForVariableImpl(@NotNull DotNetValueProxy value)
	{
		myFrame.setParameterValue(myParameter, value);
	}
}
