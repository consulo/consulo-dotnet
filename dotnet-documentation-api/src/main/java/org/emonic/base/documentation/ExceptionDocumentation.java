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

class ExceptionDocumentation extends ParameterizedDocumentation
{

	ExceptionDocumentation(Element element)
	{
		super(element);
		name = element.getAttribute(XML_CREF_TAG).substring(2);
	}
}
