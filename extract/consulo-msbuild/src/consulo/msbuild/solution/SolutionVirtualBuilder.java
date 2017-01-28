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

package consulo.msbuild.solution;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xml.GenericAttributeValue;
import consulo.msbuild.dom.ItemGroup;
import consulo.msbuild.dom.Project;
import consulo.msbuild.dom.SimpleItem;

/**
 * @author VISTALL
 * @since 29-Jan-17
 */
public class SolutionVirtualBuilder
{
	public static SolutionVirtualDirectory build(@NotNull Project domProject, @NotNull VirtualFile baseDir)
	{
		SolutionVirtualDirectory root = new SolutionVirtualDirectory("", null);

		for(ItemGroup group : domProject.getItemGroups())
		{
			addAll(group.getCompiles(), baseDir, root);
			addAll(group.getNones(), baseDir, root);
			addAll(group.getResourceCompiles(), baseDir, root);
			addAll(group.getEmbeddedResources(), baseDir, root);
			addAll(group.getItems(), baseDir, root);
		}

		return root;
	}

	private static void addAll(List<? extends SimpleItem> list, @NotNull VirtualFile baseDir, @NotNull SolutionVirtualDirectory root)
	{
		for(SimpleItem simpleItem : list)
		{
			GenericAttributeValue<String> attributeValue = simpleItem.getInclude();

			String value = attributeValue.getStringValue();
			if(value == null)
			{
				continue;
			}

			String linkValue = simpleItem.getLink().getStringValue();

			String presentationPath = linkValue == null ? value : linkValue;

			List<String> split = StringUtil.split(presentationPath, "\\");

			SolutionVirtualDirectory target = root;
			for(int i = 0; i < (split.size() - 1); i++)
			{
				target = target.createOrGetDirectory(split.get(i));
			}

			VirtualFile file = baseDir.findFileByRelativePath(FileUtilRt.toSystemIndependentName(value));

			String name = ContainerUtil.getLastItem(split);
			assert name != null;

			SolutionVirtualFile virtualFile = new SolutionVirtualFile(name, target, null, file);

			target.myChildren.put(name, virtualFile);

			String autoGenValue = simpleItem.getAutoGen().getStringValue();
			virtualFile.setGenerated(Comparing.equal(autoGenValue, "True"));

			String stringValue = simpleItem.getSubType().getStringValue();
			virtualFile.setSubType(stringValue == null ? null : SolutionVirtualFileSubType.find(stringValue));

			String dependentUpon = simpleItem.getDependentUpon().getStringValue();
			virtualFile.setDependentUpon(dependentUpon);

			String generator = simpleItem.getGenerator().getStringValue();
			if(generator != null)
			{
				virtualFile.setGenerator(generator);
				SolutionVirtualFileSubType subType = virtualFile.getSubType();
				if(subType == null)
				{
					virtualFile.setSubType(SolutionVirtualFileSubType.__generator);
				}
			}
		}
	}
}
