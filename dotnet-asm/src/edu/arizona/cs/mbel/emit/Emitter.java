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

package edu.arizona.cs.mbel.emit;

import java.io.IOException;
import java.io.OutputStream;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.mbel.Module;
import edu.arizona.cs.mbel.mbel.VTableFixup;
import edu.arizona.cs.mbel.parse.COFF_Header;
import edu.arizona.cs.mbel.parse.ImageDataDirectory;
import edu.arizona.cs.mbel.parse.PEModule;
import edu.arizona.cs.mbel.parse.PE_Header;
import edu.arizona.cs.mbel.parse.SectionHeader;

/**
 * This class is the main class used to emit an MBEL Module back to disk. All that is
 * needed is a correct Module instance, and an Emitter can write out a valid PE/COFF
 * executable (or DLL).
 *
 * @author Michael Stepp
 */
public class Emitter
{
	private static final byte[] NET_NAME = new byte[]{
			(byte) '.',
			(byte) 'n',
			(byte) 'e',
			(byte) 't',
			0,
			0,
			0,
			0
	};
	private static final int PE_HEADER_SIZE = 224;

	private Module module;
	private PEModule pe_module;
	private PE_Header pe_header;

	private long timeStamp;
	private boolean hadToShift;

	/**
	 * Makes an Emitter for the given Module
	 */
	public Emitter(Module mod) throws IOException
	{
		module = mod;
		pe_module = module.getPEModule();
		pe_header = pe_module.pe_header;
		timeStamp = System.currentTimeMillis() / 1000L;
		hadToShift = false;
	}

	private long align(long value, long align)
	{
		return value + ((align - (value % align)) % align);
	}

