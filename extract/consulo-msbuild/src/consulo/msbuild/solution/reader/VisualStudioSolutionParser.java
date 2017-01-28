/*
 * Copyright 2013-2017 consulo.io
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

package consulo.msbuild.solution.reader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import consulo.annotations.RequiredReadAction;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class VisualStudioSolutionParser
{
	private static class ParseErrorException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public ParseErrorException(String message)
		{
			super(message);
		}

		private ParseErrorException(Throwable cause)
		{
			super(cause);
		}
	}

	private static final String PROJECT_LINE_LOOKAHEAD = "Project(";
	private static final Pattern PROJECT_LINE_PATTERN = Pattern.compile("Project\\(\"[^\"]++\"\\)\\s*+=\\s*+\"([^\"]++)\",\\s*+\"([^\"]++)\"," + "" + "\\s*+\"[^\"]++\"");

	@RequiredReadAction
	public static List<VisualStudioProjectInfo> parse(Project project, VirtualFile virtualFile)
	{
		ImmutableList.Builder<VisualStudioProjectInfo> projectsBuilder = ImmutableList.builder();

		try
		{
			File file = VfsUtil.virtualToIoFile(virtualFile);
			int lineNumber = 1;
			for(String line : Files.readLines(file, StandardCharsets.UTF_8))
			{
				if(line.startsWith(PROJECT_LINE_LOOKAHEAD))
				{
					parseProjectLine(project, file, lineNumber, line, projectsBuilder);
				}
				lineNumber++;
			}
		}
		catch(IOException e)
		{
			throw Throwables.propagate(e);
		}

		return projectsBuilder.build();
	}

	@RequiredReadAction
	private static void parseProjectLine(Project project, File file, int lineNumber, String line, ImmutableList.Builder<VisualStudioProjectInfo> builder)
	{
		Matcher matcher = PROJECT_LINE_PATTERN.matcher(line);
		if(!matcher.matches())
		{
			throw new ParseErrorException("Expected the line " + lineNumber + " of " + file.getAbsolutePath() + " to match the regular expression " + PROJECT_LINE_PATTERN);
		}

		String name = matcher.group(1);
		String relativePath = matcher.group(2);
		File projectFile = new File(file.getParent(), FileUtil.toSystemDependentName(relativePath));
		if(projectFile.isDirectory() || !projectFile.exists())
		{
			return;
		}

		VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(projectFile);
		if(virtualFile == null)
		{
			return;
		}

		PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);

		if(psiFile instanceof XmlFile)
		{
			DomFileElement<consulo.msbuild.dom.Project> fileElement = DomManager.getDomManager(project).getFileElement((XmlFile) psiFile, consulo.msbuild.dom.Project.class);
			if(fileElement != null)
			{
				builder.add(new VisualStudioProjectInfo(name, projectFile, fileElement.getRootElement()));
			}
		}
	}
}
