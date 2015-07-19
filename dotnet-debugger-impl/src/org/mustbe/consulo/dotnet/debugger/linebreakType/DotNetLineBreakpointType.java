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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerSourceLineResolver;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerSourceLineResolverEP;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerUtil;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachine;
import org.mustbe.consulo.dotnet.debugger.TypeMirrorUnloadedException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.xdebugger.breakpoints.SuspendPolicy;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import mono.debugger.Location;
import mono.debugger.LocationImpl;
import mono.debugger.MethodMirror;
import mono.debugger.TypeMirror;
import mono.debugger.UnloadedElementException;
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
	protected boolean createRequestImpl(@NotNull Project project,
			@NotNull DotNetVirtualMachine virtualMachine,
			@NotNull XLineBreakpoint breakpoint,
			@Nullable TypeMirror typeMirror) throws TypeMirrorUnloadedException
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

						virtualMachine.putRequest(breakpoint, breakpointRequest);
					}
					return true;
			}
		}
		finally
		{
			DotNetBreakpointUtil.updateBreakpointPresentation(project, resultPair.getFirst() == BreakpointResult.OK, breakpoint);
		}
	}

	@NotNull
	public Pair<BreakpointResult, Location> findLocationImpl(@NotNull final Project project,
			@NotNull final DotNetVirtualMachine virtualMachine,
			@NotNull final XLineBreakpoint<?> lineBreakpoint,
			@Nullable final TypeMirror typeMirror) throws TypeMirrorUnloadedException
	{
		final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(lineBreakpoint.getFileUrl());
		if(fileByUrl == null)
		{
			return INVALID;
		}

		final PsiFile file = ApplicationManager.getApplication().runReadAction(new Computable<PsiFile>()
		{
			@Override
			public PsiFile compute()
			{
				return PsiManager.getInstance(project).findFile(fileByUrl);
			}
		});

		if(file == null)
		{
			return INVALID;
		}

		final String vmQualifiedName = ApplicationManager.getApplication().runReadAction(new Computable<String>()
		{
			@Override
			public String compute()
			{
				PsiElement psiElement = DotNetDebuggerUtil.findPsiElement(file, lineBreakpoint.getLine());
				if(psiElement == null)
				{
					return null;
				}
				DotNetDebuggerSourceLineResolver resolver = DotNetDebuggerSourceLineResolverEP.INSTANCE.forLanguage(file.getLanguage());
				assert resolver != null;
				return resolver.resolveParentVmQName(psiElement);
			}
		});

		if(vmQualifiedName == null)
		{
			return INVALID;
		}

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
		int index = 0;
		try
		{
			targetMirror = null;
			index = -1;
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
		}
		catch(UnloadedElementException e)
		{
			throw new TypeMirrorUnloadedException(mirror, e);
		}

		if(targetMirror == null)
		{
			return INVALID;
		}

		return Pair.<BreakpointResult, Location>create(BreakpointResult.OK, new LocationImpl(virtualMachine.getDelegate(), targetMirror, index));
	}
}
