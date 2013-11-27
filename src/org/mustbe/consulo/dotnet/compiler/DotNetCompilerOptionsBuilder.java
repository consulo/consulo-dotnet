package org.mustbe.consulo.dotnet.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetCompilerOptionsBuilder
{
	private String myExecutable;
	private Project myProject;
	private Sdk mySdk;

	public DotNetCompilerOptionsBuilder(Project project, Sdk sdk)
	{
		myProject = project;
		mySdk = sdk;
	}

	public GeneralCommandLine createCommandLine(Module module, Collection<VirtualFile> results) throws IOException
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(myExecutable);
		commandLine.setWorkDirectory(myProject.getBasePath());

		File tempFile = FileUtil.createTempFile("consulo-csc", ".rsp");

		String outputFile = DotNetMacros.extract(module, false, false);
		FileUtil.appendToFile(tempFile, "/out:" + outputFile + "\n");
		FileUtil.appendToFile(tempFile, "/utf8output\n");

		for(VirtualFile result : results)
		{
			FileUtil.appendToFile(tempFile, FileUtil.toSystemDependentName(result.getPath()) + "\n");
		}

		FileUtil.createParentDirs(new File(outputFile));

		commandLine.addParameter("@" + tempFile.getAbsolutePath());
		/*String fileList = StringUtil.join(results, new Function<VirtualFile, String>()
		{
			@Override
			public String fun(VirtualFile virtualFile)
			{
				return FileUtil.toSystemDependentName(virtualFile.getPath());
			}
		}, " ");
		commandLine.addParameter(fileList);   */


		return commandLine;
	}

	public void setExecutableFromSdk(String executableFromSdk)
	{
		myExecutable = mySdk.getHomePath() + "\\" + executableFromSdk;
	}
}
