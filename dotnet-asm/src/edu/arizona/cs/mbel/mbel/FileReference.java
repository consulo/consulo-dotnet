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

package edu.arizona.cs.mbel.mbel;

import edu.arizona.cs.mbel.signature.BaseCustomAttributeOwner;
import edu.arizona.cs.mbel.signature.FileAttributes;

/**
 * This class represents a File reference, and is analogous to a File metadata table.
 * Modules use these to refer to other modules in the same assembly, as well as
 * data for ManifestResources.
 *
 * @author Michael Stepp
 */
public class FileReference extends BaseCustomAttributeOwner implements FileAttributes
{
	private long FileRID = -1L;

	private long Flags;
	private String Name;          // filename, no path
	private byte[] HashValue;     // blob (cannot be null)

	/**
	 * Makes a FileReference with the given flags, filename, and hash value
	 *
	 * @param flags    a bit vector of flags (defined in FileAttributes)
	 * @param filename the name of the file (unqualified)
	 * @param hash     the hash value of the file
	 */
	public FileReference(long flags, String filename, byte[] hash)
	{
		Flags = flags;
		Name = filename;
		HashValue = hash;
	}

	/**
	 * Returns the File RID of this FileReference
	 */
	public long getFileRID()
	{
		return FileRID;
	}

	/**
	 * Sets the File RID for this FileReference
	 */
	public void setFileRID(long rid)
	{
		if(FileRID == -1L)
		{
			FileRID = rid;
		}
	}

	/**
	 * Returns a bit vector of flags for this file reference (defined in FileAttributes)
	 */
	public long getFlags()
	{
		return Flags;
	}

	/**
	 * Sets the flags for this file reference
	 */
	public void setFlags(long flags)
	{
		Flags = flags;
	}

	/**
	 * Returns the filename of this file
	 */
	public String getFileName()
	{
		return Name;
	}

	/**
	 * Sets the filename of this file
	 */
	public void setFileName(String filename)
	{
		Name = filename;
	}

	/**
	 * Returns the hash value of this file
	 */
	public byte[] getHashValue()
	{
		return HashValue;
	}

	/**
	 * Sets the hash value of this file
	 */
	public void setHashValue(byte[] hash)
	{
		HashValue = hash;
	}

	/**
	 * Compares 2 file references
	 * Returns true iff all the fields are equal
	 */
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof FileReference))
		{
			return false;
		}

		FileReference ref = (FileReference) o;
		if(Flags != ref.Flags || !Name.equals(ref.Name))
		{
			return false;
		}
		if(HashValue.length != ref.HashValue.length)
		{
			return false;
		}
		for(int i = 0; i < HashValue.length; i++)
		{
			if(HashValue[i] != ref.HashValue[i])
			{
				return false;
			}
		}
		return true;
	}
   
/*
   public void output(){
      System.out.print("FileReference[Filename=\"" + Name+"\"]");
   }
*/
}
