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

import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

class DataInputImpl implements DataInput
{
	private RandomAccessFile file;

	DataInputImpl(File sourceFile) throws FileNotFoundException
	{
		file = new RandomAccessFile(sourceFile, "r"); //$NON-NLS-1$
	}

	void close() throws IOException
	{
		file.close();
	}

	int read(byte[] b) throws IOException
	{
		return file.read(b);
	}

	@Override
	public final boolean readBoolean() throws IOException
	{
		return file.readBoolean();
	}

	@Override
	public final byte readByte() throws IOException
	{
		return file.readByte();
	}

	@Override
	public final char readChar() throws IOException
	{
		return file.readChar();
	}

	@Override
	public final double readDouble() throws IOException
	{
		return file.readDouble();
	}

	@Override
	public final float readFloat() throws IOException
	{
		return file.readFloat();
	}

	@Override
	public final void readFully(byte[] b, int off, int len) throws IOException
	{
		file.readFully(b, off, len);
	}

	@Override
	public final void readFully(byte[] b) throws IOException
	{
		file.readFully(b);
	}

	@Override
	public final int readInt() throws IOException
	{
		int ch1 = file.read();
		int ch2 = file.read();
		int ch3 = file.read();
		int ch4 = file.read();
		if((ch1 | ch2 | ch3 | ch4) < 0)
		{
			throw new EOFException();
		}
		return ((ch1 << 0) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
	}

	@Override
	public final String readLine() throws IOException
	{
		return file.readLine();
	}

	@Override
	public final long readLong() throws IOException
	{
		return file.readLong();
	}

	@Override
	public final short readShort() throws IOException
	{
		return file.readShort();
	}

	@Override
	public final int readUnsignedByte() throws IOException
	{
		return file.readUnsignedByte();
	}

	@Override
	public final int readUnsignedShort() throws IOException
	{
		int ch1 = file.read();
		int ch2 = file.read();
		if((ch1 | ch2) < 0)
		{
			throw new EOFException();
		}
		return (ch1 << 0) + (ch2 << 8);
	}

	int read7BitEncodedInt() throws IOException
	{
		int ret = 0;
		int shift = 0;
		byte b;

		do
		{
			b = readByte();

			ret = ret | ((b & 0x7f) << shift);
			shift += 7;
		}
		while((b & 0x80) == 0x80);

		return ret;
	}

	@Override
	public final String readUTF() throws IOException
	{
		int utflen = read7BitEncodedInt();
		byte[] bytearr = new byte[utflen];
		int read = file.read(bytearr);
		return new String(bytearr, 0, read, "UTF-8"); //$NON-NLS-1$
	}

	void seek(long pos) throws IOException
	{
		file.seek(pos);
	}

	@Override
	public int skipBytes(int n) throws IOException
	{
		return file.skipBytes(n);
	}

}
