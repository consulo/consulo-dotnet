/*******************************************************************************
 * Copyright (c) 2002, 2008 Ximian, Inc., and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT license which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Contributors:
 *     Miguel de Icaza <miguel@novell.com> - initial API and implementation
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - adapted from C# to Java 
 ******************************************************************************/
package org.emonic.monodoc;

import java.io.IOException;

class MonodocNode
{
	protected MonodocTree tree;
	protected int position;
	protected MonodocNode[] nodes;

	private String element;

	MonodocNode()
	{

	}

	MonodocNode(MonodocNode parent, int address) throws IOException
	{
		position = address;
		this.tree = parent.tree;
		if(address > 0)
		{
			loadNode();
		}
	}

	MonodocNode[] getNodes()
	{
		if(position < 0)
		{
			try
			{
				loadNode();
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nodes;
	}

	String getElement()
	{
		if(position < 0)
		{
			try
			{
				loadNode();
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return element;
	}

	public void loadNode() throws IOException
	{
		if(position < 0)
		{
			position = -position;
		}

		DataInputImpl file = tree.getFile();
		file.seek(position);
		int count = decodeInt(file);

		element = file.readUTF();
		file.readUTF();

		if(count == 0)
		{
			return;
		}

		nodes = new MonodocNode[count];
		for(int i = 0; i < count; i++)
		{
			int child_address = decodeInt(file);

			nodes[i] = new MonodocNode(this, -child_address);
		}
	}

	private static int decodeInt(DataInputImpl reader) throws IOException
	{
		int ret = 0;
		int shift = 0;
		byte b;

		do
		{
			b = reader.readByte();

			ret = ret | ((b & 0x7f) << shift);
			shift += 7;
		}
		while((b & 0x80) == 0x80);

		return ret;
	}
}
