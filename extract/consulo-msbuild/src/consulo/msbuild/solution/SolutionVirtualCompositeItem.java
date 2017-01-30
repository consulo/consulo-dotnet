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
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.xml.DomElement;

/**
 * @author VISTALL
 * @since 29-Jan-17
 */
public class SolutionVirtualCompositeItem extends SolutionVirtualItem
{
	protected Map<String, SolutionVirtualItem> myChildren = new TreeMap<>();

	public SolutionVirtualCompositeItem(@NotNull String name, @Nullable SolutionVirtualDirectory parent, @Nullable DomElement element)
	{
		super(name, parent, element);
	}

	@NotNull
	public Collection<SolutionVirtualItem> getChildren()
	{
		return myChildren.values();
	}

	@Override
	public boolean visitRecursive(@NotNull Predicate<SolutionVirtualItem> processor)
	{
		if(!processor.test(this))
		{
			return false;
		}

		for(SolutionVirtualItem item : myChildren.values())
		{
			if(!item.visitRecursive(processor))
			{
				return false;
			}
		}
		return true;
	}
}
