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
package consulo.dotnet.documentation.impl;

import jakarta.annotation.Nonnull;
import org.emonic.base.documentation.IDocumentation;
import org.emonic.base.documentation.ITypeDocumentation;

import java.util.ArrayList;
import java.util.List;

final class XMLTypeDocumentation extends XMLDocumentation implements ITypeDocumentation
{
	private List<IDocumentation> myMemberDocumentation;

	XMLTypeDocumentation(String name)
	{
		super(name);
		myMemberDocumentation = new ArrayList<IDocumentation>();
	}

	void add(IDocumentation documentation)
	{
		myMemberDocumentation.add(documentation);
	}

	@Nonnull
	@Override
	public List<IDocumentation> getDocumentation()
	{
		return myMemberDocumentation;
	}
}
