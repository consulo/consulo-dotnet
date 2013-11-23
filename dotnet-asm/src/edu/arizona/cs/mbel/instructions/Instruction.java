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

// all metadata tokens are 4 bytes, with the MSB=table number, and bottom 3 bytes are row number

package edu.arizona.cs.mbel.instructions;

import java.io.IOException;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Hashtable;

import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.MSILInputStream;
import edu.arizona.cs.mbel.emit.ClassEmitter;
import edu.arizona.cs.mbel.mbel.ModuleParser;
import edu.arizona.cs.mbel.parse.MSILParseException;

/**
 * This class is the abstract parent of all the Instruction classes. It contains the factory method
 * parseInstruction which will return the correct subclass of Instruction when given an input stream.
 * This class also has some methods that provide default behavior for many of the instruction subclasses.
 * An instruction on disk consists of a 1 or 2-byte opcode, optionally followed by additional information.
 * If the instruction opcode is 2 bytes long, the first byte will be 0xFE. Certain instructions
 * may also optionally be prefixed by special values. The factory method will properly parse those as well.
 * The possible prefixes are .tail, .unaligned, and .volatile, and are represented by the TailPrefixInstruction,
 * UnalignedPrefixInstruction, and VolatilePrefixInstruciton interfaces.
 * Each instruction subclass has a constructor of type (int opcode, ClassParser parse). This is called by
 * the factory method, which has already parsed the opcode of the instruction, and leaves the remaining
 * parsing to the specific instruction class. The ClassParser is also used to resolve tokens to MBEL references.
 *
 * @author Michael Stepp
 */
public abstract class Instruction
{
	private static final Hashtable CLASS_HASH = new Hashtable(101);
	private int opcode;

	static
	{
		// initialize the CLASS_HASH table
		int i = 0;
		Class[] CLASSES = new Class[88];
		CLASSES[i++] = ADD.class;
		CLASSES[i++] = ADD_OVF.class;
		CLASSES[i++] = AND.class;
		CLASSES[i++] = ARGLIST.class;
		CLASSES[i++] = BEQ.class;
		CLASSES[i++] = BGE.class;
		CLASSES[i++] = BGT.class;
		CLASSES[i++] = BLE.class;
		CLASSES[i++] = BLT.class;
		CLASSES[i++] = BNE_UN.class;
		CLASSES[i++] = BR.class;
		CLASSES[i++] = BREAK.class;
		CLASSES[i++] = BRFALSE.class;
		CLASSES[i++] = BRTRUE.class;
		CLASSES[i++] = CALL.class;
		CLASSES[i++] = CALLI.class;
		CLASSES[i++] = CEQ.class;
		CLASSES[i++] = CGT.class;
		CLASSES[i++] = CKFINITE.class;
		CLASSES[i++] = CLT.class;
		CLASSES[i++] = CONV.class;
		CLASSES[i++] = CONV_OVF.class;
		CLASSES[i++] = CONV_OVF_UN.class;
		CLASSES[i++] = CPBLK.class;
		CLASSES[i++] = DIV.class;
		CLASSES[i++] = DUP.class;
		CLASSES[i++] = ENDFILTER.class;
		CLASSES[i++] = ENDFINALLY.class;
		CLASSES[i++] = INITBLK.class;
		CLASSES[i++] = JMP.class;
		CLASSES[i++] = LDARG.class;
		CLASSES[i++] = LDARGA.class;
		CLASSES[i++] = LDC.class;
		CLASSES[i++] = LDFTN.class;
		CLASSES[i++] = LDIND.class;
		CLASSES[i++] = LDLOC.class;
		CLASSES[i++] = LDLOCA.class;
		CLASSES[i++] = LDNULL.class;
		CLASSES[i++] = LEAVE.class;
		CLASSES[i++] = LOCALLOC.class;
		CLASSES[i++] = MUL.class;
		CLASSES[i++] = MUL_OVF.class;
		CLASSES[i++] = NEG.class;
		CLASSES[i++] = NOP.class;
		CLASSES[i++] = NOT.class;
		CLASSES[i++] = OR.class;
		CLASSES[i++] = POP.class;
		CLASSES[i++] = REM.class;
		CLASSES[i++] = RET.class;
		CLASSES[i++] = SHL.class;
		CLASSES[i++] = SHR.class;
		CLASSES[i++] = STARG.class;
		CLASSES[i++] = STIND.class;
		CLASSES[i++] = STLOC.class;
		CLASSES[i++] = SUB.class;
		CLASSES[i++] = SUB_OVF.class;
		CLASSES[i++] = SWITCH.class;
		CLASSES[i++] = XOR.class;

		CLASSES[i++] = BOX.class;
		CLASSES[i++] = CALLVIRT.class;
		CLASSES[i++] = CASTCLASS.class;
		CLASSES[i++] = CPOBJ.class;
		CLASSES[i++] = INITOBJ.class;
		CLASSES[i++] = ISINST.class;
		CLASSES[i++] = LDELEM.class;
		CLASSES[i++] = LDELEMA.class;
		CLASSES[i++] = LDFLD.class;
		CLASSES[i++] = LDFLDA.class;
		CLASSES[i++] = LDLEN.class;
		CLASSES[i++] = LDOBJ.class;
		CLASSES[i++] = LDSFLD.class;
		CLASSES[i++] = LDSFLDA.class;
		CLASSES[i++] = LDSTR.class;
		CLASSES[i++] = LDTOKEN.class;
		CLASSES[i++] = LDVIRTFTN.class;
		CLASSES[i++] = MKREFANY.class;
		CLASSES[i++] = NEWARR.class;
		CLASSES[i++] = NEWOBJ.class;
		CLASSES[i++] = REFANYTYPE.class;
		CLASSES[i++] = REFANYVAL.class;
		CLASSES[i++] = RETHROW.class;
		CLASSES[i++] = SIZEOF.class;
		CLASSES[i++] = STELEM.class;
		CLASSES[i++] = STFLD.class;
		CLASSES[i++] = STOBJ.class;
		CLASSES[i++] = STSFLD.class;
		CLASSES[i++] = THROW.class;
		CLASSES[i++] = UNBOX.class;

		int[] OPCODE_LIST = null;
		try
		{
			for(Class CLASS : CLASSES)
			{
				Field opcodes = CLASS.getDeclaredField("OPCODE_LIST");
				OPCODE_LIST = (int[]) opcodes.get(null);
				for(int k = 0; k < OPCODE_LIST.length; k++)
				{
					CLASS_HASH.put(new Integer(OPCODE_LIST[k]), CLASS);
				}
			}
		}
		catch(Exception ex)
		{
		}
	}

