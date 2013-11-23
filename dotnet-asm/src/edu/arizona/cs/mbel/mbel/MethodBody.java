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

/**
 * This class represents a method body. It has an InstructionList with the actual Instrucitons in it
 * and a vector of exception clauses defined within the method. It also contains information about
 * the local variables defined in this method.
 *
 * @author Michael Stepp
 */
public class MethodBody
{
	private static final int FORMAT_MASK = 0x03;
	private static final int FLAG_FAT_FORMAT = 0x03;
	private static final int FLAG_TINY_FORMAT = 0x02;
	private static final int FLAG_MORE_SECTIONS = 0x08;
	private static final int FLAG_INIT_LOCALS = 0x10;

	private int Flags;      // only kept this for FLAG_INIT_LOCALS
	private int MaxStack;
	private edu.arizona.cs.mbel.signature.LocalVarList localVars; // may be null
	private edu.arizona.cs.mbel.instructions.InstructionList instructionList;
	private java.util.Vector SECs;   // structured exception clauses

	/**
	 * Makes a MethodBody with the given max stack value and local vars, possibly initializing local vars
	 *
	 * @param initlocals true iff the locals vars should be initialized with their default constructors
	 * @param maxstack   the maximum number of itesm that may be pushed onthe stack in this method call
	 * @param locals     the local variables list signature (may be null if no locals)
	 */
	public MethodBody(boolean initlocals, int maxstack, edu.arizona.cs.mbel.signature.LocalVarList locals)
	{
		Flags = (initlocals ? FLAG_INIT_LOCALS : 0);
		MaxStack = maxstack;
		localVars = locals;
		instructionList = new edu.arizona.cs.mbel.instructions.InstructionList();
		SECs = new java.util.Vector(10);
	}

	/**
	 * Makes a MethodBody by parsing it from a ClassParser
	 */
	protected MethodBody(ClassParser parse) throws java.io.IOException, edu.arizona.cs.mbel.parse.MSILParseException
	{
		boolean moreSects = false;
		edu.arizona.cs.mbel.MSILInputStream in = parse.getMSILInputStream();

		long start = in.getCurrent();


		Flags = in.readBYTE();
		int Size = 0;
		long CodeSize = 0, LocalVarSigTok = 0;

		if((Flags & FORMAT_MASK) == FLAG_TINY_FORMAT)
		{
			// tiny format
			Size = 0;
			CodeSize = (Flags >> 2) & 0x3F;
			Flags &= 0x3;
			MaxStack = 8;
			LocalVarSigTok = 0;
		}
		else
		{
			// fat format
			Flags = (Flags & 0xFF) | (in.readBYTE() << 8);
			Size = (Flags >> 12) & 0xF;
			Flags &= 0x0FFF;
			MaxStack = in.readWORD();
			CodeSize = in.readDWORD();
			LocalVarSigTok = in.readDWORD();

			if((Flags & FLAG_MORE_SECTIONS) != 0)
			{
				moreSects = true;
			}
		}
		// by this point, the method body size will be in CodeSize

		if((LocalVarSigTok != 0) && (Flags & FORMAT_MASK) == FLAG_FAT_FORMAT)
		{
			localVars = parse.getLocalVarList(LocalVarSigTok);
		}

		instructionList = new edu.arizona.cs.mbel.instructions.InstructionList(parse, CodeSize);
		SECs = new java.util.Vector(5);

		// this comes after the body
		if(moreSects)
		{
			in.align(4);
			MethodDataSection method_data_section = null;
			do
			{
				method_data_section = new MethodDataSection(in);
				for(int i = 0; i < method_data_section.nclauses; i++)
				{
					edu.arizona.cs.mbel.instructions.InstructionHandle ts = instructionList.getHandleAt((int) method_data_section.clauses[i].TryOffset);
					edu.arizona.cs.mbel.instructions.InstructionHandle te = instructionList.getHandleEndingAt((int) (method_data_section.clauses[i].TryOffset +
							method_data_section.clauses[i].TryLength));
					edu.arizona.cs.mbel.instructions.InstructionHandle hs = instructionList.getHandleAt((int) method_data_section.clauses[i].HandlerOffset);
					edu.arizona.cs.mbel.instructions.InstructionHandle he = instructionList.getHandleEndingAt((int) (method_data_section.clauses[i].HandlerOffset +
							method_data_section.clauses[i].HandlerLength));

					switch(method_data_section.clauses[i].Flags)
					{
						case Clause.FLAG_CLAUSE_EXCEPTION:
						{
							edu.arizona.cs.mbel.mbel.AbstractTypeReference type = parse.getClassRef(method_data_section.clauses[i].ClassToken);
							SECs.add(new edu.arizona.cs.mbel.instructions.TypedExceptionClause(type, ts, te, hs, he));
							break;
						}

						case Clause.FLAG_CLAUSE_FILTER:
						{
							edu.arizona.cs.mbel.instructions.InstructionHandle fs = instructionList.getHandleAt((int) method_data_section.clauses[i].FilterOffset);
							edu.arizona.cs.mbel.instructions.InstructionHandle fe = instructionList.getHandleEndingAt((int) method_data_section.clauses[i].HandlerOffset);
							SECs.add(new edu.arizona.cs.mbel.instructions.FilteredExceptionClause(fs, fe, ts, te, hs, he));
							break;
						}

						case Clause.FLAG_CLAUSE_FINALLY:
						{
							SECs.add(new edu.arizona.cs.mbel.instructions.FinallyClause(ts, te, hs, he));
							break;
						}

						case Clause.FLAG_CLAUSE_FAULT:
						{
							SECs.add(new edu.arizona.cs.mbel.instructions.FaultClause(ts, te, hs, he));
							break;
						}
					}
				}

			}
			while(method_data_section.hasNext());
		}

		long end = in.getCurrent();
		in.seek(start);
		in.zero(end - start);
		in.seek(end);
	}

