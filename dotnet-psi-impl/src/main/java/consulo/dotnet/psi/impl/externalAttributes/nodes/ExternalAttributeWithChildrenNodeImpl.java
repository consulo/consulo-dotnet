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

package consulo.dotnet.psi.impl.externalAttributes.nodes;

import consulo.dotnet.externalAttributes.ExternalAttributeSimpleNode;
import consulo.dotnet.externalAttributes.ExternalAttributeWithChildrenNode;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 03.09.14
 */
public class ExternalAttributeWithChildrenNodeImpl extends ExternalAttributeSimpleNodeImpl implements ExternalAttributeWithChildrenNode
{
	private final List<ExternalAttributeSimpleNode> myChildren = new ArrayList<>();

	public ExternalAttributeWithChildrenNodeImpl(String qualifiedName)
	{
		super(qualifiedName);
	}

	public void addChild(@Nonnull ExternalAttributeSimpleNode methodNode)
	{
		myChildren.add(methodNode);
	}

	@Nullable
	public ExternalAttributeSimpleNode findByName(@Nonnull String name)
	{
		for(ExternalAttributeSimpleNode child : myChildren)
		{
			if(child.getName().equals(name))
			{
				return child;
			}
		}
		return null;
	}
	@Override
	@Nonnull
	public List<ExternalAttributeSimpleNode> getChildren()
	{
		return myChildren;
	}
}
