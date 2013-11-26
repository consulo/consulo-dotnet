package org.mustbe.consulo.dotnet.compiler;

import java.util.Collection;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Function;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetCompilerOptionsBuilder
{
	private String myExecutableFromSdk;
	private Project myProject;
	private Sdk mySdk;

	public DotNetCompilerOptionsBuilder(Project project, Sdk sdk)
	{
		myProject = project;
		mySdk = sdk;
	}

	public GeneralCommandLine createCommandLine(Collection<VirtualFile> results)
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(mySdk.getHomePath() + "\\" + myExecutableFromSdk);
		commandLine.setWorkDirectory(myProject.getBasePath());

		String fileList = StringUtil.join(results, new Function<VirtualFile, String>()
		{
			@Override
			public String fun(VirtualFile virtualFile)
			{
				return FileUtil.toSystemDependentName(virtualFile.getPath());
			}
		}, " ");
		commandLine.addParameter(fileList);
		return commandLine;
	}

	public void setExecutableFromSdk(String executableFromSdk)
	{
		myExecutableFromSdk = executableFromSdk;
	}
}