	/**
	 * Returns a non-null array of StructuredExceptionClauses for this method body
	 */
	public edu.arizona.cs.mbel.instructions.StructuredExceptionClause[] getSECs()
	{
		edu.arizona.cs.mbel.instructions.StructuredExceptionClause[] secs = new edu.arizona.cs.mbel.instructions.StructuredExceptionClause[SECs.size()];
		for(int i = 0; i < secs.length; i++)
		{
			secs[i] = (edu.arizona.cs.mbel.instructions.StructuredExceptionClause) SECs.get(i);
		}
		return secs;
	}

	/**
	 * Returns the local variable list signature for this method body
	 */
	public edu.arizona.cs.mbel.signature.LocalVarList getLocalVarList()
	{
		return localVars;
	}

	/**
	 * Adds a Structured Exception Clause to this method
	 */
	public void addSEC(edu.arizona.cs.mbel.instructions.StructuredExceptionClause sec)
	{
		SECs.add(sec);
	}

	/**
	 * Removes a Structured Exception Clause from this method, using equals()
	 */
	public void removeSEC(edu.arizona.cs.mbel.instructions.StructuredExceptionClause sec)
	{
		SECs.remove(sec);
	}

	/**
	 * Returns the method body flags
	 */
	protected int getFlags()
	{
		return Flags;
	}

	/**
	 * Sets the method body flags
	 */
	protected void setFlags(int flags)
	{
		Flags = flags;
	}

	/**
	 * Returns true iff this method will initialize all of its local variables with their default constructors.
	 */
	public boolean initLocals()
	{
		return (Flags & FLAG_INIT_LOCALS) != 0;
	}

	/**
	 * Sets whether or not this method should initialize its local variables with their default constructors.
	 */
	public void setInitLocals(boolean b)
	{
		if(b)
		{
			Flags |= FLAG_INIT_LOCALS;
		}
		else
		{
			Flags &= ~FLAG_INIT_LOCALS;
		}
	}

	/**
	 * Returns the maximum number of stack items
	 */
	public int getMaxStack()
	{
		return MaxStack;
	}

	/**
	 * Sets the maximum number of stack items
	 */
	public void setMaxStack(int max)
	{
		MaxStack = max;
	}

	/**
	 * Returns the instruction list for this method body (never null)
	 */
	public edu.arizona.cs.mbel.instructions.InstructionList getInstructionList()
	{
		return instructionList;
	}

	/**
	 * Emits this method body to a ByteBuffer, with the given ClassEmitter
	 */
	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		// DONE!!!
		instructionList.setPositions();
		int codesize = instructionList.getSizeInBytes();
		if(localVars != null && localVars.getCount() == 0)
		{
			localVars = null;
		}

		boolean canbetiny = true;
		canbetiny = canbetiny && (localVars == null);
		canbetiny = canbetiny && (SECs.size() == 0);
		canbetiny = canbetiny && (MaxStack <= 8);
		canbetiny = canbetiny && (codesize < 64);
		canbetiny = canbetiny && (!initLocals());

		if(canbetiny)
		{
			int flags = FLAG_TINY_FORMAT;
			flags |= (codesize << 2);
			buffer.put(flags);
		}
		else
		{
			int flags = FLAG_FAT_FORMAT;
			if((Flags & FLAG_INIT_LOCALS) != 0)
			{
				flags |= FLAG_INIT_LOCALS;
			}
			if(SECs.size() > 0)
			{
				flags |= FLAG_MORE_SECTIONS;
			}
			flags |= (3 << 12);
			buffer.putINT16(flags);
			buffer.putINT16(MaxStack);
			buffer.putINT32(codesize);
			if(localVars != null)
			{
				long token = emitter.getStandAloneSigToken(localVars);
				buffer.putTOKEN(token);
			}
			else
			{
				buffer.putTOKEN(0L);
			}
		}

