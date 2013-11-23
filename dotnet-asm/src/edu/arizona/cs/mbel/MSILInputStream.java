package edu.arizona.cs.mbel;
/* MBEL: The Microsoft Bytecode Engineering Library
 * Copyright (C) 2003 The University of Arizona
 * http://www.cs.arizona.edu/mbel/license.html
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


/**
 * This is an input stream that buffers the entire PE/COFF file in advance.
 * Since even the main library files (like mscorlib.dll) are no more than ~2mb,
 * this class should never have any problems with using too much memory.
 * All multi-byte integers are read little-endian.
 *
 * @author Michael Stepp
 */
public class MSILInputStream
{
	private edu.arizona.cs.mbel.parse.SectionHeader[] headers;
	private byte[] data;
	private int current;
	private int size;

	/**
	 * Creates a MSILInputStream from the given InputStream and buffers all the available data.
	 * Note: if input is a network stream, this constructor will only buffer the
	 * data that is available at the time the constructor is called (i.e. as much as is reported by
	 * InputStream.available()).
	 */
	public MSILInputStream(java.io.InputStream input) throws java.io.IOException
	{
		// reads in an entire input stream and buffers it
		current = 0;
		data = new byte[input.available()];
		size = input.read(data);
		input.close();
	}

	/**
	 * Moves the file pointer to the given location.
	 */
	public void seek(long pos) throws java.io.IOException
	{
		// skips to absolute location 'point' in the file
		if(pos < 0 || pos >= size)
		{
			throw new java.io.IOException("MSILInputStream.seek: Seek position outside of file bounds: " + pos);
		}

		current = (int) pos;
	}

	/**
	 * This method allows the MSILInputStream to computer file offsets from RVAs in the file.
	 * This method must be called before any calls to getFilePointer are made.
	 */
	public void activate(edu.arizona.cs.mbel.parse.SectionHeader[] hdrs)
	{
		headers = hdrs;
	}

	/**
	 * Returns the file pointer equivalent of the given RVA.
	 * This method cannot be called until activate is called.
	 */
	public long getFilePointer(long RVA)
	{
		for(int i = 0; i < headers.length; i++)
		{
			if(headers[i].VirtualAddress <= RVA && (headers[i].VirtualAddress + headers[i].SizeOfRawData) > RVA)
			{
				return ((RVA - headers[i].VirtualAddress) + headers[i].PointerToRawData);
			}
		}
		return -1L;
	}

	/**
	 * Reads a null-terminated ASCII string from the file starting
	 * at the current location and ending at the next 0x00 ('\0') byte.
	 */
	public String readASCII() throws java.io.IOException
	{
		String result = "";
		int BYTE;
		while((BYTE = readBYTE()) != 0)
		{
			result += (char) BYTE;
		}
		return result;
	}

	/**
	 * Returns the current file position of this input stream
	 */
	public long getCurrent() throws java.io.IOException
	{
		return (long) current;
	}

