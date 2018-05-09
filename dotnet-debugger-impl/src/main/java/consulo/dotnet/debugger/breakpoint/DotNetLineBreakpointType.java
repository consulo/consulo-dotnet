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

package consulo.dotnet.debugger.breakpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
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
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointBase;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.debugger.DotNetDebuggerSourceLineResolver;
import consulo.dotnet.debugger.DotNetDebuggerSourceLineResolverEP;
import consulo.dotnet.debugger.DotNetDebuggerUtil;
import consulo.dotnet.debugger.DotNetEditorsProvider;
import consulo.dotnet.debugger.breakpoint.properties.DotNetLineBreakpointProperties;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.util.ArrayUtil2;
import consulo.ui.image.Image;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetLineBreakpointType extends XLineBreakpointType<DotNetLineBreakpointProperties>
{
	@Nonnull
	public static DotNetLineBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtension(DotNetLineBreakpointType.class);
	}

	public DotNetLineBreakpointType()
	{
		super("dotnet-line-breakpoint", ".NET Line Breakpoints");
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
	public XDebuggerEditorsProvider getEditorsProvider(@Nonnull XLineBreakpoint<DotNetLineBreakpointProperties> breakpoint, @Nonnull Project project)
	{
		return new DotNetEditorsProvider(null);
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public List<? extends XLineBreakpointVariant> computeVariants(@Nonnull Project project, @Nonnull XSourcePosition position)
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
	public static Set<PsiElement> collectExecutableChildren(PsiFile psiFile, int line)
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
		@Nonnull
		@RequiredReadAction
		@Override
		public String getText()
		{
			return "All";
		}

		@RequiredReadAction
		@Nullable
		@Override
		public Image getIcon()
		{
			return null;
		}

		@RequiredReadAction
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

		@Nonnull
		@Override
		@RequiredDispatchThread
		public String getText()
		{
			String text;
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
		public Image getIcon()
		{
			return AllIcons.Debugger.LambdaBreakpoint;
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

	@Nullable
	@Override
	public DotNetLineBreakpointProperties createProperties()
	{
		return new DotNetLineBreakpointProperties();
	}

	@Nullable
	@Override
	public DotNetLineBreakpointProperties createBreakpointProperties(@Nonnull VirtualFile file, int line)
	{
		return new DotNetLineBreakpointProperties();
	}
}
