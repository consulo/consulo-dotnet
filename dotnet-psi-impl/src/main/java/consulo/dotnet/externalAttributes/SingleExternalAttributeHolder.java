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

package consulo.dotnet.externalAttributes;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.dotnet.externalAttributes.nodes.ExternalAttributeNodeImpl;
import consulo.dotnet.externalAttributes.nodes.ExternalAttributeSimpleNodeImpl;
import consulo.dotnet.externalAttributes.nodes.ExternalAttributeWithChildrenNodeImpl;
import gnu.trove.THashMap;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

/**
 * @author VISTALL
 * @since 02.09.14
 */
public class SingleExternalAttributeHolder implements ExternalAttributeHolder
{
	private static final Logger LOG = Logger.getInstance(SingleExternalAttributeHolder.class);

	@Nonnull
	public static ExternalAttributeHolder load(VirtualFile file)
	{
		if(file.isDirectory())
		{
			return EMPTY;
		}

		try
		{
			Document document = JDOMUtil.loadDocument(file.getInputStream());

			SingleExternalAttributeHolder holder = new SingleExternalAttributeHolder();

			for(Element element : document.getRootElement().getChildren("class"))
			{
				String name = element.getAttributeValue("name");

				ExternalAttributeWithChildrenNodeImpl classNode = (ExternalAttributeWithChildrenNodeImpl) holder.myClasses.get(name);
				if(classNode == null)
				{
					holder.myClasses.put(name, classNode = new ExternalAttributeWithChildrenNodeImpl(name));
				}

				readAttributes(element, classNode);

				for(Element classChildren : element.getChildren())
				{
					String classChildrenName = classChildren.getName();
					if("method".equals(classChildrenName))
					{
						ExternalAttributeWithChildrenNodeImpl methodNode = new ExternalAttributeWithChildrenNodeImpl(classChildren.getAttributeValue("name"));
						readAttributes(classChildren, methodNode);

						for(Element methodChild : classChildren.getChildren())
						{
							ExternalAttributeSimpleNodeImpl parameterNode = new ExternalAttributeSimpleNodeImpl(methodChild.getAttributeValue("type"));
							readAttributes(methodChild, parameterNode);
							methodNode.addChild(parameterNode);
						}
						classNode.addChild(methodNode);
					}
				}
			}
			return holder;
		}
		catch(JDOMException | IOException e)
		{
			LOG.error(e);
		}
		return EMPTY;
	}

	private static void readAttributes(Element element, ExternalAttributeSimpleNodeImpl owner)
	{
		for(Element att : element.getChildren("attribute"))
		{
			ExternalAttributeNodeImpl attributeNode = new ExternalAttributeNodeImpl(att.getAttributeValue("name"));
			owner.addAttribute(attributeNode);

			for(Element attChild : att.getChildren())
			{
				String attChildName = attChild.getName();
				if("argument".equals(attChildName))
				{
					attributeNode.addArgument(new ExternalAttributeArgumentNode(attChild.getAttributeValue("type"), attChild.getTextTrim()));
				}
			}
		}
	}

	private Map<String, ExternalAttributeWithChildrenNode> myClasses = new THashMap<>();

	@Nullable
	@Override
	public ExternalAttributeWithChildrenNode findClassNode(@Nonnull String qname)
	{
		return myClasses.get(qname);
	}
}
