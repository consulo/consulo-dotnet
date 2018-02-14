package consulo.dotnet.debugger.breakpoint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetCodeBlockOwner;
import consulo.dotnet.psi.DotNetCompositeStatement;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.psi.DotNetFieldDeclaration;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetModifierList;
import consulo.dotnet.psi.DotNetStatement;
import consulo.dotnet.psi.DotNetXXXAccessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import consulo.xdebugger.breakpoints.XLineBreakpointTypeResolver;

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

		final Ref<Pair<XLineBreakpointType<?>, PsiElement>> result = Ref.create(Pair.<XLineBreakpointType<?>, PsiElement>empty());

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

				if(parent instanceof DotNetLikeMethodDeclaration || parent instanceof DotNetXXXAccessor)
				{
					if(parent.getTextRange().getEndOffset() >= document.getLineEndOffset(line))
					{
						PsiElement body = ((DotNetCodeBlockOwner) parent).getCodeBlock();
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
