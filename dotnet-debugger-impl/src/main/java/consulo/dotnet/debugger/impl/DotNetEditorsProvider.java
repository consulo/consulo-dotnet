package consulo.dotnet.debugger.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.debugger.DotNetDebuggerProvider;
import consulo.dotnet.debugger.DotNetDebuggerProviders;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.evaluation.XDebuggerEditorsProviderBase;
import consulo.language.Language;
import consulo.language.plain.PlainTextFileType;
import consulo.language.plain.PlainTextLanguage;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiFileFactory;
import consulo.language.psi.PsiManager;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author VISTALL
 * @since 06.06.14
 */
public class DotNetEditorsProvider extends XDebuggerEditorsProviderBase
{
	@Nullable
	private XDebugSession mySession;

	public DotNetEditorsProvider(@Nullable XDebugSession session)
	{
		mySession = session;
	}

	@Override
	@RequiredReadAction
	protected PsiFile createExpressionCodeFragment(@Nonnull Project project, @Nonnull String text, @Nullable PsiElement context, boolean isPhysical)
	{
		if(context == null)
		{
			XDebugSession session = mySession;
			XSourcePosition currentPosition = session == null ? null : session.getCurrentPosition();
			if(currentPosition != null)
			{
				VirtualFile file = currentPosition.getFile();
				PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
				if(psiFile != null)
				{
					context = psiFile.findElementAt(currentPosition.getOffset());
				}
			}
		}

		DotNetDebuggerProvider debuggerProvider = context == null ? null : DotNetDebuggerProviders.findByPsiFile(context.getContainingFile());
		if(debuggerProvider == null)
		{
			return PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, text);
		}

		return debuggerProvider.createExpressionCodeFragment(project, context, text, isPhysical);
	}

	@Override
	@Nonnull
	@RequiredReadAction
	public Collection<Language> getSupportedLanguages(@Nonnull Project project, @Nullable XSourcePosition sourcePosition)
	{
		if(sourcePosition == null)
		{
			return Collections.<Language>singletonList(PlainTextLanguage.INSTANCE);
		}

		DotNetDebuggerProvider dotNetDebuggerProvider = DotNetDebuggerProviders.findByVirtualFile(project, sourcePosition.getFile());
		if(dotNetDebuggerProvider == null)
		{
			return Collections.<Language>singletonList(PlainTextLanguage.INSTANCE);
		}
		return Collections.singletonList(dotNetDebuggerProvider.getEditorLanguage());
	}

	@Nonnull
	@Override
	public FileType getFileType()
	{
		return PlainTextFileType.INSTANCE;
	}
}
