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

package consulo.dotnet.mono.debugger.breakpoint;

import gnu.trove.TIntHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerSourceLineResolver;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerSourceLineResolverEP;
import org.mustbe.consulo.dotnet.debugger.TypeMirrorUnloadedException;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetLineBreakpointType;
import org.mustbe.consulo.dotnet.debugger.linebreakType.properties.DotNetLineBreakpointProperties;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.SuspendPolicy;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import consulo.dotnet.debugger.DotNetDebuggerUtil;
import consulo.dotnet.debugger.breakpoint.DotNetBreakpointUtil;
import consulo.dotnet.mono.debugger.proxy.MonoVirtualMachineProxy;
import mono.debugger.Location;
import mono.debugger.LocationImpl;
import mono.debugger.MethodMirror;
import mono.debugger.TypeMirror;
import mono.debugger.UnloadedElementException;
import mono.debugger.VMDisconnectedException;
import mono.debugger.protocol.Method_GetDebugInfo;
import mono.debugger.request.BreakpointRequest;
import mono.debugger.request.EventRequestManager;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoBreakpointUtil
{
	public static class FindLocationResult
	{
		public static final FindLocationResult WRONG_TARGET = new FindLocationResult();

		public static final FindLocationResult NO_LOCATIONS = new FindLocationResult();

		private Collection<Location> myLocations = Collections.emptyList();

		@NotNull
		public static FindLocationResult success(@NotNull Collection<Location> locations)
		{
			FindLocationResult findLocationResult = new FindLocationResult();
			findLocationResult.myLocations = locations;
			return findLocationResult;
		}

		@NotNull
		public Collection<Location> getLocations()
		{
			return myLocations;
		}
	}

	public static void createRequest(@NotNull XDebugSession debugSession, @NotNull MonoVirtualMachineProxy virtualMachine, @NotNull XLineBreakpoint breakpoint, @Nullable TypeMirror typeMirror)
	{
		try
		{
			createRequestImpl(debugSession.getProject(), virtualMachine, breakpoint, typeMirror);
		}
		catch(VMDisconnectedException ignored)
		{
		}
		catch(TypeMirrorUnloadedException e)
		{
			debugSession.getConsoleView().print(e.getFullName(), ConsoleViewContentType.ERROR_OUTPUT);
			debugSession.getConsoleView().print("You can fix this error - restart debug. If you can repeat this error, " + "please report it here 'https://github.com/consulo/consulo-dotnet/issues'",
					ConsoleViewContentType.ERROR_OUTPUT);
		}
	}

	public static void createRequestImpl(@NotNull Project project,
			@NotNull MonoVirtualMachineProxy virtualMachine,
			@NotNull XLineBreakpoint breakpoint,
			@Nullable TypeMirror typeMirror) throws TypeMirrorUnloadedException
	{
		FindLocationResult result = findLocationsImpl(project, virtualMachine, breakpoint, typeMirror);
		if(result == FindLocationResult.WRONG_TARGET)
		{
			return;
		}

		virtualMachine.stopBreakpointRequests(breakpoint);

		Collection<Location> locations = result.getLocations();
		if(breakpoint.getSuspendPolicy() != SuspendPolicy.NONE)
		{
			for(Location location : locations)
			{
				EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
				BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
				breakpointRequest.enable();

				virtualMachine.putRequest(breakpoint, breakpointRequest);
			}
		}

		DotNetBreakpointUtil.updateBreakpointPresentation(project, !locations.isEmpty(), breakpoint);
	}

	@NotNull
	public static FindLocationResult findLocationsImpl(@NotNull final Project project,
			@NotNull final MonoVirtualMachineProxy virtualMachine,
			@NotNull final XLineBreakpoint<?> lineBreakpoint,
			@Nullable final TypeMirror typeMirror) throws TypeMirrorUnloadedException
	{
		final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(lineBreakpoint.getFileUrl());
		if(fileByUrl == null)
		{
			return FindLocationResult.WRONG_TARGET;
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
			return FindLocationResult.WRONG_TARGET;
		}

		final Ref<DotNetDebuggerSourceLineResolver> resolverRef = Ref.create();

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
				resolverRef.set(resolver);
				return resolver.resolveParentVmQName(psiElement);
			}
		});

		if(vmQualifiedName == null)
		{
			return FindLocationResult.WRONG_TARGET;
		}

		if(typeMirror != null && !Comparing.equal(vmQualifiedName, org.mustbe.consulo.dotnet.debugger.DotNetDebuggerUtil.getVmQName(typeMirror)))
		{
			return FindLocationResult.WRONG_TARGET;
		}

		TypeMirror mirror = typeMirror == null ? virtualMachine.findTypeMirror(project, fileByUrl, vmQualifiedName) : typeMirror;

		if(mirror == null)

		{
			return FindLocationResult.NO_LOCATIONS;
		}

		Map<MethodMirror, Location> methods = new LinkedHashMap<MethodMirror, Location>();

		try
		{
			DotNetLineBreakpointProperties properties = (DotNetLineBreakpointProperties) lineBreakpoint.getProperties();

			final Integer executableChildrenAtLineIndex = properties.getExecutableChildrenAtLineIndex();
			for(MethodMirror methodMirror : mirror.methods())
			{
				if(methods.containsKey(methodMirror))
				{
					continue;
				}

				if(executableChildrenAtLineIndex != null)
				{
					Couple<String> lambdaInfo = DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo(methodMirror);
					if(executableChildrenAtLineIndex == -1 && lambdaInfo != null)
					{
						// is lambda - we cant enter it with -1
						continue;
					}

					if(executableChildrenAtLineIndex != -1)
					{
						if(lambdaInfo == null)
						{
							continue;
						}

						final Method_GetDebugInfo.Entry[] entries = methodMirror.debugInfo();
						if(entries.length == 0)
						{
							continue;
						}

						boolean acceptable = ApplicationManager.getApplication().runReadAction(new Computable<Boolean>()
						{
							@Override
							public Boolean compute()
							{
								return findExecutableElementFromDebugInfo(project, entries, executableChildrenAtLineIndex) != null;
							}
						});

						if(!acceptable)
						{
							continue;
						}
					}
				}

				collectLocations(virtualMachine, lineBreakpoint, methods, methodMirror);
			}

			TypeMirror[] nestedTypeMirrors = mirror.nestedTypes();
			for(TypeMirror nestedTypeMirror : nestedTypeMirrors)
			{
				if(DotNetDebuggerCompilerGenerateUtil.isYieldOrAsyncNestedType(nestedTypeMirror))
				{
					// we interest only MoveNext method
					MethodMirror moveNext = nestedTypeMirror.findMethodByName("MoveNext", false);
					if(moveNext != null)
					{
						collectLocations(virtualMachine, lineBreakpoint, methods, moveNext);
					}
				}
				else if(DotNetDebuggerCompilerGenerateUtil.isAsyncLambdaWrapper(nestedTypeMirror))
				{
					TypeMirror[] typeMirrors = nestedTypeMirror.nestedTypes();
					if(typeMirrors.length > 0)
					{
						MethodMirror moveNext = typeMirrors[0].findMethodByName("MoveNext", false);

						if(moveNext != null)
						{
							collectLocations(virtualMachine, lineBreakpoint, methods, moveNext);
						}
					}
				}
			}
		}
		catch(UnloadedElementException e)
		{
			throw new TypeMirrorUnloadedException(mirror, e);
		}

		return methods.isEmpty() ? FindLocationResult.NO_LOCATIONS : FindLocationResult.success(methods.values());
	}

	@RequiredReadAction
	public static PsiElement findExecutableElementFromDebugInfo(final Project project, Method_GetDebugInfo.Entry[] entries, @NotNull Integer index)
	{
		Method_GetDebugInfo.Entry entry = entries[0];
		Method_GetDebugInfo.SourceFile sourceFile = entry.sourceFile;
		if(sourceFile == null)
		{
			return null;
		}
		final VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(sourceFile.name);
		if(fileByPath == null)
		{
			return null;
		}

		PsiFile otherPsiFile = PsiManager.getInstance(project).findFile(fileByPath);
		if(otherPsiFile == null)
		{
			return null;
		}

		PsiElement psiElement = DotNetDebuggerUtil.findPsiElement(otherPsiFile, entry.line - 1, entry.column);
		if(psiElement == null)
		{
			return null;
		}

		Set<PsiElement> psiElements = DotNetLineBreakpointType.collectExecutableChildren(otherPsiFile, entry.line - 1);
		if(psiElements.isEmpty())
		{
			return null;
		}

		PsiElement[] array = ContainerUtil.toArray(psiElements, PsiElement.ARRAY_FACTORY);

		// inc + 1, we dont need go absolute parent
		PsiElement executableTarget = ArrayUtil2.safeGet(array, index + 1);
		if(executableTarget == null)
		{
			return null;
		}

		return PsiTreeUtil.isAncestor(executableTarget, psiElement, true) ? executableTarget : null;
	}

	private static void collectLocations(MonoVirtualMachineProxy virtualMachine, XLineBreakpoint<?> lineBreakpoint, Map<MethodMirror, Location> methods, MethodMirror methodMirror)
	{
		TIntHashSet registeredLines = new TIntHashSet();
		for(Method_GetDebugInfo.Entry entry : methodMirror.debugInfo())
		{
			if(entry.line == (lineBreakpoint.getLine() + 1))
			{
				if(!registeredLines.add(entry.line))
				{
					continue;
				}
				methods.put(methodMirror, new LocationImpl(virtualMachine.getDelegate(), methodMirror, entry.offset));
			}
		}
	}
}
