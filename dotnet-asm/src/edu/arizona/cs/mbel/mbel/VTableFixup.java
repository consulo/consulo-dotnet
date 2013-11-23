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

import java.io.IOException;

import edu.arizona.cs.mbel.MSILInputStream;
import edu.arizona.cs.mbel.metadata.TableConstants;

/**
 * This class holds the VTable fixups for a module. VTable fixups are only needed by native code,
 * and since MBEL is not designed to deal with native code (or rather, specifically designed NOT to
 * deal with native code), this class is immutable once constructed. The emitter rewrites the VTableFixups
 * in exactly the same place they were parsed from. The only reason to parse it in the first place is that
 * it contains method tokens which may change as a result of MBEL manipulations. Thus we store references
 * to those methods, then upon emission we simply ask those references what their new RIDs are and write those out.
 *
 * @author Michael Stepp
 */
public class VTableFixup
{
	public static final int STRUCT_SIZE = 8;
	public static final int COR_VTABLE_32BIT = 0x01;
	public static final int COR_VTABLE_64BIT = 0x02;
	public static final int COR_VTABLE_FROM_UNMANAGED = 0x04;
	public static final int COR_VTABLE_CALL_MOST_DERIVED = 0x10;

	private long RVA;
	private int Type;                // 2 bytes (flags)
	private MethodDef[] fixups; // Methods

	/**
	 * Parses a VTableFixups object from a ClassParser
	 */
	protected VTableFixup(ModuleParser parser) throws IOException
	{
		MSILInputStream in = parser.getMSILInputStream();

		RVA = in.readDWORD();
		int Count = in.readWORD();
		Type = in.readWORD();

		fixups = new MethodDef[Count];

		if((Type & COR_VTABLE_32BIT) != 0)
		{
			// each slot is 32 bits
			in.seek(in.getFilePointer(RVA));
			long token;
			for(int i = 0; i < Count; i++)
			{
				token = in.readDWORD();
				fixups[i] = parser.getVTableFixup(token);
			}
		}
		else
		{
			// each slot is 64 bits (top 4 bytes are 0)
			in.seek(in.getFilePointer(RVA));
			long token;
			for(int i = 0; i < Count; i++)
			{
				token = in.readDDWORD();
				fixups[i] = parser.getVTableFixup(token);
			}
		}
	}

	/**
	 * Returns the method references for all the RIDs in the VTable fixups
	 */
	public MethodDef[] getFixups()
	{
		return fixups;
	}

	/**
	 * Returns a flag integer (values defined in this class: COR_*)
	 */
	public int getType()
	{
		return Type;
	}

	/**
	 * Returns the RVA of the VTable.
	 * This must be kept because it will not change.
	 */
	public long getRVA()
	{
		return RVA;
	}

	/**
	 * Tests one of the flags in getType;
	 * Returns true iff getType() & COR_VTABLE_64BIT
	 */
	public boolean is64Bit()
	{
		return ((Type & COR_VTABLE_64BIT) != 0);
	}

	/**
	 * Returns a byte array that is the representation of the vtable on disk
	 * (can be emitted straight into the output file)
	 */
	public byte[] toByteArray()
	{
		// emits the VTable entries contiguously as a byte array
		if(is64Bit())
		{
			byte[] data = new byte[fixups.length * 8];
			int index = 0;
			for(MethodDef fixup : fixups)
			{
				long token = fixup.getMethodRID() | ((long) (TableConstants.Method)) << 24;
				token &= 0xFFFFFFFFL;
				data[index + 0] = (byte) (token & 0xFF);
				data[index + 1] = (byte) ((token >> 8) & 0xFF);
				data[index + 2] = (byte) ((token >> 16) & 0xFF);
				data[index + 3] = (byte) ((token >> 24) & 0xFF);
				data[index + 4] = 0;
				data[index + 5] = 0;
				data[index + 6] = 0;
				data[index + 7] = 0;
				index += 8;
			}
			return data;
		}
		else
		{
			byte[] data = new byte[fixups.length * 4];
			int index = 0;
			for(MethodDef fixup : fixups)
			{
				long token = fixup.getMethodRID() | ((long) (TableConstants.Method)) << 24;
				token &= 0xFFFFFFFFL;
				data[index + 0] = (byte) (token & 0xFF);
				data[index + 1] = (byte) ((token >> 8) & 0xFF);
				data[index + 2] = (byte) ((token >> 16) & 0xFF);
				data[index + 3] = (byte) ((token >> 24) & 0xFF);
				index += 4;
			}
			return data;
		}
	}
   
/*
   public void output(){
      System.out.print("VTableFixup:{");
      System.out.print("\n  Type = " + Long.toBinaryString(Type));
      System.out.print("\n  Fixups[" + fixups.length + "] = {");
      for (int i=0;i<fixups.length;i++){
         System.out.print("\n    ");
         fixups[i].output();
      }
      System.out.print("\n  }\n}");
   }
*/
}

