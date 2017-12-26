/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.emonic.base.documentation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public abstract class AbstractDocumentation implements IDocumentation
{

	static final String XML_SUMMARY_TAG = "summary"; //$NON-NLS-1$

	static final String XML_REMARKS_TAG = "remarks"; //$NON-NLS-1$

	static final String XML_SEE_TAG = "see"; //$NON-NLS-1$

	static final String XML_CREF_TAG = "cref"; //$NON-NLS-1$

	static final String XML_LANGWORD_TAG = "langword"; //$NON-NLS-1$

	static final String XML_VALUE_TAG = "value"; //$NON-NLS-1$

	static final String XML_RETURNS_TAG = "returns"; //$NON-NLS-1$

	static final String XML_PARAM_TAG = "param"; //$NON-NLS-1$

	static final String XML_PARAMREF_TAG = "paramref"; //$NON-NLS-1$

	static final String XML_EXCEPTION_TAG = "exception"; //$NON-NLS-1$

	static final String XML_SEEALSO_TAG = "seealso"; //$NON-NLS-1$

	static final String XML_PARA_TAG = "para"; //$NON-NLS-1$

	static final String XML_NAME_TAG = "name"; //$NON-NLS-1$

	static String trim(String text)
	{
		String[] split = text.trim().split("\\s"); //$NON-NLS-1$
		StringBuilder buffer = new StringBuilder();

		for(int i = 0; i < split.length; i++)
		{
			if(!split[i].equals(""))
			{ //$NON-NLS-1$
				buffer.append(split[i]).append(' ');
			}
		}
		return buffer.toString();

	}

	static String getText(NodeList childNodes)
	{
		int length = childNodes.getLength();
		if(length == 1)
		{
			Node node = childNodes.item(0);
			if(node instanceof Text)
			{
				Text text = (Text) node;
				return trim(text.getData());
			}
			else
			{
				return getText(node.getChildNodes());
			}
		}
		else
		{
			StringBuilder buffer = new StringBuilder();

			for(int j = 0; j < length; j++)
			{
				Node item = childNodes.item(j);
				if(item instanceof Text)
				{
					buffer.append(((Text) item).getData());
				}
				else
				{
					String name = item.getNodeName();
					if(name.equals(XML_SEE_TAG))
					{
						Element element = (Element) item;
						String attribute = element.getAttribute(XML_CREF_TAG);
						if(attribute.equals(""))
						{ //$NON-NLS-1$
							buffer.append(element.getAttribute(XML_LANGWORD_TAG));
						}
						else
						{
							buffer.append(attribute.substring(2));
						}
					}
					else if(name.equals(XML_PARAMREF_TAG))
					{
						Element element = (Element) item;
						buffer.append(element.getAttribute(XML_NAME_TAG));
					}
					else if(name.equals(XML_PARA_TAG))
					{
						buffer.append(getText(item.getChildNodes()));
					}
				}
			}
			return trim(buffer.toString());

		}
	}

	@Override
	public IDocumentation[] getExceptions()
	{
		return null;
	}

	@Override
	public IDocumentation[] getParams()
	{
		return null;
	}

	@Override
	public String getRemarks()
	{
		return null;
	}

	@Override
	public String getReturns()
	{
		return null;
	}

	@Override
	public String getValue()
	{
		return null;
	}

	@Override
	public String getSeeAlso()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}

