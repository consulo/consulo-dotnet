package consulo.dotnet.debugger;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerProvider;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import com.intellij.psi.impl.source.PsiPlainTextFileImpl;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase;

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
	protected PsiFile createExpressionCodeFragment(@NotNull Project project, @NotNull String text, @Nullable PsiElement context, boolean isPhysical)
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
			LightVirtualFile virtualFile = new LightVirtualFile("test.txt", PlainTextFileType.INSTANCE, text);
			return new PsiPlainTextFileImpl(new SingleRootFileViewProvider(PsiManager.getInstance(project), virtualFile, true));
		}

		return debuggerProvider.createExpressionCodeFragment(project, context, text, isPhysical);
	}

	@Override
	@NotNull
	@RequiredReadAction
	public Collection<Language> getSupportedLanguages(@NotNull Project project, @Nullable XSourcePosition sourcePosition)
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

	@NotNull
	@Override
	public FileType getFileType()
	{
		return PlainTextFileType.INSTANCE;
	}
}
