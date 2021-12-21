/*
 * Copyright 2013-2014 must-be.org
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

package consulo.dotnet.externalAttributes.nodes;

import consulo.dotnet.externalAttributes.ExternalAttributeNode;
import consulo.dotnet.externalAttributes.ExternalAttributeSimpleNode;
import consulo.dotnet.externalAttributes.ExternalAttributeWithChildrenNode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 03.09.14
 */
public class ExternalAttributeWithChildrenCompositeNodeImpl implements ExternalAttributeWithChildrenNode
{
	private final List<ExternalAttributeWithChildrenNode> myNodes;

	public ExternalAttributeWithChildrenCompositeNodeImpl(List<ExternalAttributeWithChildrenNode> nodes)
	{
		myNodes = nodes;
	}

	@Nonnull
	@Override
	public List<ExternalAttributeSimpleNode> getChildren()
	{
		List<ExternalAttributeSimpleNode> list = new ArrayList<>();
		for(ExternalAttributeWithChildrenNode node : myNodes)
		{
			list.addAll(node.getChildren());
		}
		return list;
	}

	@Nonnull
	@Override
	public List<ExternalAttributeNode> getAttributes()
	{
		List<ExternalAttributeNode> list = new ArrayList<>();
		for(ExternalAttributeWithChildrenNode node : myNodes)
		{
			list.addAll(node.getAttributes());
		}
		return list;
	}

	@Nonnull
	@Override
	public String getName()
	{
		return myNodes.get(0).getName();
	}
}
