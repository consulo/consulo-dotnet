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

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.Ref;

/**
 * @author VISTALL
 * @since 30-Jan-17
 * <p>
 * https://github.com/mono/monodevelop/blob/master/main/src/core/MonoDevelop.Core/MonoDevelop.Projects.MSBuild/SlnFile.cs
 */
public class SlnFile
{
	SlnProjectCollection projects = new SlnProjectCollection();
	SlnSectionCollection sections = new SlnSectionCollection();
	SlnPropertySet metadata = new SlnPropertySet(true);
	int prefixBlankLines = 1;

	public String FormatVersion;
	public String ProductDescription;

	public SlnFile()
	{
		projects.setParentFile(this);
		sections.setParentFile(this);
	}

	public SlnSectionCollection getSections()
	{
		return sections;
	}

	@NotNull
	public Collection<SlnProject> getProjects()
	{
		return projects;
	}

	public SlnPropertySetCollection getProjectConfigurationsSection()
	{
		return sections.GetOrCreateSection("ProjectConfigurationPlatforms", SlnSectionType.PostProcess).getNestedPropertySets();
	}

	public void Read(LineNumberReader reader) throws IOException
	{
		String line;
		int curLineNum = 0;
		boolean globalFound = false;
		boolean productRead = false;

		while((line = reader.readLine()) != null)
		{
			curLineNum++;
			line = line.trim();
			if(line.startsWith("Microsoft Visual Studio Solution File"))
			{
				int i = line.lastIndexOf(' ');
				if(i == -1)
				{
					throw new InvalidSolutionFormatException(curLineNum);
				}
				FormatVersion = line.substring(i + 1);
				prefixBlankLines = curLineNum - 1;
			}
			if(line.startsWith("# "))
			{
				if(!productRead)
				{
					productRead = true;
					ProductDescription = line.substring(2);
				}
			}
			else if(line.startsWith("Project"))
			{
				SlnProject p = new SlnProject();
				Ref<Integer> ref = Ref.create(curLineNum);
				p.Read(reader, line, ref);
				curLineNum = ref.get();
				projects.add(p);
			}
			else if(line.equals("Global"))
			{
				if(globalFound)
				{
					throw new InvalidSolutionFormatException(curLineNum, "Global section specified more than once");
				}
				globalFound = true;
				while((line = reader.readLine()) != null)
				{
					curLineNum++;
					line = line.trim();
					if(line.equals("EndGlobal"))
					{
						break;
					}
					else if(line.startsWith("GlobalSection"))
					{
						SlnSection sec = new SlnSection();
						Ref<Integer> ref = Ref.create(curLineNum);
						sec.Read(reader, line, ref);
						curLineNum = ref.get();
						sections.add(sec);
					}
					else // Ignore text that's out of place
					{
						continue;
					}
				}
				if(line == null)
				{
					throw new InvalidSolutionFormatException(curLineNum, "Global section not closed");
				}
			}
			else if(line.indexOf('=') != -1)
			{
				metadata.ReadLine(line, curLineNum);
			}
		}

		if(FormatVersion == null)
		{
			throw new InvalidSolutionFormatException(curLineNum, "File header is missing");
		}
	}

}
