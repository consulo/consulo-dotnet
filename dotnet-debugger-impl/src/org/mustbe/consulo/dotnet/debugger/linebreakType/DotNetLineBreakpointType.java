package org.mustbe.consulo.dotnet.debugger.linebreakType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetLineBreakpointType extends XLineBreakpointType<DotNetLineBreakpointProperties>
{
	public DotNetLineBreakpointType()
	{
		super("dotnet-linebreapoint", ".NET Line Breakpoint");
	}

	@Override
	public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project)
	{
		PsiManager psiManager = PsiManager.getInstance(project);

		PsiFile psiFile = psiManager.findFile(file);
		if(psiFile == null)
		{
			return false;
		}
		for(DotNetDebuggerProvider dotNetDebuggerProvider : DotNetDebuggerProvider.EP_NAME.getExtensions())
		{
			if(dotNetDebuggerProvider.isSupported(psiFile))
			{
				return true;
			}
		}
		return false;
	}

	@Nullable
	@Override
	public DotNetLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile virtualFile, int i)
	{
		return new DotNetLineBreakpointProperties();
	}
}
