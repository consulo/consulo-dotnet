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

package consulo.dotnet.microsoft.debugger;

import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XStackFrame;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFramesRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftStackFrame extends XStackFrame
{
	private int myIndex;
	private GetFramesRequestResult.FrameInfo myFrame;

	public MicrosoftStackFrame(int index, GetFramesRequestResult.FrameInfo frame)
	{
		myIndex = index;
		myFrame = frame;
	}

	@Nullable
	@Override
	public XSourcePosition getSourcePosition()
	{
		String fileName = myFrame.Position.FilePath;
		if(fileName == null)
		{
			return null;
		}
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(fileName);
		if(fileByPath == null)
		{
			return null;
		}

		//XLineBreakpoint<?> breakpoint = myDebuggerContext.getBreakpoint();
		XSourcePosition originalPosition = XDebuggerUtil.getInstance().createPosition(fileByPath, myFrame.Position.Line);
		if(originalPosition == null)
		{
			return null;
		}
		/*if(breakpoint != null)
		{
			DotNetLineBreakpointProperties properties = (DotNetLineBreakpointProperties) breakpoint.getProperties();
			final Integer executableChildrenAtLineIndex = properties.getExecutableChildrenAtLineIndex();
			if(executableChildrenAtLineIndex != null && executableChildrenAtLineIndex > -1)
			{
				final MethodMirror method = myFrameProxy.location().method();
				if(DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo(method) != null)
				{
					PsiElement executableElement = ApplicationManager.getApplication().runReadAction(new Computable<PsiElement>()
					{
						@Override
						public PsiElement compute()
						{
							return DotNetLineBreakpointType.findExecutableElementFromDebugInfo(myDebuggerContext.getProject(), method.debugInfo(), executableChildrenAtLineIndex);
						}
					});

					if(executableElement != null)
					{
						return new DotNetSourcePositionImpl(originalPosition, executableElement);
					}
				}
			}
		}  */

		return originalPosition;
	}
	@Override
	public void customizePresentation(ColoredTextContainer component)
	{
		component.append(myFrame.Method, SimpleTextAttributes.REGULAR_ATTRIBUTES);
		component.setIcon(AllIcons.Debugger.StackFrame);
	}
}
