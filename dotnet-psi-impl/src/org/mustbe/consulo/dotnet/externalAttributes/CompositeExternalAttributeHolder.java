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

package org.mustbe.consulo.dotnet.externalAttributes;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 02.09.14
 */
public class CompositeExternalAttributeHolder implements ExternalAttributeHolder
{
	private List<ExternalAttributeHolder> myHolders;

	public CompositeExternalAttributeHolder(@NotNull List<ExternalAttributeHolder> holders)
	{
		myHolders = holders;
	}

	@Nullable
	@Override
	public ExternalAttributeCompositeNode findClassNode(@NotNull String qname)
	{
		for(ExternalAttributeHolder holder : myHolders)
		{
			ExternalAttributeCompositeNode classNode = holder.findClassNode(qname);
			if(classNode != null)
			{
				return classNode;
			}
		}
		return null;
	}
}
