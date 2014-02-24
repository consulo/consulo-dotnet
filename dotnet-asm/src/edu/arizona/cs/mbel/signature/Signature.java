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


package edu.arizona.cs.mbel.signature;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.emit.ClassEmitter;

/**
 * Superclass of all signature types. This inheritance hierarchy is just for
 * convenience, to supply useful state and behavior to each signature class
 *
 * @author Michael Stepp
 */
abstract class Signature implements SignatureConstants
{
	/**
	 * Write this signature in binary format to the given buffer
	 *
	 * @param buffer the buffer to write to
	 */
	public abstract void emit(ByteBuffer buffer, ClassEmitter emitter);

	/**
	 * Parses a PackedLen coded integer from the given buffer
	 *
	 * @param buffer the buffer
	 * @return the decoded integer value
	 */
	public static int readCodedInteger(ByteBuffer buffer)
	{
		int result = 0;
		byte data1 = buffer.get();
		if((data1 & 0xE0) == 0xC0)
		{
			// 4 bytes
			byte data2 = buffer.get();
			byte data3 = buffer.get();
			byte data4 = buffer.get();
			result = ((data1 & 0x1F) << 24) | ((data2 & 0xFF) << 16) | ((data3 & 0xFF) << 8) | (data4 & 0xFF);
			return result;
		}
		else if((data1 & 0xC0) == 0x80)
		{
			// 2 bytes
			byte data2 = buffer.get();
			result = ((data1 & 0x3F) << 8) | (data2 & 0xFF);
			return result;
		}
		else
		{
			// 1 byte
			return (data1 & 0xFF);
		}
	}

	/**
	 * Reads a TypeDefOrRefEncoded blob from the given buffer, and returns an int array
	 * in the form of {table number, row number}
	 *
	 * @param buffer the buffer to read from
	 * @return an int array with {table num, row num}
	 */
	public static int[] parseTypeDefOrRefEncoded(ByteBuffer buffer)
	{
		// returns {table, row} tuple
		int[] ROWS = {
				0x02,
				0x01,
				0x1B
		};

		int[] token = new int[2];
		int result = readCodedInteger(buffer);

		token[0] = ROWS[result & 0x3]; // this is the actual table number now
		token[1] = result >> 2;

		return token;
	}

	/**
	 * Turns a {table num, row num} into a TypeDefOrRefEncoded integer value
	 *
	 * @param table the table number (i.e. one of TypeDef, TypeRef, TypeSpec)
	 * @param row   the row number within the given table (1-based index)
	 * @return the raw binary TypeDefOrRefEncoded blob
	 */
	public static byte[] makeTypeDefOrRefEncoded(int table, int row)
	{
		switch(table)
		{
			case 0x02:
				table = 0;
				break;
			case 0x01:
				table = 1;
				break;
			case 0x1B:
				table = 2;
				break;
			default:
				return null;
		}
		int token = (table | (row << 2));
		return encodeInteger(token);
	}

	/**
	 * Converts an unsigned integer to its PackedLen integer equivalent
	 *
	 * @param value the value to convert to PackedLen form
	 * @return the raw binary PackedLen blob corresponding to value
	 */
	public static byte[] encodeInteger(long value)
	{
		if(value < 0) // if value too high
		{
			return null;
		}

		if(value <= 0x7FL)
		{
			// 1 byte
			return new byte[]{(byte) (value & 0x7F)};
		}
		else if(value <= 0x3FFFL)
		{
			// 2 bytes
			return new byte[]{
					(byte) (0x80 | ((value >> 8) & 0x3F)),
					(byte) (value & 0xFF)
			};
		}
		else if(value <= 0x1FFFFFFFL)
		{
			// 4 bytes
			byte[] result = new byte[4];
			result[0] = (byte) (0xC0 | ((value >> 24) & 0x1F));
			result[1] = (byte) ((value >> 16) & 0xFF);
			result[2] = (byte) ((value >> 8) & 0xFF);
			result[3] = (byte) (value & 0xFF);
			return result;
		}
		else
		{
			return null;
		}
	}
}
