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
 * This class contains the raw form of the metadata in a .NET module.
 *
 * @author Michael Stepp
 */
public class Metadata
{
	private static final long MAGIC = 0x424A5342L;
	private static final String STRINGS_STREAM_NAME = "#strings";
	private static final String BLOB_STREAM_NAME = "#blob";
	private static final String GUID_STREAM_NAME = "#guid";
	private static final String US_STREAM_NAME = "#us";
	private static final String COMPRESSED_STREAM_NAME = "#~";
	private static final String UNCOMPRESSED_STREAM_NAME = "#-";

	///// Storage signature //////////////////////////
	public long Signature;       // 4bytes (== MAGIC)
	public int MajorVersion;     // 2bytes
	public int MinorVersion;     // 2bytes
	public long ExtraData;       // 4bytes (== 0)
	public long Length;          // 4bytes
	public byte[] VersionString; // [Length]
	//////////////////////////////////////////////////

	///// Storage Header /////////////////////////////
	public int Flags;                     // 1byte (== 0)
	public int Streams;                   // 2bytes
	public StreamHeader[] stream_headers; // [Streams]
	//////////////////////////////////////////////////
	private long startFP;   // important, do not remove!

	public Metadata()
	{
		Signature = MAGIC;
		MajorVersion = 1;
		MinorVersion = 1;
		ExtraData = 0;
		Length = 12;
		VersionString = new byte[]{
				(byte) 'v',
				(byte) '1',
				(byte) '.',
				(byte) '0',
				(byte) '.',
				(byte) '3',
				(byte) '7',
				(byte) '0',
				(byte) '5',
				0,
				0,
				0
		};
		Flags = 0;
		Streams = 0;
		stream_headers = null;
	}


	/**
	 * Constructs and parses the metadata chunk of a .NET module
	 *
	 * @param in the input stream to read from
	 */
	public Metadata(edu.arizona.cs.mbel.MSILInputStream in) throws java.io.IOException, edu.arizona.cs.mbel.parse.MSILParseException
	{
		startFP = in.getCurrent();

		Signature = in.readDWORD();
		if(Signature != MAGIC)
		{
			throw new edu.arizona.cs.mbel.parse.MSILParseException("Metadata: Bad magic number");
		}

		//// read storage signature //////////
		MajorVersion = in.readWORD();
		MinorVersion = in.readWORD();
		ExtraData = in.readDWORD();
		Length = in.readDWORD();
		VersionString = new byte[(int) Length];
		in.read(VersionString);
		in.align(4);   // 4-byte align
		//////////////////////////////////////


		//// read storage header /////////////
		Flags = in.readBYTE();
		in.readBYTE(); // padding byte
		Streams = in.readWORD();
		//////////////////////////////////////


		///// Read stream headers //////////////////
		stream_headers = new StreamHeader[Streams];
		for(int i = 0; i < Streams; i++)
		{
			in.align(4);
			stream_headers[i] = new StreamHeader(in);
		}
		////////////////////////////////////////////

		long end = in.getCurrent();
		in.seek(startFP);
		in.zero(end - startFP);
		in.seek(end);
	}

