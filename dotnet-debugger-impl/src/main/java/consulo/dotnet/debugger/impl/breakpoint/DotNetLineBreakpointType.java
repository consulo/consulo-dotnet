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

package consulo.dotnet.debugger.impl.breakpoint;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.AllIcons;
import consulo.document.Document;
import consulo.document.util.DocumentUtil;
import consulo.document.util.TextRange;
import consulo.dotnet.debugger.DotNetDebuggerSourceLineResolver;
import consulo.dotnet.debugger.DotNetDebuggerUtil;
import consulo.dotnet.debugger.impl.DotNetEditorsProvider;
import consulo.dotnet.debugger.impl.breakpoint.properties.DotNetLineBreakpointProperties;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.util.ArrayUtil2;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.breakpoint.XLineBreakpointType;
import consulo.execution.debug.evaluation.XDebuggerEditorsProvider;
import consulo.language.psi.*;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.image.Image;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author VISTALL
 * @since 10.04.14
 */
@ExtensionImpl
public class DotNetLineBreakpointType extends XLineBreakpointType<DotNetLineBreakpointProperties>
{
	@Nonnull
	public static DotNetLineBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtensionOrFail(DotNetLineBreakpointType.class);
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

		PsiFile file = PsiManager.getInstance(breakpoint.getProject()).findFile(virtualFile);
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
		if(allExecutableChildren.isEmpty() || allExecutableChildren.size() == 1)
		{
			return Collections.emptyList();
		}

		List<XLineBreakpointVariant> variants = new ArrayList<XLineBreakpointVariant>(1 + allExecutableChildren.size());
		int i = -1;
		for(PsiElement allExecutableChild : allExecutableChildren)
		{
			variants.add(new SingleExecutableVariant(position, allExecutableChild, i++));
		}
		variants.add(new AllBreakpointVariant(position));

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

		DotNetDebuggerSourceLineResolver resolver = DotNetDebuggerSourceLineResolver.forLanguage(psiFile.getLanguage());

		Set<PsiElement> allExecutableChildren = resolver.getAllExecutableChildren(element);
		if(!allExecutableChildren.isEmpty())
		{
			Set<PsiElement> newSet = new LinkedHashSet<>(allExecutableChildren.size() + 1);
			newSet.add(likeMethod);
			newSet.addAll(allExecutableChildren);
			return newSet;
		}

		return Collections.singleton(likeMethod);
	}

	class AllBreakpointVariant extends XLineBreakpointAllVariant
	{
		public AllBreakpointVariant(XSourcePosition position)
		{
			super(position);
		}

		@Nullable
		@Override
		public DotNetLineBreakpointProperties createProperties()
		{
			return new DotNetLineBreakpointProperties();
		}
	}

	class SingleExecutableVariant extends XLineBreakpointVariant
	{
		private final XSourcePosition myPosition;
		private final PsiElement myElement;
		private int myIndex;

		public SingleExecutableVariant(XSourcePosition position, PsiElement element, int index)
		{
			myPosition = position;
			myElement = element;
			myIndex = index;
		}

		@Nonnull
		@Override
		@RequiredUIAccess
		public String getText()
		{
			String text;
			if(myElement instanceof PsiNameIdentifierOwner)
			{
				return "Line";
			}
			else
			{
				text = myElement.getText();
				return StringUtil.shortenTextWithEllipsis(text, 100, 0);
			}
		}

		@Nullable
		@Override
		@RequiredUIAccess
		public Image getIcon()
		{
			if(myElement instanceof PsiNameIdentifierOwner)
			{
				return AllIcons.Debugger.Db_set_breakpoint;
			}
			return AllIcons.Debugger.LambdaBreakpoint;
		}

		@Nullable
		@Override
		@RequiredUIAccess
		public TextRange getHighlightRange()
		{
			if(myElement instanceof PsiNameIdentifierOwner)
			{
				return intersectWithLine(myElement.getTextRange(), myElement.getContainingFile(), myPosition.getLine());
			}
			return myElement.getTextRange();
		}

		@Nullable
		public TextRange intersectWithLine(@Nullable TextRange range, @Nullable PsiFile file, int line)
		{
			if(range != null && file != null)
			{
				Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
				if(document != null)
				{
					range = range.intersection(DocumentUtil.getLineTextRange(document, line));
				}
			}
			return range;
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
