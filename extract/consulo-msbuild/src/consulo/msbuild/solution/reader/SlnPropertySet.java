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

import java.util.Map;
import java.util.TreeMap;

import com.intellij.openapi.util.text.StringUtil;

/**
 * @author VISTALL
 * @since 30-Jan-17
 *
 * https://github.com/mono/monodevelop/blob/master/main/src/core/MonoDevelop.Core/MonoDevelop.Projects.MSBuild/SlnFile.cs
 */
public class SlnPropertySet
{
	private Map<String, String> values = new TreeMap<>();

	public int Line;

	public boolean isMetadata;

	public SlnSection ParentSection;

	public boolean Processed;

	public String Id;

	public SlnPropertySet()
	{
	}

	public SlnPropertySet (boolean isMetadata)
	{
		this.isMetadata = isMetadata;
	}

	public SlnPropertySet(String id)
	{
		Id = id;
	}

	public void ReadLine(String line, int currentLine)
	{
		if(Line == 0)
		{
			Line = currentLine;
		}
		int k = line.indexOf('=');
		if(k != -1)
		{
			String name = line.substring(0, k).trim();
			String val = line.substring(k + 1).trim();
			values.put(name, val);
		}
		else
		{
			line = line.trim();
			if(!StringUtil.isEmptyOrSpaces(line))
			{
				values.put(line, null);
			}
		}
	}
}