		instructionList.emit(buffer, emitter);

		if(SECs.size() > 0)
		{
			int flags = MethodDataSection.FLAG_FAT_FORMAT;
			flags <<= 24;
			flags |= (SECs.size() * 24 + 4);
			buffer.putINT32(flags);

			for(int i = 0; i < SECs.size(); i++)
			{
				Object obj = SECs.get(i);
				if(obj instanceof edu.arizona.cs.mbel.instructions.FaultClause)
				{
					edu.arizona.cs.mbel.instructions.FaultClause fc = (edu.arizona.cs.mbel.instructions.FaultClause) obj;
					buffer.putINT32(4);
					buffer.putINT32(fc.getTryStart().getPosition());
					int trylength = fc.getTryEnd().getPosition() + fc.getTryEnd().getInstruction().getLength() - fc.getTryStart().getPosition();
					buffer.putINT32(trylength);

					buffer.putINT32(fc.getHandlerStart().getPosition());
					int handlerlength = fc.getHandlerEnd().getPosition() + fc.getHandlerEnd().getInstruction().getLength() - fc.getHandlerStart().getPosition();
					buffer.putINT32(handlerlength);
					buffer.putTOKEN(0);
				}
				else if(obj instanceof edu.arizona.cs.mbel.instructions.FilteredExceptionClause)
				{
					edu.arizona.cs.mbel.instructions.FilteredExceptionClause fec = (edu.arizona.cs.mbel.instructions.FilteredExceptionClause) obj;
					buffer.putINT32(1);
					buffer.putINT32(fec.getTryStart().getPosition());
					int trylength = fec.getTryEnd().getPosition() + fec.getTryEnd().getInstruction().getLength() - fec.getTryStart().getPosition();
					buffer.putINT32(trylength);

					buffer.putINT32(fec.getHandlerStart().getPosition());
					int handlerlength = fec.getHandlerEnd().getPosition() + fec.getHandlerEnd().getInstruction().getLength() - fec.getHandlerStart().getPosition();
					buffer.putINT32(handlerlength);

					buffer.putINT32(fec.getFilterStart().getPosition());
				}
				else if(obj instanceof edu.arizona.cs.mbel.instructions.FinallyClause)
				{
					edu.arizona.cs.mbel.instructions.FinallyClause fc = (edu.arizona.cs.mbel.instructions.FinallyClause) obj;
					buffer.putINT32(2);
					buffer.putINT32(fc.getTryStart().getPosition());
					int trylength = fc.getTryEnd().getPosition() + fc.getTryEnd().getInstruction().getLength() - fc.getTryStart().getPosition();
					buffer.putINT32(trylength);

					buffer.putINT32(fc.getHandlerStart().getPosition());
					int handlerlength = fc.getHandlerEnd().getPosition() + fc.getHandlerEnd().getInstruction().getLength() - fc.getHandlerStart().getPosition();
					buffer.putINT32(handlerlength);
					buffer.putTOKEN(0);
				}
				else if(obj instanceof edu.arizona.cs.mbel.instructions.TypedExceptionClause)
				{
					edu.arizona.cs.mbel.instructions.TypedExceptionClause tec = (edu.arizona.cs.mbel.instructions.TypedExceptionClause) obj;
					buffer.putINT32(0);
					buffer.putINT32(tec.getTryStart().getPosition());
					int trylength = tec.getTryEnd().getPosition() + tec.getTryEnd().getInstruction().getLength() - tec.getTryStart().getPosition();
					buffer.putINT32(trylength);

					buffer.putINT32(tec.getHandlerStart().getPosition());
					int handlerlength = tec.getHandlerEnd().getPosition() + tec.getHandlerEnd().getInstruction().getLength() - tec.getHandlerStart().getPosition();
					buffer.putINT32(handlerlength);

					long token = emitter.getTypeToken(tec.getExceptionType());
					buffer.putTOKEN(token);
				}
			}
		}
	}
   
   /*
   public void output(){
      System.out.print("MethodBody:[");
      System.out.print("\n  Flags = " + Integer.toBinaryString(Flags));
      System.out.print("\n  MaxStack = " + MaxStack);
      if (localVars!=null){
         System.out.print("\n  Signature = ");
         localVars.output();
      }
      if (instructionList!=null){
         System.out.print("\n  code={\n");
         instructionList.output();
         System.out.print("  }");
      }
      
      for (int i=0;i<SECs.size();i++){
         System.out.println();
         ((edu.arizona.cs.mbel.instructions.StructuredExceptionClause)SECs.get(i)).output();
      }
      
      System.out.println("\n]");
   }
*/
}

