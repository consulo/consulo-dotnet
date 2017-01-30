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

import java.util.ArrayList;

import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 30-Jan-17
 * <p>
 * https://github.com/mono/monodevelop/blob/master/main/src/core/MonoDevelop.Core/MonoDevelop.Projects.MSBuild/SlnFile.cs
 */
public class SlnSectionCollection extends ArrayList<SlnSection>
{
	private SlnFile myParentFile;

	public SlnFile getParentFile()
	{
		return myParentFile;
	}

	public SlnSection GetSection(String id)
	{
		return ContainerUtil.find(this, s -> s.Id.equals(id));
	}

	public SlnSection GetOrCreateSection(String id, SlnSectionType sectionType)
	{
		if(id == null)
		{
			throw new NullPointerException("id");
		}
		SlnSection sec = ContainerUtil.find(this, s -> s.Id.equals(id));
		if(sec == null)
		{
			sec = new SlnSection();
			sec.Id = id;
			sec.SectionType = sectionType;
			add(sec);
		}
		return sec;
	}

	public SlnSectionCollection setParentFile(SlnFile parentFile)
	{
		myParentFile = parentFile;
		for(SlnSection slnSection : this)
		{
			slnSection.setParentFile(parentFile);
		}
		return this;
	}
}
