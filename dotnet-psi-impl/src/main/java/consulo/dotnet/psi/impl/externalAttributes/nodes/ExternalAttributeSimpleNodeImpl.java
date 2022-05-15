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

import consulo.dotnet.externalAttributes.ExternalAttributeNode;
import consulo.dotnet.externalAttributes.ExternalAttributeSimpleNode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 03.09.14
 */
public class ExternalAttributeSimpleNodeImpl implements ExternalAttributeSimpleNode
{
	private List<ExternalAttributeNode> myAttributes = new ArrayList<>();

	private final String myName;

	public ExternalAttributeSimpleNodeImpl(String name)
	{
		myName = name;
	}

	public void addAttribute(@Nonnull ExternalAttributeNode a)
	{
		myAttributes.add(a);
	}

	@Override
	@Nonnull
	public List<ExternalAttributeNode> getAttributes()
	{
		return myAttributes;
	}

	@Nonnull
	@Override
	public String getName()
	{
		return myName;
	}
}