	/**
	 * Writes the Module back out to the given output filename
	 */
	public void emitModule(OutputStream out) throws IOException
	{
		ByteBuffer bigBuffer = new ByteBuffer(1000000);
		ByteBuffer netBuffer = new ByteBuffer(200000);
		COFF_Header coff_header = pe_module.coff_header;


		ClassEmitter classEmitter = new ClassEmitter(module);
		classEmitter.buildTables();
		classEmitter.emitMetadata(netBuffer);

		long netSectionVirtualSize = netBuffer.getPosition();
		netBuffer.pad((int) pe_header.FileAlignment);
		long netSectionRVA = align(pe_module.section_headers[pe_module.section_headers.length - 1].VirtualAddress + pe_module.section_headers[pe_module
				.section_headers.length - 1].VirtualSize, pe_header.SectionAlignment);


		//// emit stub and PE signature ////////////
		pe_module.msdos_stub.emit(bigBuffer);
		bigBuffer.putDWORD(0x00004550L); // "PE\0\0"
		////////////////////////////////////////////


		int netSectionIndex = 0;
		int numSects = coff_header.NumberOfSections;
		int numNormalSects = numSects;
		if(!pe_module.section_headers[numSects - 1].isNetHeader())
		{
			// no .net section header, add one
			netSectionIndex = numSects;
			numSects++;
			SectionHeader[] new_headers = new SectionHeader[numSects];
			for(int i = 0; i < netSectionIndex; i++)
			{
				new_headers[i] = pe_module.section_headers[i];
			}
			new_headers[netSectionIndex] = new SectionHeader();
			pe_module.section_headers = new_headers;

			new_headers[netSectionIndex].Name = NET_NAME;
			new_headers[netSectionIndex].VirtualSize = netSectionVirtualSize;
			new_headers[netSectionIndex].VirtualAddress = netSectionRVA;
			new_headers[netSectionIndex].SizeOfRawData = netBuffer.getPosition();// will be rounded to FileAlignment already
			new_headers[netSectionIndex].PointerToRawData = new_headers[netSectionIndex - 1].PointerToRawData + new_headers[netSectionIndex - 1]
					.SizeOfRawData;
			new_headers[netSectionIndex].Characteristics = SectionHeader.IMAGE_SCN_CNT_CODE |
					SectionHeader.IMAGE_SCN_MEM_READ |
					SectionHeader.IMAGE_SCN_MEM_EXECUTE;
		}
		else
		{
			// already has a .net section
			netSectionIndex = numSects - 1;
			numNormalSects--;
			netSectionRVA = pe_module.section_headers[netSectionIndex].VirtualAddress;
			pe_module.section_headers[netSectionIndex].VirtualSize = netSectionVirtualSize;
		}

		///// emit COFF Header /////////////////////////
		bigBuffer.putWORD(coff_header.Machine);
		bigBuffer.putWORD(numSects);
		bigBuffer.putDWORD(timeStamp);
		long coffSymbolsStart = bigBuffer.getPosition();
		bigBuffer.putDWORD(coff_header.PointerToSymbolTable);
		bigBuffer.putDWORD(coff_header.NumberOfSymbols);
		bigBuffer.putWORD(PE_HEADER_SIZE);
		bigBuffer.putWORD(coff_header.Characteristics | COFF_Header.IMAGE_FILE_LOCAL_SYMS_STRIPPED);
		/////////////////////////////////////////////////


		long endOfHeadersFP = bigBuffer.getPosition() + PE_HEADER_SIZE + (SectionHeader.STRUCT_SIZE * numSects);


		//// emit PE Header /////////////////////////////
		long peheaderStart = bigBuffer.getPosition();
		pe_header.Magic = 0x10B;
		pe_header.SizeOfCode += netBuffer.getPosition();
		long imageSize = align(pe_module.section_headers[numSects - 1].VirtualAddress + pe_module.section_headers[numSects - 1].VirtualSize,
				pe_header.SectionAlignment);
		pe_header.SizeOfImage = imageSize;
		if(endOfHeadersFP > pe_module.section_headers[0].PointerToRawData)
		{
			hadToShift = true;
			pe_header.SizeOfHeaders = pe_module.section_headers[0].PointerToRawData + pe_header.FileAlignment;
		}
		else
		{
			pe_header.SizeOfHeaders = pe_module.section_headers[0].PointerToRawData;
		}
		pe_header.emit(bigBuffer);
		/////////////////////////////////////////////////


		///// fix up PointerToSymbolTable in coff header //////////
		if(hadToShift)
		{
			long end = bigBuffer.getPosition();
			bigBuffer.setPosition((int) coffSymbolsStart);
			long value = bigBuffer.getDWORD();
			value += pe_header.FileAlignment;
			bigBuffer.setPosition((int) coffSymbolsStart);
			bigBuffer.putDWORD(value);
			bigBuffer.setPosition((int) end);
		}
		/////////////////////////////////////////////////////////


		////// re-emit VTableFixups (in same place as old VTableFixups) ////////////////
		for(VTableFixup fixup : module.getVTableFixups())
		{
			long VTABLERVA = fixup.getRVA();
			int FP = 0;
			int sectIndex = 0;
			for(int j = 0; j < numNormalSects; j++)
			{
				if(pe_module.section_headers[j].VirtualAddress > VTABLERVA)
				{
					sectIndex = j - 1;
					FP = (int) (VTABLERVA - pe_module.section_headers[sectIndex].VirtualAddress);
					break;
				}
			}

			byte[] section = pe_module.sections[sectIndex];
			byte[] vtable = fixup.toByteArray();
			for(int j = 0; j < vtable.length; j++)
			{
				section[FP + j] = vtable[j];
			}
		}
		///////////////////////////////////////////////////////////////////////////////


		//// emit data directories ////////////////////
		pe_header.DataDirectory[0].emit(bigBuffer);
		// export table - ok

		pe_header.DataDirectory[1].emit(bigBuffer);
		// import table - ok

		pe_header.DataDirectory[2].emit(bigBuffer);
		// resource table - ok

		pe_header.DataDirectory[3].emit(bigBuffer);
		// exception table - ok

		bigBuffer.putDWORD(0);
		bigBuffer.putDWORD(0);
		// certificate table - remove

		pe_header.DataDirectory[5].emit(bigBuffer);
		// base relocation - ok

		pe_header.DataDirectory[6].emit(bigBuffer);
		// debug - patch

		bigBuffer.putDWORD(0);
		bigBuffer.putDWORD(0);
		// architecture - remove

		pe_header.DataDirectory[8].emit(bigBuffer);
		// global pointer - ok

		pe_header.DataDirectory[9].emit(bigBuffer);
		// TLS - ???

		bigBuffer.putDWORD(0);
		bigBuffer.putDWORD(0);
		// load config - remove

		bigBuffer.putDWORD(0);
		bigBuffer.putDWORD(0);
		// bound import - remove

		pe_header.DataDirectory[12].emit(bigBuffer);
		// import address - ok

		pe_header.DataDirectory[13].emit(bigBuffer);
		// delay load import - ok

		bigBuffer.putDWORD(netSectionRVA);
		bigBuffer.putDWORD(72);
		// CLI header - ok

		bigBuffer.putDWORD(0);
		bigBuffer.putDWORD(0);
		// none
		///////////////////////////////////////////////


		///// cleanup all the data directories with FPs in them ///////////
		if(hadToShift)
		{

			{// Debug Table
				long debugRVA = pe_header.DataDirectory[ImageDataDirectory.DEBUG_TABLE_INDEX].VirtualAddress;
				int debugFP = 0;
				int sectIndex = 0;
				for(int i = 0; i < numNormalSects; i++)
				{
					if(pe_module.section_headers[i].VirtualAddress > debugRVA)
					{
						debugFP = (int) (debugRVA - pe_module.section_headers[i - 1].VirtualAddress);
						sectIndex = i - 1;
						break;
					}
				}

				long value = pe_module.sections[sectIndex][debugFP + 24] & 0xFFL;
				value |= (pe_module.sections[sectIndex][debugFP + 25] & 0xFFL) << 8;
				value |= (pe_module.sections[sectIndex][debugFP + 26] & 0xFFL) << 16;
				value |= (pe_module.sections[sectIndex][debugFP + 27] & 0xFFL) << 24;
				value &= 0xFFFFFFFFL;
				value += pe_header.FileAlignment;

				pe_module.sections[sectIndex][debugFP + 24] = (byte) (value & 0xFF);
				pe_module.sections[sectIndex][debugFP + 25] = (byte) ((value >> 8) & 0xFF);
				pe_module.sections[sectIndex][debugFP + 26] = (byte) ((value >> 16) & 0xFF);
				pe_module.sections[sectIndex][debugFP + 27] = (byte) ((value >> 24) & 0xFF);
			}
		}
		//////////////////////////////////////////////////////////////////


		/// emit section headers ///////////////
		for(int i = 0; i < numSects; i++)
		{
			if(hadToShift)
			{
				pe_module.section_headers[i].PointerToRawData += pe_header.FileAlignment;
				if(pe_module.section_headers[i].PointerToRelocations != 0)
				{
					pe_module.section_headers[i].PointerToRelocations += pe_header.FileAlignment;
				}
				if(pe_module.section_headers[i].PointerToLinenumbers != 0)
				{
					pe_module.section_headers[i].PointerToLinenumbers += pe_header.FileAlignment;
				}
			}
			pe_module.section_headers[i].emit(bigBuffer);
		}
		////////////////////////////////////////


		////// Apply .net section patches //////////
		long end = netBuffer.getPosition();
		for(PatchList.Patch patch = classEmitter.getNetPatches().first; patch != null; patch = patch.next)
		{
			netBuffer.setPosition((int) patch.address);
			long value = netBuffer.get() & 0xFFL;
			value |= (netBuffer.get() & 0xFFL) << 8;
			value |= (netBuffer.get() & 0xFFL) << 16;
			value |= (netBuffer.get() & 0xFFL) << 24;
			value &= 0xFFFFFFFFL;
			value += netSectionRVA;
			netBuffer.setPosition((int) patch.address);
			netBuffer.putDWORD(value);
		}
		netBuffer.setPosition((int) end);
		//////////////////////////////////////////


		//// emit sections ///////////////////
		bigBuffer.pad((int) pe_header.SizeOfHeaders);
		for(int i = 0; i < numNormalSects; i++)
		{
			bigBuffer.put(pe_module.sections[i]);
		}
		bigBuffer.concat(netBuffer);
		//////////////////////////////////////


		///// write out the entire file to disk /////////////
		byte[] theWholeEnchilada = bigBuffer.toByteArray();
		out.write(theWholeEnchilada);
		/////////////////////////////////////////////////////
	}
}
