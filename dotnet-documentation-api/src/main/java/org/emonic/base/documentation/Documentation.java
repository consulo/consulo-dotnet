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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Documentation extends AbstractDocumentation
{

	private String name;

	private String summary;

	private String remarks;

	private String value;

	private String returns;

	private String seeAlso;

	private ParameterDocumentation[] params;

	private ExceptionDocumentation[] exceptions;

	public Documentation(String name, Node element)
	{
		this.name = name;

		if(element == null)
		{
			return;
		}

		NodeList children = element.getChildNodes();
		int length = children.getLength();
		List parameters = new ArrayList(length);
		List exceptionDeclarations = new ArrayList(length);

		for(int i = 0; i < length; i++)
		{
			Node node = children.item(i);
			if(!(node instanceof Element))
			{
				continue;
			}
			String nodeName = node.getNodeName();
			if(nodeName.equals(XML_SUMMARY_TAG))
			{
				summary = getText(node.getChildNodes());
			}
			else if(nodeName.equals(XML_REMARKS_TAG))
			{
				remarks = getText(node.getChildNodes());
			}
			else if(nodeName.equals(XML_VALUE_TAG))
			{
				value = getText(node.getChildNodes());
			}
			else if(nodeName.equals(XML_RETURNS_TAG))
			{
				returns = getText(node.getChildNodes());
			}
			else if(nodeName.equals(XML_PARAM_TAG))
			{
				parameters.add(new ParameterDocumentation((Element) node));
			}
			else if(nodeName.equals(XML_EXCEPTION_TAG))
			{
				exceptionDeclarations.add(new ExceptionDocumentation((Element) node));
			}
			else if(nodeName.equals(XML_SEEALSO_TAG))
			{
				seeAlso = getText(node.getChildNodes());
			}
		}

		params = (ParameterDocumentation[]) parameters.toArray(new ParameterDocumentation[parameters.size()]);
		exceptions = (ExceptionDocumentation[]) exceptionDeclarations.toArray(new ExceptionDocumentation[exceptionDeclarations.size()]);
	}

	public String getName()
	{
		return name;
	}

	public String getSummary()
	{
		return summary;
	}

	public String getValue()
	{
		return value;
	}

	public String getReturns()
	{
		return returns;
	}

	public IDocumentation[] getParams()
	{
		return params;
	}

	public IDocumentation[] getExceptions()
	{
		return exceptions;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public String getSeeAlso()
	{
		return seeAlso;
	}

}
