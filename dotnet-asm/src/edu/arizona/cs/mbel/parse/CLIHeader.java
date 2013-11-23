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


package edu.arizona.cs.mbel.parse;

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


/*
   Should be in a read only, sharable section
*/

/**
 * This class holds the data for the CLI header of a .NET module. This
 * will be pointed to by the CLI header entry of the DataDirectory table.
 * This structre will always be 72 bytes large
 *
 * @author Michael Stepp
 */
public class CLIHeader
{
	public static final int COMIMAGE_FLAGS_ILONLY = 0x00000001;
	public static final int COMIMAGE_FLAGS_32BITREQUIRED = 0x00000002;
	public static final int COMIMAGE_FLAGS_IL_LIBRARY = 0x00000004;
	public static final int COMIMAGE_FLAGS_STRONGNAMESIGNED = 0x00000008;
	public static final int COMIMAGE_FLAGS_TRACKDEBUGDATA = 0x00010000;
	// constants for the 'Flags' field

	public long HeaderSize;                            // 4 bytes (==size of CLIHeader)
	public int MajorRuntimeVersion;                    // 2 bytes
	public int MinorRuntimeVersion;                    // 2 bytes
	public ImageDataDirectory MetaData;                // 8 bytes
	public long Flags;                                 // 4 bytes
	public long EntryPointToken;                       // 4 bytes (MethodDef or File token)
	public ImageDataDirectory Resources;               // 8 bytes
	public ImageDataDirectory StrongNameSignature;     // 8 bytes
	public ImageDataDirectory CodeManagerTable;        // 8 bytes
	public ImageDataDirectory VTableFixups;            // 8 bytes
	public ImageDataDirectory ExportAddressTableJumps; // 8 bytes
	public ImageDataDirectory ManagedNativeHeader;     // 8 bytes

	protected CLIHeader()
	{
		HeaderSize = 72;
		MajorRuntimeVersion = 2;
		MinorRuntimeVersion = 0;
		MetaData = new ImageDataDirectory();
		Flags = COMIMAGE_FLAGS_ILONLY;
		EntryPointToken = 0;
		Resources = new ImageDataDirectory();
		StrongNameSignature = new ImageDataDirectory();
		CodeManagerTable = new ImageDataDirectory();
		VTableFixups = new ImageDataDirectory();
		ExportAddressTableJumps = new ImageDataDirectory();
		ManagedNativeHeader = new ImageDataDirectory();
	}


	/**
	 * Parses a CLI header from an input stream
	 */
	public CLIHeader(edu.arizona.cs.mbel.MSILInputStream in) throws java.io.IOException, edu.arizona.cs.mbel.parse.MSILParseException
	{
		long start = in.getCurrent();
		HeaderSize = in.readDWORD();
		MajorRuntimeVersion = in.readWORD();
		MinorRuntimeVersion = in.readWORD();
		MetaData = new ImageDataDirectory(in);
		Flags = in.readDWORD();
		EntryPointToken = in.readDWORD();
		Resources = new ImageDataDirectory(in);
		StrongNameSignature = new ImageDataDirectory(in);
		CodeManagerTable = new ImageDataDirectory(in);
		VTableFixups = new ImageDataDirectory(in);
		ExportAddressTableJumps = new ImageDataDirectory(in);
		ManagedNativeHeader = new ImageDataDirectory(in);
		long end = in.getCurrent();
		in.seek(start);
		in.zero(end - start);
		in.seek(end);
	}

	/**
	 * Writes a CLI header out to a buffer
	 */
	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer)
	{
		buffer.putDWORD(72);
		buffer.putWORD(MajorRuntimeVersion);
		buffer.putWORD(MinorRuntimeVersion);
		MetaData.emit(buffer);
		buffer.putDWORD(Flags);
		buffer.putDWORD(EntryPointToken);
		Resources.emit(buffer);
		StrongNameSignature.emit(buffer);
		CodeManagerTable.emit(buffer);
		VTableFixups.emit(buffer);
		ExportAddressTableJumps.emit(buffer);
		ManagedNativeHeader.emit(buffer);
	}
   
	 /*
   public void output(){
      System.out.print("CLIHeader:");
      System.out.print("\nHeaderSize = " + HeaderSize);
      System.out.print("\nMajorRuntimeVersion = " + MajorRuntimeVersion);
      System.out.print("\nMinorRuntimeVersion = " + MinorRuntimeVersion);
      System.out.print("\nMetaData = " + MetaData);
      System.out.print("\nFlags = " + "0x" + Long.toHexString(Flags));
      System.out.print("\nEntryPointToken = " + EntryPointToken);
      System.out.print("\nResources = " + Resources);
      System.out.print("\nStrongNameSignature = " + StrongNameSignature);
      System.out.print("\nCodeManagerTable = " + CodeManagerTable);
      System.out.print("\nVTableFixups = " + VTableFixups);
      System.out.print("\nExportAddressTableJumps = " + ExportAddressTableJumps);
      System.out.print("\nManagedNativeHeader = " + ManagedNativeHeader);
   }
	 */
}
