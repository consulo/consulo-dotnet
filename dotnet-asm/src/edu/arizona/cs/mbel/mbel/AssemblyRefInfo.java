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

import edu.arizona.cs.mbel.signature.AssemblyFlags;
import edu.arizona.cs.mbel.signature.BaseCustomAttributeOwner;

/**
 * This class is simply a container for all the fields found in an AssemblyRef metadata table.
 * It is used by AssemblyTypeRef and AssemblyManifestResource, and directly maps to its own
 * metadata table upon emission. Also, it may contain CustomAttributes.
 *
 * @author Michael Stepp
 */
public class AssemblyRefInfo extends BaseCustomAttributeOwner implements AssemblyFlags, edu.arizona.cs.mbel.signature.Culture
{
	/**
	 * A pre-made assembly ref for mscorlib.dll, since it will be used often
	 */
	public static final AssemblyRefInfo MSCORLIB = new AssemblyRefInfo(1, 0, 3300, 0, 0, new byte[]{
			(byte) 0xB7,
			(byte) 0x7A,
			(byte) 0x5C,
			(byte) 0x56,
			(byte) 0x19,
			(byte) 0x34,
			(byte) 0xE0,
			(byte) 0x89
	}, "mscorlib", "", new byte[]{
			(byte) 0xEA,
			(byte) 0x8F,
			(byte) 0x4C,
			(byte) 0x9D,
			(byte) 0x50,
			(byte) 0x3E,
			(byte) 0x55,
			(byte) 0xB7,
			(byte) 0x21,
			(byte) 0x0A,
			(byte) 0x26,
			(byte) 0x34,
			(byte) 0xD3,
			(byte) 0x88,
			(byte) 0x8C,
			(byte) 0xC6,
			(byte) 0x29,
			(byte) 0xDC,
			(byte) 0xCD
	});

	private long AssemblyRefRID = -1L;

	private int MajorVersion, MinorVersion, BuildNumber, RevisionNumber;
	private long Flags;
	private byte[] PublicKeyOrToken; // blob
	private String Name;
	private String Culture;
	private byte[] HashValue;        // blob

	/**
	 * Makes an AssemblyRefInfo with the given information
	 *
	 * @param maj   the major version of the assembly
	 * @param min   the minro version of the assembly
	 * @param bn    the build number of the assembly
	 * @param rn    the revision number of the assembly
	 * @param flags a bit vector of flags for this assembly (defined in AssemblyFlags)
	 * @param pkey  the public key or token blob for this assembly
	 * @param name  the logical name of this assembly
	 * @param cult  the culture string for this assembly (defined in Culture.ValidCultures[])
	 */
	public AssemblyRefInfo(int maj, int min, int bn, int rn, long flags, byte[] pkey, String name, String cult, byte[] hash)
	{
		MajorVersion = maj;
		MinorVersion = min;
		BuildNumber = bn;
		RevisionNumber = rn;
		Flags = flags;
		PublicKeyOrToken = pkey;
		Name = name;
		Culture = cult;
		HashValue = hash;
	}

	/**
	 * Returns the AssemblyRef RID for this AssemblyRefInfo (used by emitter)
	 */
	public long getAssemblyRefRID()
	{
		return AssemblyRefRID;
	}

	/**
	 * Sets the AssemblyRef RID for this AssemblyRefInfo (AssemblyRef)
	 */
	public void setAssemblyRefRID(long rid)
	{
		if(AssemblyRefRID == -1L)
		{
			AssemblyRefRID = rid;
		}
	}

	/**
	 * Returns the major version of this assembly
	 */
	public int getMajorVersion()
	{
		return MajorVersion;
	}

	/**
	 * Sets the major version of this assembly
	 */
	public void setMajorVersion(int maj)
	{
		MajorVersion = maj;
	}

	/**
	 * Returns the minor version of this assembly
	 */
	public int getMinorVersion()
	{
		return MinorVersion;
	}

	/**
	 * Sets the minor version of this assembly
	 */
	public void setMinorVersion(int min)
	{
		MinorVersion = min;
	}

	/**
	 * Returns the build number of this assembly
	 */
	public int getBuildNumber()
	{
		return BuildNumber;
	}

	/**
	 * Sets the build number of this assembly
	 */
	public void setBuildNumber(int bn)
	{
		BuildNumber = bn;
	}

	/**
	 * Returns the revision number of this assembly
	 */
	public int getRevisionNumber()
	{
		return RevisionNumber;
	}

	/**
	 * Sets the revision number of this assembly
	 */
	public void setRevisionNumber(int rn)
	{
		RevisionNumber = rn;
	}

	/**
	 * Returns the bit vector of flags for this assembly (defined in AssemblyFlags)
	 */
	public long getFlags()
	{
		return Flags;
	}

	/**
	 * Sets the flags for this assembly
	 */
	public void setFlags(long flags)
	{
		Flags = flags;
	}

	/**
	 * Returns the public key or token blob for this assembly
	 */
	public byte[] getPublicKeyOrToken()
	{
		return PublicKeyOrToken;
	}

	/**
	 * Sets the public key or token blob for this assembly
	 */
	public void setPublicKeyOrToken(byte[] pkey)
	{
		PublicKeyOrToken = pkey;
	}

	/**
	 * Returns the logical name of this assembly
	 */
	public String getName()
	{
		return Name;
	}

	/**
	 * Sets the logical name of this assembly
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * Returns the culture string for this assembly
	 */
	public String getCulture()
	{
		return Culture;
	}

	/**
	 * Sets the culture string for this assembly
	 */
	public void setCulture(String cult)
	{
		Culture = cult;
	}

	/**
	 * Returns the hashvalue for this assembly
	 */
	public byte[] getHashValue()
	{
		return HashValue;
	}

	/**
	 * Sets the hash value for this assembly
	 */
	public void setHashValue(byte[] hash)
	{
		HashValue = hash;
	}

	/**
	 * Compares 2 AssemblyRefInfos.
	 * Returns true iff all the fields are equal (all those given in constructor)
	 */
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof AssemblyRefInfo))
		{
			return false;
		}

		AssemblyRefInfo ref = (AssemblyRefInfo) o;

		if(MajorVersion != ref.MajorVersion ||
				MinorVersion != ref.MinorVersion ||
				BuildNumber != ref.BuildNumber ||
				RevisionNumber != ref.RevisionNumber ||
				Flags != ref.Flags ||
				!Name.equals(ref.Name))
		{
			return false;
		}

		if(PublicKeyOrToken.length != ref.PublicKeyOrToken.length || HashValue.length != ref.HashValue.length)
		{
			return false;
		}
		for(int i = 0; i < PublicKeyOrToken.length; i++)
		{
			if(PublicKeyOrToken[i] != ref.PublicKeyOrToken[i])
			{
				return false;
			}
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
}
