/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.dotnet.dll.vfs.builder.util;

/**
 * @author VISTALL
 * @since 26.03.14
 */
public class XByteUtil
{
	public static int getShort(byte[] array)
	{
		int value = (array[0] & 0xFF);
		value |= (array[1] & 0xFF) << 8;
		return value;
	}

	public static char getChar(byte[] array)
	{
		int value = (array[0] & 0xFF);
		value |= (array[1] & 0xFF) << 8;
		value &= 0xFFFF;
		return (char) value;
	}

	public static int getInt(byte[] array)
	{
		int value = (array[0] & 0xFF);
		value |= (array[1] & 0xFF) << 8;
		value |= (array[2] & 0xFF) << 16;
		value |= (array[3] & 0xFF) << 24;
		return value;
	}

	public static long getLong(byte[] array)
	{
		long value = (array[0] & 0xFF);
		value |= (array[1] & 0xFFL) << 8;
		value |= (array[2] & 0xFFL) << 16;
		value |= (array[3] & 0xFFL) << 24;
		value |= (array[4] & 0xFFL) << 32;
		value |= (array[5] & 0xFFL) << 40;
		value |= (array[6] & 0xFFL) << 48;
		value |= (array[7] & 0xFFL) << 56;
		return value;
	}

	static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (((b3       ) << 24) |
				((b2 & 0xff) << 16) |
				((b1 & 0xff) <<  8) |
				((b0 & 0xff)      ));
	}
}
