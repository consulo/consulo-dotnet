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

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;

/**
 * @author VISTALL
 * @since 30-Jan-17
 * <p>
 * https://github.com/mono/monodevelop/blob/master/main/src/core/MonoDevelop.Core/MonoDevelop.Projects.MSBuild/SlnFile.cs
 */
public class SlnProject
{
	SlnSectionCollection sections = new SlnSectionCollection();

	public int Line;
	public String TypeGuid;
	public String Name;
	public String FilePath;
	public String Id;
	public boolean Processed;

	private SlnFile myParentFile;

	public SlnFile getParentFile()
	{
		return myParentFile;
	}

	public SlnProject setParentFile(SlnFile parentFile)
	{
		myParentFile = parentFile;
		sections.setParentFile(parentFile);
		return this;
	}

	public void Read(LineNumberReader reader, String line, Ref<Integer> curLineNum) throws IOException
	{
		Line = curLineNum.get();

		Ref<Integer> n = Ref.create(0);
		FindNext(curLineNum, line, n, '(');
		n = Ref.create(n.get() + 1);
		FindNext(curLineNum, line, n, '"');
		Ref<Integer> n2 = Ref.create(n.get() + 1);
		FindNext(curLineNum, line, n2, '"');
		TypeGuid = DotNetString.substring(line, n.get() + 1, n2.get() - n.get() - 1);

		n = Ref.create(n2.get() + 1);
		FindNext(curLineNum, line, n, ')');
		FindNext(curLineNum, line, n, '=');

		FindNext(curLineNum, line, n, '"');
		n2 = Ref.create(n.get() + 1);
		FindNext(curLineNum, line, n2, '"');
		Name = DotNetString.substring(line, n.get() + 1, n2.get() - n.get() - 1);

		n = Ref.create(n2.get() + 1);
		FindNext(curLineNum, line, n, ',');
		FindNext(curLineNum, line, n, '"');
		n2 = Ref.create(n.get() + 1);
		FindNext(curLineNum, line, n2, '"');
		FilePath = DotNetString.substring(line, n.get() + 1, n2.get() - n.get() - 1);

		n = Ref.create(n2.get() + 1);
		FindNext(curLineNum, line, n, ',');
		FindNext(curLineNum, line, n, '"');
		n2 = Ref.create(n.get() + 1);
		FindNext(curLineNum, line, n2, '"');
		Id = DotNetString.substring(line, n.get() + 1, n2.get() - n.get() - 1);

		while((line = reader.readLine()) != null)
		{
			curLineNum = Ref.create(curLineNum.get() + 1);
			line = line.trim();
			if(line.equals("EndProject"))
			{
				return;
			}
			if(line.startsWith("ProjectSection"))
			{
				if(sections == null)
				{
					sections = new SlnSectionCollection();
				}
				SlnSection sec = new SlnSection();
				sections.add(sec);
				sec.Read(reader, line, curLineNum);
			}
		}

		throw new InvalidSolutionFormatException(curLineNum.get(), "Project section not closed");
	}

	void FindNext(Ref<Integer> ln, String line, Ref<Integer> i, char c)
	{
		i.set(StringUtil.indexOf(line, c, i.get()));
		if(i.get() == -1)
		{
			throw new InvalidSolutionFormatException(ln.get());
		}
	}

}
