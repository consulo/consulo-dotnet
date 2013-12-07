package org.mustbe.consulo.dotnet.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.module.Module;
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
	private Sdk mySdk;

	private final List<String> myArguments = new ArrayList<String>();

	public DotNetCompilerOptionsBuilder(Sdk sdk)
	{
		mySdk = sdk;
	}

	public DotNetCompilerOptionsBuilder addArgument(@NotNull String arg)
	{
		myArguments.add(arg + "\n");
		return this;
	}

	public GeneralCommandLine createCommandLine(Module module, Collection<VirtualFile> results) throws IOException
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(myExecutable);
		commandLine.setWorkDirectory(module.getModuleDirPath());

		File tempFile = FileUtil.createTempFile("consulo-csc", ".rsp");

		String outputFile = DotNetMacros.extract(module, false, false);
		FileUtil.appendToFile(tempFile, "/out:" + outputFile + "\n");
		for(String argument : myArguments)
		{
			FileUtil.appendToFile(tempFile, argument);
		}

		for(VirtualFile result : results)
		{
			FileUtil.appendToFile(tempFile, FileUtil.toSystemDependentName(result.getPath()) + "\n");
		}

		FileUtil.createParentDirs(new File(outputFile));

		commandLine.addParameter("@" + tempFile.getAbsolutePath());
		commandLine.setRedirectErrorStream(true);
		return commandLine;
	}

	public void setExecutableFromSdk(String executableFromSdk)
	{
		myExecutable = mySdk.getHomePath() + "\\" + executableFromSdk;
	}
}