	protected Instruction(int op, int[] opcodes) throws InstructionInitException
	{
		opcode = op;
		if(opcodes == null)
		{
			throw new InstructionInitException("Invalid opcode in initializer");
		}
		boolean found = false;
		for(int opcode1 : opcodes)
		{
			if(op == opcode1)
			{
				found = true;
				break;
			}
		}
		if(!found)
		{
			throw new InstructionInitException("Invalid opcode in initializer");
		}
	}

	public String toString()
	{
		return getName();
	}

	/**
	 * Returns the length in bytes of this instruction, as would be found on disk.
	 * This value includes any prefixes prepended to this instruction.
	 */
	public int getLength()
	{
		// returns the number of bytes this instruction
		// takes up (including any prefixes)
		int sum = 0;
		if(this instanceof TailPrefixInstruction)
		{
			sum += (((TailPrefixInstruction) this).hasTailPrefix() ? 2 : 0);
		}
		if(this instanceof UnalignedPrefixInstruction)
		{
			sum += (((UnalignedPrefixInstruction) this).hasUnalignedPrefix() ? 3 : 0);
		}
		if(this instanceof VolatilePrefixInstruction)
		{
			sum += (((VolatilePrefixInstruction) this).hasVolatilePrefix() ? 2 : 0);
		}

		sum += (((opcode & 0xFF) == 0xFE) ? 2 : 1);
		return sum;
	}

	/**
	 * Returns the opcode for this instruction object (may vary from instance to instance)
	 */
	public int getOpcode()
	{
		return opcode;
	}
   
   /*
   public void output(){
      System.out.print(getName());
   }
   */

	/**
	 * Returns the string representation of this instruction (may vary instance-to-instance)
	 *
	 * @return a string representing this instruction (i.e. "cpblk")
	 */
	public abstract String getName();