	/**
	 * Asserts that the next bytes to be read from the input stream equal the
	 * byte array given. If sucessful, returns true and advances the file pointer by
	 * bytes.length. If unsuccessful, will either throw an exception (if premature EOF)
	 * or will returns false. The file position after an unsuccessful match is undefined,
	 * but will be somewhere between start and (start+bytes.length).
	 */
	public boolean match(byte[] bytes) throws java.io.IOException
	{
		if((current + bytes.length) > size)
		{
			return false;
		}
		for(int i = 0; i < bytes.length; i++)
		{
			if(bytes[i] != data[current++])
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Advances the file pointer by 'amount'.
	 *
	 * @param amount the amount to skip, may be negative.
	 * @return true iff the skip was successful
	 */
	public boolean skip(int amount)
	{
		if((current + amount) > size || (current + amount) < 0)
		{
			return false;
		}
		current += amount;
		return true;
	}

	/**
	 * Moves the file pointer to the next higher multiple of 'a'.
	 *
	 * @param a the value to align to, must be positive
	 * @return true iff the align was successful
	 */
	public boolean align(int a)
	{
		// skips to the next multiple of 'a' bytes
		if(a <= 0)
		{
			return false;
		}
		if(a == 1)
		{
			return true;
		}
		int temp = current + (a - (current % a)) % a;
		if(temp > size)
		{
			return false;
		}
		current = temp;
		return true;
	}

	/**
	 * Reads from the current file pointer into the given array.
	 *
	 * @param bytes the array to read into. this will attempt to read bytes.length bytes from the file
	 */
	public void read(byte[] bytes) throws java.io.IOException
	{
		if(bytes == null)
		{
			return;
		}
		if((current + bytes.length) > size)
		{
			throw new java.io.IOException("BufferedMSILInputStream.read: Premature EOF");
		}

		for(int i = 0; i < bytes.length; i++)
		{
			bytes[i] = data[current++];
		}
	}

	/**
	 * Reads an unsigned byte from the file, returned in the lower 8 bits of an int.
	 * Advances the file pointer by 1.
	 */
	public int readBYTE() throws java.io.IOException
	{
		// throws java.io.IOException if EOF
		return ((int) (data[current++] & 0xFF)) & 0xFF;
	}

	/**
	 * Reads an unsigned 2-byte integer from the file, returned in the lower 2 bytes of an int
	 * Advances the file pointer by 2.
	 */
	public int readWORD() throws java.io.IOException
	{
		if(current + 1 >= size)
		{
			throw new java.io.IOException("MSILInputStream.readWORD: Premature EOF");
		}

		int b1 = readBYTE();
		int b2 = readBYTE();
		int result = (int) ((b1 & 0xFF) | ((b2 & 0xFF) << 8));
		return result;
	}

	/**
	 * Reads an unsigned 4-byte integer from the file and returns it the lower 4 bytes of a long.
	 * Advances the file pointer by 4.
	 */
	public long readDWORD() throws java.io.IOException
	{
		if(current + 3 >= size)
		{
			throw new java.io.IOException("MSILInputStream.readDWORD: Premature EOF");
		}
		int b1 = readBYTE();
		int b2 = readBYTE();
		int b3 = readBYTE();
		int b4 = readBYTE();

		long result = (long) ((b1 & 0xFFL) | ((b2 & 0xFFL) << 8) | ((b3 & 0xFFL) << 16) | ((b4 & 0xFFL) << 24));
		return result;
	}

	/**
	 * Reads an 8-byte (signed) quantity and returns it in a long.
	 * Advances the file pointer by 8.
	 */
	public long readDDWORD() throws java.io.IOException
	{
		// throws java.io.IOException if EOF
		if(current + 7 >= size)
		{
			throw new java.io.IOException("MSILInputStream.readDWORD: Premature EOF");
		}
		int b1 = readBYTE();
		int b2 = readBYTE();
		int b3 = readBYTE();
		int b4 = readBYTE();
		int b5 = readBYTE();
		int b6 = readBYTE();
		int b7 = readBYTE();
		int b8 = readBYTE();

		long result = (long) ((b1 & 0xFFl) | ((b2 & 0xFFl) << 8) | ((b3 & 0xFFl) << 16) | ((b4 & 0xFFl) << 24) | ((b5 & 0xFFl) << 32) | ((b6 & 0xFFl) <<
				40) | ((b7 & 0xFFl) << 48) | ((b8 & 0xFFl) << 56));
		return result;
	}
	/////////////////////////////

	/**
	 * Reads an int64 (signed) from the file and returns it in a long.
	 * Advances the file pointer by 8.
	 */
	public long readINT64() throws java.io.IOException
	{
		return readDDWORD();
	}

	/**
	 * Reads an int32 (signed) from the file and returns it in an int.
	 * Advances the file pointer by 4.
	 */
	public int readINT32() throws java.io.IOException
	{
		if(current + 3 >= size)
		{
			throw new java.io.IOException("MSILInputStream.readINT32: Premature EOF");
		}

		int b1 = readBYTE();
		int b2 = readBYTE();
		int b3 = readBYTE();
		int b4 = readBYTE();

		int temp = (b1 & 0xFF) | ((b2 & 0xFF) << 8) |
				((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
		return temp;
	}

	/**
	 * Reads an int16 (signed) from the file and returns it in an int.
	 * Advances the file pointer by 2.
	 */
	public int readINT16() throws java.io.IOException
	{
		short shorty = 0;
		if(current + 1 >= size)
		{
			throw new java.io.IOException("MSILInputStream.readINT16: Premature EOF");
		}
		int b1 = readBYTE();
		int b2 = readBYTE();

		shorty = (short) (((b1 & 0xFF) | (b2 & 0xFF) << 8) & 0xFFFF);
		return (int) shorty;
	}

	/**
	 * Reads an int8 (signed) from the file and returns it in an int.
	 * Advances the file pointer by 1.
	 */
	public int readINT8() throws java.io.IOException
	{
		if(current >= size)
		{
			throw new java.io.IOException("MSILInputStream.readINT8: Premature EOF");
		}
		return (int) data[current++];
	}

	/**
	 * Reads an r4 from the file and returns it in a float.
	 * Advances the file pointer by 4.
	 */
	public float readR4() throws java.io.IOException
	{
		return Float.intBitsToFloat((int) readDWORD());
	}

	/**
	 * Reads an r8 from the file and returns it in a double.
	 * Advances the file pointer by 8.
	 */
	public double readR8() throws java.io.IOException
	{
		return Double.longBitsToDouble(readDDWORD());
	}

	/**
	 * Reads an uint32 from the file and returns it in the lower 4 bytes of a long.
	 * Advances the file pointer by 4.
	 */
	public long readUINT32() throws java.io.IOException
	{
		return readDWORD();
	}

	/**
	 * Reads an uint16 from the file and returns it in the lower 2 bytes of an int.
	 * Advances the file pointer by 2.
	 */
	public int readUINT16() throws java.io.IOException
	{
		return readWORD();
	}

	/**
	 * Reads an uint8 from the file and returns it in the lower byte of an int.
	 * Advances the file pointer by 1.
	 */
	public int readUINT8() throws java.io.IOException
	{
		return readBYTE();
	}

	/**
	 * Reads a token from the file and returns it in a long.
	 * Advances the file pointer by 4.
	 */
	public long readTOKEN() throws java.io.IOException
	{
		return readDWORD();
	}

	/**
	 * Zeroes out the data starting at the current file pointer and extending 'length' bytes.
	 * Advances the file pointer by 'length', if successful.
	 *
	 * @param length the number of bytes to zero out, must be positive
	 */
	public void zero(long length) throws java.io.IOException
	{
		if((current + length) >= size || length < 0)
		{
			throw new java.io.IOException("MSILInputStream.zero: Invalid length parameter");
		}
		for(int i = 0; i < length; i++)
		{
			data[current] = 0;
			current++;
		}
	}
}
