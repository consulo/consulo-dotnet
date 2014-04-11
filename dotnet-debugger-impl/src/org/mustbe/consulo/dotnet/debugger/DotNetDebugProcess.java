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

package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetLineBreakpointType;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.run.DotNetRunProfileState;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerBundle;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;
import mono.debugger.JDWPException;
import mono.debugger.Location;
import mono.debugger.LocationImpl;
import mono.debugger.MethodMirror;
import mono.debugger.TypeMirror;
import mono.debugger.VirtualMachine;
import mono.debugger.VirtualMachineImpl;
import mono.debugger.protocol.Method_GetDebugInfo;
import mono.debugger.request.BreakpointRequest;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetDebugProcess extends XDebugProcess
{
	private final ExecutionResult myResult;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private DotNetDebugThread myDebugThread;

	public DotNetDebugProcess(XDebugSession session, ExecutionResult result, DotNetRunProfileState state)
	{
		super(session);
		myResult = result;
		session.setPauseActionSupported(true);
		myDebugConnectionInfo = state.getDebugConnectionInfo();
		myDebugThread = new DotNetDebugThread(session, myDebugConnectionInfo);
		myDebugThread.start();
	}

	@Nullable
	@Override
	protected ProcessHandler doGetProcessHandler()
	{
		return myResult.getProcessHandler();
	}

	@NotNull
	@Override
	public ExecutionConsole createConsole()
	{
		return myResult.getExecutionConsole();
	}

	@Override
	public XBreakpointHandler<?>[] getBreakpointHandlers()
	{
		return new XBreakpointHandler[]{
				new XBreakpointHandler<XLineBreakpoint<XBreakpointProperties>>(DotNetLineBreakpointType.class)
				{
					@Override
					public void registerBreakpoint(@NotNull final XLineBreakpoint<XBreakpointProperties> breakpoint)
					{
						myDebugThread.addCommand(new Processor<VirtualMachine>()
						{
							@Override
							public boolean process(VirtualMachine virtualMachine)
							{
								Location location = findLocation(virtualMachine, breakpoint);
								if(location == null)
								{
									return false;
								}
								BreakpointRequest breakpointRequest = virtualMachine.eventRequestManager().createBreakpointRequest(location);
								breakpointRequest.enable();
								return false;
							}
						});
					}

					@Override
					public void unregisterBreakpoint(@NotNull XLineBreakpoint<XBreakpointProperties> breakpoint, boolean b)
					{

					}

					public Location findLocation(final VirtualMachine virtualMachine, final XLineBreakpoint<?> lineBreakpoint)
					{
						return ApplicationManager.getApplication().runReadAction(new Computable<Location>()
						{
							@Override
							public Location compute()
							{
								return findLocationImpl(virtualMachine, lineBreakpoint);
							}
						});
					}

					public Location findLocationImpl(VirtualMachine virtualMachine, XLineBreakpoint<?> lineBreakpoint)
					{
						VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(lineBreakpoint.getFileUrl());
						if(fileByUrl == null)
						{
							return null;
						}

						PsiElement psiElement = findPsiElement(getSession().getProject(), fileByUrl, lineBreakpoint.getLine());
						if(psiElement == null)
						{
							return null;
						}
						DotNetMethodDeclaration methodDeclaration = PsiTreeUtil.getParentOfType(psiElement, DotNetMethodDeclaration.class, false);
						if(methodDeclaration == null)
						{
							return null;
						}
						PsiElement codeBlock = methodDeclaration.getCodeBlock();
						if(codeBlock == null)
						{
							return null;
						}
						if(!PsiTreeUtil.isAncestor(codeBlock, psiElement, false))
						{
							return null;
						}
						PsiElement parent = methodDeclaration.getParent();
						if(!(parent instanceof DotNetTypeDeclaration))
						{
							return null;
						}
						TypeMirror[] types = virtualMachine.findTypes(((DotNetTypeDeclaration) parent).getPresentableQName(), false);
						if(types.length == 0)
						{
							return null;
						}

						MethodMirror targetMirror = null;
						TypeMirror mirror = types[0];
						for(MethodMirror methodMirror : mirror.methods())
						{
							if(isValidMethodMirror(methodDeclaration, methodMirror))
							{
								targetMirror = methodMirror;
								break;
							}
						}

						if(targetMirror == null)
						{
							return null;
						}

						try
						{
							Method_GetDebugInfo process = Method_GetDebugInfo.process((VirtualMachineImpl) virtualMachine, targetMirror);

							int index = -1;
							for(Method_GetDebugInfo.Entry entry : process.entries)
							{
								if(entry.line == lineBreakpoint.getLine())
								{
									index = entry.offset;
									break;
								}
							}
							if(index == -1)
							{
								return null;
							}
							return new LocationImpl(virtualMachine, targetMirror, index);
						}
						catch(JDWPException e)
						{
							e.printStackTrace();
						}
						return null;
					}

					private boolean isValidMethodMirror(DotNetMethodDeclaration methodDeclaration, MethodMirror methodMirror)
					{
						if(methodMirror.genericParameterCount() != methodDeclaration.getGenericParametersCount())
						{
							return false;
						}
						DotNetParameter[] parameters = methodDeclaration.getParameters();
						if(parameters.length != methodMirror.parameters().length)
						{
							return false;
						}
						//TODO [VISTALL]
						return Comparing.equal(methodDeclaration.getName(), methodMirror.name());
					}

					@Nullable
					protected PsiElement findPsiElement(@NotNull final Project project, @NotNull final VirtualFile file, final int line)
					{

						final Document doc = FileDocumentManager.getInstance().getDocument(file);
						final PsiFile psi = doc == null ? null : PsiDocumentManager.getInstance(project).getPsiFile(doc);
						if(psi == null)
						{
							return null;
						}

						int offset = doc.getLineStartOffset(line);
						int endOffset = doc.getLineEndOffset(line);
						for(int i = offset + 1; i < endOffset; i++)
						{
							PsiElement el = psi.findElementAt(i);
							if(el != null && !(el instanceof PsiWhiteSpace))
							{
								return el;
							}
						}

						return null;
					}
				}
		};
	}

	@NotNull
	@Override
	public XDebuggerEditorsProvider getEditorsProvider()
	{
		return new XDebuggerEditorsProviderBase()
		{
			@Override
			protected PsiFile createExpressionCodeFragment(
					@NotNull Project project, @NotNull String s, @Nullable PsiElement element, boolean b)
			{
				return null;
			}

			@NotNull
			@Override
			public FileType getFileType()
			{
				return PlainTextFileType.INSTANCE;
			}
		};
	}

	@Override
	public void startPausing()
	{
		myDebugThread.addCommand(new Processor<VirtualMachine>()
		{
			@Override
			public boolean process(VirtualMachine virtualMachine)
			{
				virtualMachine.suspend();
				getSession().positionReached(new DotNetSuspendContext(virtualMachine, getSession().getProject(), null));
				return false;
			}
		});
	}

	@Override
	public void resume()
	{
		myDebugThread.addCommand(new Processor<VirtualMachine>()
		{
			@Override
			public boolean process(VirtualMachine virtualMachine)
			{
				virtualMachine.resume();
				return false;
			}
		});
	}

	@Override
	public String getCurrentStateMessage()
	{
		if(myDebugThread.isConnected())
		{
			return "Connected to " + myDebugConnectionInfo.getHost() + ":" + myDebugConnectionInfo.getPort();
		}
		return XDebuggerBundle.message("debugger.state.message.disconnected");
	}

	@Override
	public void startStepOver()
	{

	}

	@Override
	public void startStepInto()
	{

	}

	@Override
	public void startStepOut()
	{

	}

	@Override
	public void stop()
	{
		myDebugThread.setStop();
	}

	@Override
	public void runToPosition(@NotNull XSourcePosition xSourcePosition)
	{

	}
}