	/**
	 * Writes the instruction to the given buffer in raw byte form
	 *
	 * @param buffer  the buffer to write to
	 * @param emitter a ClassEmitter for reconciling references to tokens
	 */
	protected void emit(ByteBuffer buffer, ClassEmitter emitter)
	{
		// this implementation is a default one for instructions
		// with no arguments
		if(this instanceof TailPrefixInstruction)
		{
			TailPrefixInstruction tailP = (TailPrefixInstruction) this;
			if(tailP.hasTailPrefix())
			{
				buffer.putWORD(TailPrefixInstruction.TAIL);
			}
		}
		if(this instanceof UnalignedPrefixInstruction)
		{
			UnalignedPrefixInstruction unalignedP = (UnalignedPrefixInstruction) this;
			if(unalignedP.hasUnalignedPrefix())
			{
				buffer.putWORD(UnalignedPrefixInstruction.UNALIGNED);
				buffer.put(unalignedP.getAlignment());
			}
		}
		if(this instanceof VolatilePrefixInstruction)
		{
			VolatilePrefixInstruction volatileP = (VolatilePrefixInstruction) this;
			if(volatileP.hasVolatilePrefix())
			{
				buffer.putWORD(VolatilePrefixInstruction.VOLATILE);
			}
		}

		if((opcode & 0xFF) == 0xFE)
		{
			// 2-byte opcode
			buffer.putWORD(opcode);
		}
		else
		{
			// 1 byte opcode
			buffer.put(opcode);
		}
	}

	/**
	 * The Instruction factory method for parsing instructions from a buffer.
	 * The buffer is parse.getMSILInputStream().
	 *
	 * @param parse the ClassParser used to parse this module
	 * @return an Instruction object (i.e. an instance of the correct subclass)
	 */
	public static Instruction readInstruction(ModuleParser parse) throws IOException,
			MSILParseException
	{
		boolean tailP = false;
		boolean volatileP = false;
		boolean unalignedP = false;
		int unalignedByte = 0;
		int input;

		MSILInputStream in = parse.getMSILInputStream();

		input = in.readBYTE();
		if(input == 0xFE)
		{
			input |= (in.readBYTE() << 8);
			if(input == TailPrefixInstruction.TAIL)
			{
				tailP = true;
			}
			else if(input == UnalignedPrefixInstruction.UNALIGNED)
			{
				unalignedP = true;
				unalignedByte = in.readUINT8();
				if(!(unalignedByte == 1 || unalignedByte == 2 || unalignedByte == 4))
				{
					throw new MSILParseException("Instruction.readInstruction: \'unaligned.\' prefix alignment value is not one of 1,2, " +
							"" + "or 4");
				}

				input = in.readBYTE();
				if(input == 0xFE)
				{
					input |= (in.readBYTE() << 8);
					if(input == VolatilePrefixInstruction.VOLATILE)
					{
						volatileP = true;
					}
				}
			}
			else if(input == VolatilePrefixInstruction.VOLATILE)
			{
				volatileP = true;

				input = in.readBYTE();
				if(input == 0xFE)
				{
					input |= (in.readBYTE() << 8);
					if(input == UnalignedPrefixInstruction.UNALIGNED)
					{
						unalignedP = true;
						unalignedByte = in.readUINT8();
						if(!(unalignedByte == 1 || unalignedByte == 2 || unalignedByte == 4))
						{
							throw new MSILParseException("Instruction.readInstruction: \'unaligned.\' prefix alignment value is not one of 1,2," +
									"" + " or 4");
						}
					}
				}
			}
		}

		Object obj = CLASS_HASH.get(new Integer(input));
		if(obj == null || !(obj instanceof Class))
		{
			throw new MSILParseException("Instruction.readInstruction: Invalid instruction code");
		}

		Class clazz = (Class) obj;
		Class[] parseParams = {
				int.class,
				ModuleParser.class
		};

		try
		{
			Constructor newInst = clazz.getConstructor(parseParams);
			Instruction instr = (Instruction) newInst.newInstance(new Object[]{
					new Integer(input),
					parse
			});
			if(instr instanceof TailPrefixInstruction)
			{
				((TailPrefixInstruction) instr).setTailPrefix(tailP);
			}
			if(instr instanceof UnalignedPrefixInstruction)
			{
				((UnalignedPrefixInstruction) instr).setUnalignedPrefix(unalignedP);
				((UnalignedPrefixInstruction) instr).setAlignment(unalignedByte);
			}
			if(instr instanceof VolatilePrefixInstruction)
			{
				((VolatilePrefixInstruction) instr).setVolatilePrefix(volatileP);
			}
			return instr;
		}
		catch(Exception e)
		{
		}

		return null;
	}

	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof Instruction))
		{
			return false;
		}
		Instruction i = (Instruction) o;
		return (i.getOpcode() == opcode);
	}
}
