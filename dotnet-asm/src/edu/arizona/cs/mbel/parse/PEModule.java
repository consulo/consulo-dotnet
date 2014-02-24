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


import java.io.IOException;

import edu.arizona.cs.mbel.MSILInputStream;
import edu.arizona.cs.mbel.metadata.Metadata;

/**
 * This class is used to parse and hold all the header information about a PE/COFF file.
 * It also keeps buffers for the contents of each of the sections in the file.
 *
 * @author Michael Stepp
 */
public class PEModule
{
	public MSDOS_Stub msdos_stub;
	public COFF_Header coff_header;
	public PE_Header pe_header;
	public CLIHeader cliHeader;
	public Metadata metadata;
	public SectionHeader[] section_headers;
	public byte[][] sections;

	public PEModule(int sub)
	{
		msdos_stub = new MSDOS_Stub();
		coff_header = new COFF_Header();
		pe_header = new PE_Header(sub);
		cliHeader = new CLIHeader();
		metadata = new Metadata();

		section_headers = new SectionHeader[2];
		section_headers[0] = new SectionHeader();
		section_headers[0].Name = new byte[]{
				(byte) '.',
				(byte) 't',
				(byte) 'e',
				(byte) 'x',
				(byte) 't',
				0,
				0,
				0
		};
		section_headers[0].VirtualSize = 96;
		section_headers[0].VirtualAddress = 0x2000;
		section_headers[0].SizeOfRawData = 0x1000;
		section_headers[0].PointerToRawData = 0x1000;
		section_headers[0].PointerToRelocations = 0;
		section_headers[0].PointerToLinenumbers = 0;
		section_headers[0].NumberOfRelocations = 0;
		section_headers[0].NumberOfLinenumbers = 0;
		section_headers[0].Characteristics = SectionHeader.IMAGE_SCN_CNT_CODE |
				SectionHeader.IMAGE_SCN_MEM_EXECUTE |
				SectionHeader.IMAGE_SCN_MEM_READ;


		section_headers[1] = new SectionHeader();
		section_headers[1].Name = new byte[]{
				(byte) '.',
				(byte) 'r',
				(byte) 'e',
				(byte) 'l',
				(byte) 'o',
				(byte) 'c',
				0,
				0
		};
		section_headers[1].VirtualSize = 12;
		section_headers[1].VirtualAddress = 0x4000;
		section_headers[1].SizeOfRawData = 0x1000;
		section_headers[1].PointerToRawData = 0x2000;
		section_headers[1].PointerToRelocations = 0;
		section_headers[1].PointerToLinenumbers = 0;
		section_headers[1].NumberOfRelocations = 0;
		section_headers[1].NumberOfLinenumbers = 0;
		section_headers[1].Characteristics = SectionHeader.IMAGE_SCN_CNT_INITIALIZED_DATA |
				SectionHeader.IMAGE_SCN_MEM_DISCARDABLE |
				SectionHeader.IMAGE_SCN_MEM_READ;


		sections = new byte[2][];


		sections[0] = new byte[0x1000];
		sections[1] = new byte[0x1000];

		byte[] temp = new byte[]{
				(byte) 0x38,
				(byte) 0x20,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				//IAT
				(byte) 0x38,
				(byte) 0x20,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				//ILT==IAT

				(byte) 0x08,
				(byte) 0x20,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x46,
				(byte) 0x20,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x20,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				// IT

				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x5f,
				(byte) 0x43,
				(byte) 0x6f,
				(byte) 0x72,
				(byte) 0x45,
				(byte) 0x78,
				(byte) 0x65,
				(byte) 0x4d,
				(byte) 0x61,
				(byte) 0x69,
				(byte) 0x6e,
				(byte) 0x00,
				(byte) 0x6d,
				(byte) 0x73,
				(byte) 0x63,
				(byte) 0x6f,
				(byte) 0x72,
				(byte) 0x65,
				(byte) 0x65,
				(byte) 0x2e,
				(byte) 0x64,
				(byte) 0x6c,
				(byte) 0x6c,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0xff,
				(byte) 0x25,
				(byte) 0x00,
				(byte) 0x20,
				(byte) 0x40,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00
				// hint/name table
		};

		for(int i = 0; i < temp.length; i++)
		{
			sections[0][i] = temp[i];
		}

		temp = new byte[]{
				0,
				(byte) 0x20,
				0,
				0,
				(byte) 0x0C,
				0,
				0,
				0,
				(byte) 0x58,
				(byte) 0x30,
				0,
				0
		};
		for(int i = 0; i < temp.length; i++)
		{
			sections[1][i] = temp[i];
		}
	}

	/**
	 * Parses a PEModule from an input stream
	 */
	public PEModule(MSILInputStream msil) throws IOException, MSILParseException
	{
		msdos_stub = new MSDOS_Stub(msil);

		// Verify PE signature ///////////
		msil.seek(msdos_stub.getHeaderAddress());
		if(!msil.match(PE_Header.PE_TAG))
		{
			throw new MSILParseException("PEModule: Missing PE signature");
		}
		//////////////////////////////////

		coff_header = new COFF_Header(msil);
		pe_header = new PE_Header(msil);

		///// Parse Section Headers and sections /////////////////////////
		section_headers = new SectionHeader[coff_header.NumberOfSections];
		for(int i = 0; i < coff_header.NumberOfSections; i++)
		{
			section_headers[i] = new SectionHeader(msil);
		}
		msil.activate(section_headers);
		/////////////////////////////////////////////////////////////////


		//// Parse CLI Header /////////////////////////////////
		long CLIRVA = pe_header.DataDirectory[ImageDataDirectory.CLI_HEADER_INDEX].VirtualAddress;
		if(CLIRVA == 0)
		{
			throw new MSILParseException("PEModule: CLI Header not present");
		}
		long CLIFP = msil.getFilePointer(CLIRVA);
		msil.seek(CLIFP);
		cliHeader = new CLIHeader(msil);
		///////////////////////////////////////////////////////


		///// Parse metadata header ///////////////////////////
		long metadataRVA = cliHeader.MetaData.VirtualAddress;
		long metadataFP = msil.getFilePointer(metadataRVA);
		msil.seek(metadataFP);
		metadata = new Metadata(msil);
		///////////////////////////////////////////////////////
	}

	/**
	 * This method buffers each section of the PE/COFF file. This should only be called by ClassParser.
	 */
	public void bufferSections(MSILInputStream msil) throws IOException
	{
		sections = new byte[coff_header.NumberOfSections][];

		for(int i = 0; i < sections.length; i++)
		{
			sections[i] = new byte[(int) section_headers[i].SizeOfRawData];
			msil.seek(section_headers[i].PointerToRawData);
			msil.read(sections[i]);
		}
	}

	/**
	 * Returns true iff this is a DLL file (according to the flags)
	 */
	public boolean isDLLFile()
	{
		return ((coff_header.Characteristics & COFF_Header.IMAGE_FILE_DLL) != 0);
	}
   
	 /*
   public void output(){
      msdos_stub.output();
      System.out.print("\n\n");
      coff_header.output();
      System.out.print("\n\n");
      pe_header.output();
      System.out.print("\n\n");
      for (int i=0;i<coff_header.NumberOfSections;i++){
         section_headers[i].output();
         System.out.print("\n\n");
      }
      cliHeader.output();
      System.out.println();
      metadata.output();
      System.out.println();
   }
	 */
}
