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

package org.mustbe.consulo.dotnet.debugger.nodes;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import mono.debugger.StackFrameMirror;

/**
 * @author VISTALL
 * @since 16.04.2015
 */
public class DotNetSourcePositionUtil
{
	@Nullable
	@RequiredReadAction
	public static PsiElement resolveTargetPsiElement(DotNetDebugContext context, StackFrameMirror myFrame)
	{
		String sourcePath = myFrame.location().sourcePath();
		if(sourcePath == null)
		{
			return null;
		}
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(sourcePath);
		if(fileByPath == null)
		{
			return null;
		}
		return DotNetDebuggerUtil.findPsiElement(context.getProject(), fileByPath, myFrame.location().lineNumber() - 1);
	}
}
