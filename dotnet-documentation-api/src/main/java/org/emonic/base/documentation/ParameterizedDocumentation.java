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

class ParameterizedDocumentation extends AbstractDocumentation
{
	protected String name;

	private String summary;

	ParameterizedDocumentation(Element element)
	{
		summary = getText(element.getChildNodes());
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getSummary()
	{
		return summary;
	}
}