	/**
	 * Parses the metadata tables and streams, and returns them in a TableConstant object.
	 */
	public TableConstants parseTableConstants(edu.arizona.cs.mbel.MSILInputStream in) throws java.io.IOException,
			edu.arizona.cs.mbel.parse.MSILParseException
	{
		StringsStream strings_stream = null;
		BlobStream blob_stream = null;
		GUIDStream guid_stream = null;
		USStream us_stream = null;
		CompressedStream c_stream = null;

		for(int i = 0; i < Streams; i++)
		{
			if(stream_headers[i].rcName.toLowerCase().equals(US_STREAM_NAME) && us_stream == null)
			{
				// Parse User String stream
				in.seek(startFP + stream_headers[i].Offset);
				us_stream = new USStream(in, stream_headers[i].Size);

			}
			else if(stream_headers[i].rcName.toLowerCase().equals(STRINGS_STREAM_NAME) && strings_stream == null)
			{
				// Parse Strings heap stream
				in.seek(startFP + stream_headers[i].Offset);
				strings_stream = new StringsStream(in, stream_headers[i].Size);

			}
			else if(stream_headers[i].rcName.toLowerCase().equals(GUID_STREAM_NAME) && guid_stream == null)
			{
				// Parse GUID heap stream
				in.seek(startFP + stream_headers[i].Offset);
				guid_stream = new GUIDStream(in, stream_headers[i].Size);

			}
			else if(stream_headers[i].rcName.toLowerCase().equals(BLOB_STREAM_NAME) && blob_stream == null)
			{
				// Parse Blob heap stream
				in.seek(startFP + stream_headers[i].Offset);
				blob_stream = new BlobStream(in, stream_headers[i].Size);

			}
			else if(stream_headers[i].rcName.toLowerCase().equals(COMPRESSED_STREAM_NAME) && c_stream == null)
			{
				// Parse ~ (compressed) stream
				in.seek(startFP + stream_headers[i].Offset);
				c_stream = new CompressedStream(in);
			}
			else if(stream_headers[i].rcName.toLowerCase().equals(UNCOMPRESSED_STREAM_NAME) && c_stream == null)
			{
				// Parse - (uncompressed) stream
				in.seek(startFP + stream_headers[i].Offset);
				c_stream = new CompressedStream(in);
			}
		}

		if(c_stream == null)
		{
			throw new edu.arizona.cs.mbel.parse.MSILParseException("Metadata: ~ or - stream not found");
		}

		TableConstants tc = new TableConstants(c_stream, strings_stream, blob_stream, guid_stream, us_stream);
		in.seek(c_stream.tableStartFP);
		tc.parseTables(in);
		return tc;
	}

	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer)
	{
		buffer.putDWORD(Signature);
		buffer.putWORD(MajorVersion);
		buffer.putWORD(MinorVersion);
		buffer.putDWORD(ExtraData);
		buffer.putDWORD(Length);
		buffer.put(VersionString);
		buffer.pad(4);
		buffer.put(Flags);
		buffer.put(0);
		buffer.putWORD(Streams);
		for(int i = 0; i < Streams; i++)
		{
			buffer.pad(4);
			stream_headers[i].emit(buffer);
		}
	}
   
/*
   public void output(){
      System.out.print("METADATA:{0x"+Long.toHexString(startFP));
      System.out.print("\n  Signature = 0x" + Long.toHexString(Signature));
      System.out.print("\n  MajorVersion = " + MajorVersion);
      System.out.print("\n  MinorVersion = " + MinorVersion);
      System.out.print("\n  ExtraData = " + ExtraData);
      System.out.print("\n  Length = " + Length);
      System.out.print("\n  VersionString[" + Length + "] = {");

      for (int i=0;i<Length;i++)
         System.out.print((char)(VersionString[i]&0xFF) + " ");
         
      System.out.print("}\n  Flags = " + Flags);
      System.out.print("\n  Streams = " + Streams);
      for (int i=0;i<Streams;i++){
         System.out.print("\n");
         stream_headers[i].output();
      }
   }
*/
}

/**
 * This class holds the raw data for a stream header in the Metadata chunk.
 */
class StreamHeader
{
	public long Offset;     // 4 byte offset from start of metadata
	public long Size;       // 4 bytes, will be multiple of 4
	public String rcName;   // null-terminated string name (<=16 chars)

	/**
	 * Constructs and parses a StreamHeader
	 *
	 * @param in the input stream to read from
	 */
	protected StreamHeader(edu.arizona.cs.mbel.MSILInputStream in) throws java.io.IOException
	{
		Offset = in.readDWORD();
		Size = in.readDWORD();

		rcName = "";
		for(int i = 0; i < 16; i++)
		{
			int temp = in.readBYTE();
			if(temp == 0)
			{
				break;
			}
			rcName += (char) temp;
		}
	}

	public StreamHeader(long offset, long size, String name)
	{
		Offset = offset;
		Size = size;
		rcName = name;
	}

	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer)
	{
		// must be emitted on 4-byte boundary
		buffer.putDWORD(Offset);
		buffer.putDWORD(Size);
		for(int i = 0; i < rcName.length(); i++)
		{
			int value = rcName.charAt(i) & 0xFF;
			buffer.put(value);
		}
		buffer.put(0);
	}

/*
   public void output(){
      System.out.print("Stream_Header:{");
      System.out.print("\n  Offset = " + Offset);
      System.out.print("\n  Size = " + Size);
      System.out.print("\n  rcName = \"" + rcName + "\"");
      System.out.print("\n}");
   }
*/
}
