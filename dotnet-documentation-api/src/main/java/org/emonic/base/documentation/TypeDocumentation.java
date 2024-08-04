/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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

import jakarta.annotation.Nonnull;
import org.w3c.dom.Element;

public class TypeDocumentation extends Documentation implements ITypeDocumentation
{
	private final List<IDocumentation> myDocumentations;

	public TypeDocumentation(String name, Element element)
	{
		super(name, element);
		myDocumentations = new ArrayList<IDocumentation>();
	}

	public void add(IDocumentation doc)
	{
		myDocumentations.add(doc);
	}

	@Nonnull
	public List<IDocumentation> getDocumentation()
	{
		return myDocumentations;
	}
}
