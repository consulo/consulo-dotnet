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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
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
 * @since 28-Jan-17
 */
public class SolutionVirtualDirectory extends SolutionVirtualItem
{
	public static SolutionVirtualDirectory get(Project p, @NotNull VirtualFile baseDir)
	{
		SolutionVirtualDirectory root = new SolutionVirtualDirectory("", null);

		for(ItemGroup group : p.getItemGroups())
		{
			addAll(group.getCompiles(), baseDir, root);
			addAll(group.getNones(), baseDir, root);
			addAll(group.getResourceCompiles(), baseDir, root);
			addAll(group.getItems(), baseDir, root);
		}

		return root;
	}

	private Map<String, SolutionVirtualItem> myChildren = new TreeMap<>();

	public SolutionVirtualDirectory(String name, SolutionVirtualDirectory parent)
	{
		super(name, parent, null);
	}

	@NotNull
	public Collection<SolutionVirtualItem> getChildren()
	{
		return myChildren.values();
	}

	@NotNull
	public SolutionVirtualDirectory createOrGetDirectory(@NotNull String name)
	{
		return (SolutionVirtualDirectory) myChildren.computeIfAbsent(name, s -> new SolutionVirtualDirectory(name, this));
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

			List<String> split = StringUtil.split(value, "\\");

			SolutionVirtualDirectory target = root;
			for(int i = 0; i < (split.size() - 1); i++)
			{
				target = target.createOrGetDirectory(split.get(i));
			}

			VirtualFile file = baseDir.findFileByRelativePath(FileUtilRt.toSystemIndependentName(value));

			String name = ContainerUtil.getLastItem(split);
			assert name != null;
			target.myChildren.put(name, new SolutionVirtualFile(name, target, null, file));
		}
	}
}