///////////////////////////////////////////////////////////////////////////////////

class MethodDataSection
{
	public static final int FLAG_EXCEPTION_HANDLING_DATA = 0x01;   // must be set
	public static final int FLAG_FAT_FORMAT = 0x40;
	public static final int FLAG_MORE_SECTIONS = 0x80;

	public int Kind;        // 1 byte
	public int DataSize;    // either 1 byte followed by 2 padding bytes, or 3 bytes
	public Clause[] clauses;// [nclauses]
	public int nclauses;

	public MethodDataSection(edu.arizona.cs.mbel.MSILInputStream in) throws java.io.IOException
	{
		Kind = in.readBYTE();
		DataSize = in.readBYTE();

		if((Kind & FLAG_FAT_FORMAT) != 0)
		{
			// fat data header
			DataSize = (DataSize & 0xFF) | ((in.readBYTE() & 0xFF) << 8) | ((in.readBYTE() & 0xFF) << 16);
		}
		else
		{
			// tiny data header
			// 2 null bytes
			in.readWORD();
		}

		nclauses = -1;
		boolean isTiny = ((Kind & FLAG_FAT_FORMAT) == 0);

		if(isTiny)
		{
			nclauses = (DataSize - 4) / Clause.TINY_SIZE;
			clauses = new Clause[nclauses];
		}
		else
		{
			nclauses = (DataSize - 4) / Clause.FAT_SIZE;
			clauses = new Clause[nclauses];
		}

		for(int i = 0; i < nclauses; i++)
		{
			clauses[i] = new Clause(in, isTiny);
		}
	}

/*    
   public void output(){
      System.out.print("MethodDataSection=[");
      System.out.print("\n  Kind = " + Integer.toBinaryString(Kind));
      System.out.print("\n  DataSize = " + DataSize);
      for (int i=0;i<nclauses;i++)
         clauses[i].output();
      System.out.print("\n]");
   }   
*/

	public boolean hasNext()
	{
		return ((Kind & FLAG_MORE_SECTIONS) != 0);
	}
}

class Clause
{
	public static final int FAT_SIZE = 24;
	public static final int TINY_SIZE = 12;
	public static final int FLAG_CLAUSE_EXCEPTION = 0x0000;
	public static final int FLAG_CLAUSE_FILTER = 0x0001;
	public static final int FLAG_CLAUSE_FINALLY = 0x0002;
	public static final int FLAG_CLAUSE_FAULT = 0x0004;

	public int Flags;           // either 2 or 4 bytes
	public long TryOffset;      // either 2 or 4 bytes
	public long TryLength;      // either 1 or 4 bytes
	public long HandlerOffset;  // either 2 or 4 bytes
	public long HandlerLength;  // either 1 or 4 bytes
	// union /////////////////////////////
	public long ClassToken;     // 4 bytes - use this one when FLAG_CLAUSE_EXCEPTION
	public long FilterOffset;   // 4 bytes - use this one when FLAG_CLAUSE_FILTER
	//////////////////////////////////////
	private boolean isTiny;

	protected Clause(edu.arizona.cs.mbel.MSILInputStream in, boolean tiny) throws java.io.IOException
	{
		isTiny = tiny;

		if(isTiny)
		{
			Flags = in.readWORD();
			TryOffset = (in.readWORD() & 0xFFFFL);
			TryLength = (in.readBYTE() & 0xFFL);
			HandlerOffset = (in.readWORD() & 0xFFFFL);
			HandlerLength = (in.readBYTE() & 0xFFL);
		}
		else
		{
			Flags = (int) in.readDWORD();
			TryOffset = in.readDWORD();
			TryLength = in.readDWORD();
			HandlerOffset = in.readDWORD();
			HandlerLength = in.readDWORD();
		}
		ClassToken = FilterOffset = in.readDWORD();
	}
   
/*
   public void output(){
      System.out.print("\n  Clause={" + (isTiny?"Tiny":"Fat"));
      System.out.print("\n    Flags = " + Long.toBinaryString(Flags));
      System.out.print("\n    TryOffset = " + TryOffset);
      System.out.print("\n    TryLength = " + TryLength);
      System.out.print("\n    HandlerOffset = " + HandlerOffset);
      System.out.print("\n    HandlerLength = " + HandlerLength);
      System.out.print("\n    ClassToken/FilterLength = " + ClassToken);
      System.out.print("\n  }");
   }
*/
}
