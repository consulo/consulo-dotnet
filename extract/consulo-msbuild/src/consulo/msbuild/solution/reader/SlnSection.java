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
import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.util.Ref;

/**
 * @author VISTALL
 * @since 30-Jan-17
 * <p>
 * https://github.com/mono/monodevelop/blob/master/main/src/core/MonoDevelop.Core/MonoDevelop.Projects.MSBuild/SlnFile.cs
 */
public class SlnSection
{
	SlnPropertySetCollection nestedPropertySets;
	SlnPropertySet properties;

	public String Id;
	public int Line;
	public SlnSectionType SectionType;

	List<String> sectionLines;
	int baseIndex;
	private SlnFile myParentFile;

	public SlnFile getParentFile()
	{
		return myParentFile;
	}

	public SlnSection setParentFile(SlnFile parentFile)
	{
		myParentFile = parentFile;
		return this;
	}

	public void Read(LineNumberReader reader, String line, Ref<Integer> curLineNum) throws IOException
	{
		Line = curLineNum.get();
		int k = line.indexOf('(');
		if(k == -1)
		{
			throw new InvalidSolutionFormatException(curLineNum.get(), "Section id missing");
		}
		String tag = DotNetString.substring(line, 0, k).trim();
		int k2 = line.indexOf(')', k);
		if(k2 == -1)
		{
			throw new InvalidSolutionFormatException(curLineNum.get());
		}
		Id = DotNetString.substring(line, k + 1, k2 - k - 1);

		k = line.indexOf('=', k2);
		SectionType = ToSectionType(curLineNum.get(), line.substring(k + 1).trim());

		String endTag = "End" + tag;

		sectionLines = new ArrayList<>();
		curLineNum.set(curLineNum.get() + 1);
		baseIndex = curLineNum.get();
		while((line = reader.readLine()) != null)
		{
			curLineNum.set(curLineNum.get() + 1);
			line = line.trim();
			if(line.equals(endTag))
			{
				break;
			}
			sectionLines.add(line);
		}
		if(line == null)
		{
			throw new InvalidSolutionFormatException(curLineNum.get(), "Closing section tag not found");
		}
	}

	void LoadPropertySets()
	{
		if(sectionLines != null)
		{
			SlnPropertySet curSet = null;
			for(int n = 0; n < sectionLines.size(); n++)
			{
				String line = sectionLines.get(n);
				if(DotNetString.IsNullOrEmpty(line.trim()))
				{
					continue;
				}
				int i = line.indexOf('.');
				if(i == -1)
				{
					throw new InvalidSolutionFormatException(baseIndex + n);
				}
				String id = DotNetString.substring(line, 0, i);
				if(curSet == null || !id.equals(curSet.Id))
				{
					curSet = new SlnPropertySet(id);
					nestedPropertySets.add(curSet);
				}
				curSet.ReadLine(line.substring(i + 1), baseIndex + n);
			}
			sectionLines = null;
		}
	}

	public SlnPropertySetCollection getNestedPropertySets()
	{

		if(nestedPropertySets == null)
		{
			nestedPropertySets = new SlnPropertySetCollection(this);
			if(sectionLines != null)
			{
				LoadPropertySets();
			}
		}
		return nestedPropertySets;
	}

	public SlnPropertySet getProperties()
	{
		if(properties == null)
		{
			properties = new SlnPropertySet();
			properties.ParentSection = this;
			if(sectionLines != null)
			{
				for(String line : sectionLines)
				{
					properties.ReadLine(line, Line);
				}
				sectionLines = null;
			}
		}
		return properties;
	}

	SlnSectionType ToSectionType(int curLineNum, String s)
	{
		if(s.equals("preSolution") || s.equals("preProject"))
		{
			return SlnSectionType.PreProcess;
		}
		if(s.equals("postSolution") || s.equals("postProject"))
		{
			return SlnSectionType.PostProcess;
		}
		throw new InvalidSolutionFormatException(curLineNum, "Invalid section type: " + s);
	}
}
