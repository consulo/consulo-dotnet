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

package edu.arizona.cs.mbel.metadata;

/**
 * This exception is thrown whenever an error is found in terms of valid metadata.
 * This will mostly be used in the validator, upon emitting a module back to a file.
 *
 * @author Michael Stepp
 */
public class MetadataValidationException extends Exception
{
	public static final int ERROR = 0;
	public static final int WARNING = 1;
	public static final int CLS = 2;

	private int myType;
	private String message;

	/**
	 * Makes an exception with the given message and error type.
	 *
	 * @param mesg the message to print
	 * @param type the error type (one of ERROR, WARNING, CLS)
	 */
	public MetadataValidationException(String mesg, int type)
	{
		message = mesg;
		myType = type;
	}

	/**
	 * Returns the error type
	 */
	public int getType()
	{
		return myType;
	}

	public String toString()
	{
		String[] types = {
				"[ERROR]",
				"[WARNING]",
				"[CLS]"
		};
		String result = "MetadataValidationException: " + message + " " + types[myType];
		return result;
	}
}
