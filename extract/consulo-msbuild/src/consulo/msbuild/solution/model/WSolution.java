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

package consulo.msbuild.solution.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.common.base.Throwables;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.annotations.RequiredReadAction;
import consulo.msbuild.solution.reader.SlnFile;
import consulo.msbuild.solution.reader.SlnProject;
import consulo.msbuild.solution.reader.SlnSection;

/**
 * @author VISTALL
 * @since 30-Jan-17
 * <p>
 * solution wrapper
 */
public class WSolution
{
	public static final String SECTION_NESTED_PROJECTS = "NestedProjects";

	@NotNull
	@RequiredReadAction
	public static WSolution build(@NotNull Project project, @NotNull VirtualFile solutionVirtualFile)
	{
		SlnFile slnFile = new SlnFile();
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(solutionVirtualFile.getInputStream(), StandardCharsets.UTF_8)))
		{
			slnFile.Read(reader);
		}
		catch(IOException e)
		{
			throw Throwables.propagate(e);
		}

		return new WSolution(project, solutionVirtualFile, slnFile);
	}

	private final SlnFile myFile;

	private List<WProject> myProjects = new ArrayList<>();

	@RequiredReadAction
	public WSolution(Project project, VirtualFile solutionVirtualFile, SlnFile file)
	{
		myFile = file;
		for(SlnProject slnProject : file.getProjects())
		{
			myProjects.add(new WProject(project, solutionVirtualFile, slnProject));
		}
	}

	@Nullable
	public SlnSection getSection(String id)
	{
		return myFile.getSections().GetSection(id);
	}

	public Collection<WProject> getProjects()
	{
		return myProjects;
	}
}
