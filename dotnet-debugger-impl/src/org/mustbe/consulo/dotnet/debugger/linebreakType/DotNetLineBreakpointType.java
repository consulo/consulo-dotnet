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

package org.mustbe.consulo.dotnet.debugger.linebreakType;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerUtil;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachine;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import org.mustbe.consulo.dotnet.psi.DotNetCodeBlockOwner;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.SuspendPolicy;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import lombok.val;
import mono.debugger.Location;
import mono.debugger.LocationImpl;
import mono.debugger.MethodMirror;
import mono.debugger.TypeMirror;
import mono.debugger.protocol.Method_GetDebugInfo;
import mono.debugger.request.BreakpointRequest;
import mono.debugger.request.EventRequestManager;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetLineBreakpointType extends DotNetAbstractBreakpointType
{
	enum BreakpointResult
	{
		INVALID,
		WRONG_TYPE,
		OK
	}

	private static final Pair<BreakpointResult, Location> INVALID = Pair.create(BreakpointResult.INVALID, null);
	private static final Pair<BreakpointResult, Location> WRONG_TYPE = Pair.create(BreakpointResult.WRONG_TYPE, null);

	@NotNull
	public static DotNetLineBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtension(DotNetLineBreakpointType.class);
	}

	public DotNetLineBreakpointType()
	{
		super("dotnet-linebreapoint", ".NET Line Breakpoint", null);
	}

	@Override
	@RequiredReadAction
	protected boolean createRequestImpl(@NotNull Project project,
			@NotNull DotNetVirtualMachine virtualMachine,
			@NotNull XLineBreakpoint breakpoint,
			@Nullable TypeMirror typeMirror)
	{
		Pair<BreakpointResult, Location> resultPair = findLocationImpl(project, virtualMachine, breakpoint, typeMirror);
		try
		{
			switch(resultPair.getFirst())
			{
				default:
					return false;
				case OK:
					if(breakpoint.getSuspendPolicy() != SuspendPolicy.NONE)
					{
						EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
						BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(resultPair.getSecond());
						breakpointRequest.enable();

						breakpoint.putUserData(DotNetAbstractBreakpointType.EVENT_REQUEST, breakpointRequest);
					}
					return true;
			}
		}
		finally
		{
			updateBreakpointPresentation(project, resultPair.getFirst(), breakpoint);
		}
	}

	private void updateBreakpointPresentation(@NotNull Project project,
			@NotNull BreakpointResult breakpointResult,
			@NotNull XLineBreakpoint breakpoint)
	{
		XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();

		Icon icon = null;
		SuspendPolicy suspendPolicy = breakpoint.getSuspendPolicy();
		if(breakpoint.isTemporary())
		{
			if(suspendPolicy == SuspendPolicy.NONE)
			{
				icon = AllIcons.Debugger.Db_temporary_breakpoint;
			}
			else
			{
				icon = AllIcons.Debugger.Db_muted_temporary_breakpoint;
			}
		}
		else
		{
			switch(breakpointResult)
			{
				default:
					if(suspendPolicy == SuspendPolicy.NONE)
					{
						icon = AllIcons.Debugger.Db_muted_invalid_breakpoint;
					}
					else
					{
						icon = AllIcons.Debugger.Db_invalid_breakpoint;
					}
					break;
				case OK:
					if(suspendPolicy == SuspendPolicy.NONE)
					{
						icon = AllIcons.Debugger.Db_muted_verified_breakpoint;
					}
					else
					{
						icon = AllIcons.Debugger.Db_verified_breakpoint;
					}
					break;
			}
		}
		breakpointManager.updateBreakpointPresentation(breakpoint, icon, null);
	}

	@NotNull
	@RequiredReadAction
	public Pair<BreakpointResult, Location> findLocationImpl(@NotNull Project project,
			@NotNull DotNetVirtualMachine virtualMachine,
			@NotNull XLineBreakpoint<?> lineBreakpoint,
			@Nullable TypeMirror typeMirror)
	{
		VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(lineBreakpoint.getFileUrl());
		if(fileByUrl == null)
		{
			return INVALID;
		}

		PsiElement psiElement = DotNetDebuggerUtil.findPsiElement(project, fileByUrl, lineBreakpoint.getLine());
		if(psiElement == null)
		{
			return INVALID;
		}
		DotNetCodeBlockOwner codeBlockOwner = PsiTreeUtil.getParentOfType(psiElement, DotNetCodeBlockOwner.class, false);
		if(codeBlockOwner == null)
		{
			return INVALID;
		}
		PsiElement codeBlock = codeBlockOwner.getCodeBlock();
		if(codeBlock == null)
		{
			return INVALID;
		}
		if(!PsiTreeUtil.isAncestor(codeBlock, psiElement, false))
		{
			return INVALID;
		}
		DotNetTypeDeclaration typeDeclaration = PsiTreeUtil.getParentOfType(codeBlockOwner, DotNetTypeDeclaration.class);
		if(typeDeclaration == null)
		{
			return INVALID;
		}

		val vmQualifiedName = DotNetVirtualMachineUtil.toVMQualifiedName(typeDeclaration);
		if(typeMirror != null && !Comparing.equal(vmQualifiedName, typeMirror.qualifiedName()))
		{
			return WRONG_TYPE;
		}

		TypeMirror mirror = typeMirror == null ? virtualMachine.findTypeMirror(fileByUrl, vmQualifiedName) : typeMirror;

		if(mirror == null)
		{
			return INVALID;
		}

		MethodMirror targetMirror = null;
		int index = -1;
		highLoop:
		for(MethodMirror methodMirror : mirror.methods())
		{
			for(Method_GetDebugInfo.Entry entry : methodMirror.debugInfo())
			{
				if(entry.line == (lineBreakpoint.getLine() + 1))
				{
					targetMirror = methodMirror;
					index = entry.offset;
					break highLoop;
				}
			}
		}

		if(targetMirror == null)
		{
			return INVALID;
		}

		return Pair.<BreakpointResult, Location>create(BreakpointResult.OK, new LocationImpl(virtualMachine.getDelegate(), targetMirror, index));
	}
}
