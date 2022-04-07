package consulo.dotnet.debugger.breakpoint;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.util.function.Processor;
import consulo.debugger.XDebuggerUtil;
import consulo.debugger.breakpoint.XLineBreakpointType;
import consulo.debugger.breakpoint.XLineBreakpointTypeResolver;
import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.dotnet.psi.*;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.project.Project;
import consulo.util.lang.Pair;
import consulo.util.lang.ref.SimpleReference;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 19.03.2015
 */
public class DotNetLineBreakpointTypeResolver implements XLineBreakpointTypeResolver
{
	@Nullable
	@Override
	@RequiredReadAction
	public XLineBreakpointType<?> resolveBreakpointType(@Nonnull Project project, @Nonnull VirtualFile virtualFile, final int line)
	{
		final Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
		if(document == null)
		{
			return null;
		}

		return tryResolveBreakpointType(project, virtualFile, line).getFirst();
	}

	@RequiredReadAction
	@Nonnull
	public static Pair<XLineBreakpointType<?>, PsiElement> tryResolveBreakpointType(@Nonnull final Project project, @Nonnull final VirtualFile virtualFile, final int line)
	{
		final Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
		if(document == null)
		{
			return Pair.empty();
		}

		final SimpleReference<Pair<XLineBreakpointType<?>, PsiElement>> result = SimpleReference.create(Pair.<XLineBreakpointType<?>, PsiElement>empty());

		XDebuggerUtil.getInstance().iterateLine(project, document, line, new Processor<PsiElement>()
		{
			@Override
			@RequiredReadAction
			public boolean process(PsiElement element)
			{
				// avoid comments
				if((element instanceof PsiWhiteSpace) || (PsiTreeUtil.getParentOfType(element, PsiComment.class, false) != null))
				{
					return true;
				}

				PsiElement parent = element;
				while(element != null)
				{
					// skip modifiers
					if(element instanceof DotNetModifierList)
					{
						element = element.getParent();
						continue;
					}

					final int offset = element.getTextOffset();
					if(offset >= 0)
					{
						if(document.getLineNumber(offset) != line)
						{
							break;
						}
					}
					parent = element;
					element = element.getParent();
				}

				if(parent instanceof DotNetLikeMethodDeclaration || parent instanceof DotNetXAccessor)
				{
					if(parent.getTextRange().getEndOffset() >= document.getLineEndOffset(line))
					{
						PsiElement body = ((DotNetCodeBlockOwner) parent).getCodeBlock().getElement();
						if(body instanceof DotNetCompositeStatement)
						{
							DotNetStatement[] statements = ((DotNetCompositeStatement) body).getStatements();
							if(statements.length > 0 && document.getLineNumber(body.getTextOffset()) == line)
							{
								result.set(Pair.<XLineBreakpointType<?>, PsiElement>create(DotNetLineBreakpointType.getInstance(), null));
							}
						}
						else if(body instanceof DotNetExpression)
						{
							// if body is expression and at the same line - return line breakpoint
							if(document.getLineNumber(body.getTextOffset()) == line)
							{
								result.set(Pair.<XLineBreakpointType<?>, PsiElement>create(DotNetLineBreakpointType.getInstance(), null));
							}
						}
					}

					if(result.get().getFirst() == null)
					{
						result.set(Pair.<XLineBreakpointType<?>, PsiElement>create(DotNetMethodBreakpointType.getInstance(), parent));
					}
				}
				else if(parent instanceof DotNetFieldDeclaration)
				{
					// we can't place breakpoints to field declaration
				}
				else
				{
					result.set(Pair.<XLineBreakpointType<?>, PsiElement>create(DotNetLineBreakpointType.getInstance(), null));
				}
				return true;
			}
		});
		return result.get();
	}
}
