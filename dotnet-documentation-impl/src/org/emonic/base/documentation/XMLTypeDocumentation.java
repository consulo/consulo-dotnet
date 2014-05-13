/*******************************************************************************
 * Copyright (c) 2008 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.emonic.base.documentation;

import java.util.ArrayList;
import java.util.List;

final class XMLTypeDocumentation extends XMLDocumentation implements ITypeDocumentation
{

	private List memberDocumentation;

	XMLTypeDocumentation(String name)
	{
		super(name);
		memberDocumentation = new ArrayList();
	}

	void add(IDocumentation documentation)
	{
		memberDocumentation.add(documentation);
	}

	public List getDocumentation()
	{
		return memberDocumentation;
	}

}
