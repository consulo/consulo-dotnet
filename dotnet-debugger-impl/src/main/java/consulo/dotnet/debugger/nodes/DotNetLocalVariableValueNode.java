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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.ui.RequiredUIAccess;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetCodeBlockOwner;
import consulo.dotnet.psi.DotNetVariable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.util.PsiScopesUtilCore;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XNavigatable;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 12.04.14
 */
public class DotNetLocalVariableValueNode extends DotNetAbstractVariableValueNode
{
	private final DotNetLocalVariableProxy myLocal;

	public DotNetLocalVariableValueNode(DotNetDebugContext debuggerContext, DotNetLocalVariableProxy local, DotNetStackFrameProxy frameProxy)
	{
		super(debuggerContext, local.getName(), frameProxy);
		myLocal = local;
	}

	@Override
	public boolean canNavigateToSource()
	{
		return true;
	}

	@Override
	@RequiredUIAccess
	public void computeSourcePosition(@Nonnull XNavigatable navigatable)
	{
		computeSourcePosition(navigatable, getName(), myDebugContext, myFrameProxy);
	}

	@RequiredReadAction
	public static void computeSourcePosition(@Nonnull XNavigatable navigatable, String name, DotNetDebugContext debugContext, DotNetStackFrameProxy proxy)
	{
		if(StringUtil.isEmpty(name))
		{
			return;
		}
		PsiElement psiElement = DotNetSourcePositionUtil.resolveTargetPsiElement(debugContext, proxy);
		if(psiElement == null)
		{
			return;
		}

		DotNetCodeBlockOwner codeBlockOwner = PsiTreeUtil.getParentOfType(psiElement, DotNetCodeBlockOwner.class);
		if(codeBlockOwner == null)
		{
			return;
		}

		final Ref<DotNetVariable> elementRef = Ref.create();
		PsiScopesUtilCore.treeWalkUp(new BaseScopeProcessor()
		{
			@Override
			public boolean execute(@Nonnull PsiElement element, ResolveState state)
			{
				if(element instanceof DotNetVariable && name.equals(((DotNetVariable) element).getName()))
				{
					elementRef.set((DotNetVariable) element);
					return false;
				}
				return true;
			}
		}, psiElement, codeBlockOwner);

		DotNetVariable element = elementRef.get();
		if(element == null)
		{
			return;
		}
		PsiFile containingFile = element.getContainingFile();
		if(containingFile == null)
		{
			return;
		}
		navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(containingFile.getVirtualFile(), element.getTextOffset()));
	}

	@Nullable
	@Override
	public DotNetTypeProxy getTypeOfVariableImpl()
	{
		return myLocal.getType();
	}

	@Nullable
	@Override
	public DotNetValueProxy getValueOfVariableImpl()
	{
		return myFrameProxy.getLocalValue(myLocal);
	}

	@Override
	public void setValueForVariableImpl(@Nonnull DotNetValueProxy value)
	{
		myFrameProxy.setLocalValue(myLocal, value);
	}
}
