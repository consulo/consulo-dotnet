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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredDispatchThread;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerSourceLineResolver;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerSourceLineResolverEP;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerUtil;
import org.mustbe.consulo.dotnet.debugger.DotNetEditorsProvider;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachine;
import org.mustbe.consulo.dotnet.debugger.TypeMirrorUnloadedException;
import org.mustbe.consulo.dotnet.debugger.linebreakType.properties.DotNetLineBreakpointProperties;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetDebuggerCompilerGenerateUtil;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.DocumentUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.SuspendPolicy;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointBase;
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
 * @since 10.04.14
 */
public class DotNetLineBreakpointType extends XLineBreakpointType<DotNetLineBreakpointProperties>
{
	@NotNull
	public static DotNetLineBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtension(DotNetLineBreakpointType.class);
	}

	public DotNetLineBreakpointType()
	{
		super("dotnet-linebreapoint", ".NET Line Breakpoint");
	}

	@Nullable
	@Override
	@RequiredReadAction
	public TextRange getHighlightRange(XLineBreakpoint<DotNetLineBreakpointProperties> breakpoint)
	{
		DotNetLineBreakpointProperties properties = breakpoint.getProperties();
		if(properties == null)
		{
			return null;
		}
		Integer executableChildrenAtLineIndex = properties.getExecutableChildrenAtLineIndex();
		if(executableChildrenAtLineIndex == null || executableChildrenAtLineIndex == -1)
		{
			return null;
		}

		VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
		if(virtualFile == null)
		{
			return null;
		}

		XBreakpointBase base = (XBreakpointBase) breakpoint;

		PsiFile file = PsiManager.getInstance(base.getProject()).findFile(virtualFile);
		if(file == null)
		{
			return null;
		}

		Set<PsiElement> allExecutableChildren = collectExecutableChildren(file, breakpoint.getLine());

		PsiElement[] psiElements = ContainerUtil.toArray(allExecutableChildren, PsiElement.ARRAY_FACTORY);
		// we need plus one, due first index - its original method or accessor
		PsiElement psiElement = ArrayUtil2.safeGet(psiElements, executableChildrenAtLineIndex + 1);
		if(psiElement == null)
		{
			return null;
		}
		return psiElement.getTextRange();
	}

	@Nullable
	@Override
	public XDebuggerEditorsProvider getEditorsProvider(@NotNull XLineBreakpoint<DotNetLineBreakpointProperties> breakpoint, @NotNull Project project)
	{
		return new DotNetEditorsProvider(null);
	}

	@NotNull
	@Override
	@RequiredReadAction
	public List<? extends XLineBreakpointVariant> computeVariants(@NotNull Project project, @NotNull XSourcePosition position)
	{
		VirtualFile virtualFile = position.getFile();

		PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
		if(file == null)
		{
			return Collections.emptyList();
		}

		Set<PsiElement> allExecutableChildren = collectExecutableChildren(file, position.getLine());

		List<XLineBreakpointVariant> variants = new ArrayList<XLineBreakpointVariant>(1 + allExecutableChildren.size());
		variants.add(new AllBreakpointVariant());
		int i = -1;
		for(PsiElement allExecutableChild : allExecutableChildren)
		{
			variants.add(new SingleExecutableVariant(allExecutableChild, i++));
		}
		return variants;
	}

	@RequiredReadAction
	private static Set<PsiElement> collectExecutableChildren(PsiFile psiFile, int line)
	{
		final Document document = PsiDocumentManager.getInstance(psiFile.getProject()).getDocument(psiFile);
		if(document == null || line >= document.getLineCount())
		{
			return Collections.emptySet();
		}
		PsiElement element = DotNetDebuggerUtil.findPsiElement(psiFile, line);
		final TextRange lineRange = DocumentUtil.getLineTextRange(document, line);
		do
		{
			if(element == null)
			{
				break;
			}
			PsiElement parent = element.getParent();
			if(parent == null || (parent.getTextOffset() < lineRange.getStartOffset()))
			{
				break;
			}
			element = parent;
		}
		while(true);

		PsiElement likeMethod = PsiTreeUtil.getParentOfType(element, DotNetQualifiedElement.class);
		if(likeMethod == null)
		{
			return Collections.emptySet();
		}

		DotNetDebuggerSourceLineResolver resolver = DotNetDebuggerSourceLineResolverEP.INSTANCE.forLanguage(psiFile.getLanguage());

		Set<PsiElement> allExecutableChildren = resolver.getAllExecutableChildren(element);
		if(allExecutableChildren.isEmpty())
		{
			return Collections.emptySet();
		}

		Set<PsiElement> newSet = new LinkedHashSet<PsiElement>(allExecutableChildren.size() + 1);
		newSet.add(likeMethod);
		newSet.addAll(allExecutableChildren);

		return newSet;
	}


	class AllBreakpointVariant extends XLineBreakpointVariant
	{
		@Override
		public String getText()
		{
			return "All";
		}

		@Nullable
		@Override
		public Icon getIcon()
		{
			return null;
		}

		@Nullable
		@Override
		public TextRange getHighlightRange()
		{
			return null;
		}

		@Nullable
		@Override
		public DotNetLineBreakpointProperties createProperties()
		{
			return new DotNetLineBreakpointProperties();
		}
	}

	class SingleExecutableVariant extends AllBreakpointVariant
	{
		private PsiElement myExecutableChild;
		private int myIndex;

		public SingleExecutableVariant(PsiElement executableChild, int index)
		{
			myExecutableChild = executableChild;
			myIndex = index;
		}

		@Override
		@RequiredDispatchThread
		public String getText()
		{
			String text = null;
			if(myExecutableChild instanceof PsiNameIdentifierOwner)
			{
				TextRange textRange = new TextRange(myExecutableChild.getTextOffset(), myExecutableChild.getTextRange().getEndOffset());
				text = textRange.substring(myExecutableChild.getContainingFile().getText());
			}
			else
			{
				text = myExecutableChild.getText();
			}
			return StringUtil.shortenTextWithEllipsis(text, 100, 0);
		}

		@Nullable
		@Override
		@RequiredDispatchThread
		public Icon getIcon()
		{
			return IconDescriptorUpdaters.getIcon(myExecutableChild, 0);
		}

		@Nullable
		@Override
		@RequiredDispatchThread
		public TextRange getHighlightRange()
		{
			return myExecutableChild.getTextRange();
		}

		@Nullable
		@Override
		public DotNetLineBreakpointProperties createProperties()
		{
			DotNetLineBreakpointProperties breakpointProperties = new DotNetLineBreakpointProperties();
			breakpointProperties.setExecutableChildrenAtLineIndex(myIndex);
			return breakpointProperties;
		}
	}

	public boolean createRequest(@NotNull XDebugSession debugSession, @NotNull DotNetVirtualMachine virtualMachine, @NotNull XLineBreakpoint breakpoint, @Nullable TypeMirror typeMirror)
	{
		try
		{
			virtualMachine.stopBreakpointRequests(breakpoint);

			return createRequestImpl(debugSession.getProject(), virtualMachine, breakpoint, typeMirror);
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
		return false;
	}

	private boolean createRequestImpl(@NotNull Project project,
			@NotNull DotNetVirtualMachine virtualMachine,
			@NotNull XLineBreakpoint breakpoint,
			@Nullable TypeMirror typeMirror) throws TypeMirrorUnloadedException
	{
		Collection<Location> locationsImpl = findLocationsImpl(project, virtualMachine, breakpoint, typeMirror);
		if(!locationsImpl.isEmpty())
		{
			if(breakpoint.getSuspendPolicy() != SuspendPolicy.NONE)
			{
				for(Location location : locationsImpl)
				{
					EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
					BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
					breakpointRequest.enable();

					virtualMachine.putRequest(breakpoint, breakpointRequest);
				}
			}
		}

		DotNetBreakpointUtil.updateBreakpointPresentation(project, !locationsImpl.isEmpty(), breakpoint);

		return !locationsImpl.isEmpty();
	}

	@NotNull
	public Collection<Location> findLocationsImpl(@NotNull final Project project,
			@NotNull final DotNetVirtualMachine virtualMachine,
			@NotNull final XLineBreakpoint<?> lineBreakpoint,
			@Nullable final TypeMirror typeMirror) throws TypeMirrorUnloadedException
	{
		final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(lineBreakpoint.getFileUrl());
		if(fileByUrl == null)
		{
			return Collections.emptyList();
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
			return Collections.emptyList();
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
			return Collections.emptyList();
		}

		if(typeMirror != null && !Comparing.equal(vmQualifiedName, typeMirror.qualifiedName()))
		{
			return Collections.emptyList();
		}

		TypeMirror mirror = typeMirror == null ? virtualMachine.findTypeMirror(project, fileByUrl, vmQualifiedName) : typeMirror;

		if(mirror == null)
		{
			return Collections.emptyList();
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

		return methods.values();
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

		Set<PsiElement> psiElements = collectExecutableChildren(otherPsiFile, entry.line - 1);
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


	private void collectLocations(DotNetVirtualMachine virtualMachine, XLineBreakpoint<?> lineBreakpoint, Map<MethodMirror, Location> methods, MethodMirror methodMirror)
	{
		for(Method_GetDebugInfo.Entry entry : methodMirror.debugInfo())
		{
			if(entry.line == (lineBreakpoint.getLine() + 1))
			{
				methods.put(methodMirror, new LocationImpl(virtualMachine.getDelegate(), methodMirror, entry.offset));
			}
		}
	}

	@Nullable
	@Override
	public DotNetLineBreakpointProperties createProperties()
	{
		return new DotNetLineBreakpointProperties();
	}

	@Nullable
	@Override
	public DotNetLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line)
	{
		return new DotNetLineBreakpointProperties();
	}
}
