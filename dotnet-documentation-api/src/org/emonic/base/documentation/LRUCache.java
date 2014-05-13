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

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V>
{

	private static final long serialVersionUID = 2738512722695892535L;

	private final int maxSize;

	public LRUCache()
	{
		this(100);
	}

	public LRUCache(int maxSize)
	{
		super();
		this.maxSize = maxSize;
	}

	protected boolean removeEldestEntry(Map.Entry eldest)
	{
		return size() > maxSize;
	}
}
