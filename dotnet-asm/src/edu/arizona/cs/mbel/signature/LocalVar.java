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


package edu.arizona.cs.mbel.signature;

/**
 * This class models a single local var, whereas LocalVarList
 * contains a list of all the LocalVars for a whole method
 *
 * @author Michael Stepp
 */
public class LocalVar extends Signature
{
	private java.util.Vector constraints;  // Constraints
	private boolean byref;
	private TypeSignature type;

	/**
	 * Makes a LocalVar of the given type, possibly by reference
	 *
	 * @param BYREF true if the local var is passed by reference
	 * @param t     the type of the parameter
	 */
	public LocalVar(boolean BYREF, TypeSignature t) throws SignatureException
	{
		if(t == null)
		{
			throw new SignatureException("LocalVar: local var type is null");
		}
		byref = BYREF;
		type = t;
		constraints = new java.util.Vector(5);
	}

	/**
	 * Makes a LocalVar with the given type and constraints, possibly by reference
	 *
	 * @param con   an array of constraints
	 * @param BYREF true if this local var is passed by reference
	 * @param t     the type of this local var
	 */
	public LocalVar(Constraint con[], boolean BYREF, TypeSignature t) throws SignatureException
	{
		if(t == null)
		{
			throw new SignatureException("LocalVar: local var type is null");
		}
		byref = BYREF;
		type = t;
		constraints = new java.util.Vector(5);
		if(con != null)
		{
			for(int i = 0; i < con.length; i++)
			{
				if(con[i] != null)
				{
					constraints.add(con[i]);
				}
			}
		}
	}

	private LocalVar()
	{
	}

	/**
	 * Factory method for parsing a LocalVar from a raw binary blob
	 *
	 * @param buffer the buffer to read from
	 * @param group  a TypeGroup for reconciling tokens to mbel references
	 * @return a LocalVar representing the given blob, or null if there was a parse error
	 */
	public static LocalVar parse(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.mbel.TypeGroup group)
	{
		LocalVar blob = new LocalVar();

		blob.constraints = new java.util.Vector(5);
		int pos = buffer.getPosition();
		Constraint temp = Constraint.parse(buffer);
		while(temp != null)
		{
			blob.constraints.add(temp);
			pos = buffer.getPosition();
			temp = Constraint.parse(buffer);
		}
		buffer.setPosition(pos);

		byte data = buffer.peek();
		if(data == ELEMENT_TYPE_BYREF)
		{
			blob.byref = true;
			buffer.get();
		}
		blob.type = TypeSignature.parse(buffer, group);
		if(blob.type == null)
		{
			return null;
		}
		return blob;
	}

	/**
	 * Getter method for the Constraints applied to this local var
	 */
	public Constraint[] getConstraints()
	{
		Constraint[] sigs = new Constraint[constraints.size()];
		for(int i = 0; i < sigs.length; i++)
		{
			sigs[i] = (Constraint) constraints.get(i);
		}

		return sigs;
	}

	/**
	 * Returns whether or not this local var is used by reference
	 */
	public boolean isByRef()
	{
		return byref;
	}

	/**
	 * Returns the type of this local var
	 */
	public TypeSignature getType()
	{
		return type;
	}

	/**
	 * Writes this signature to a buffer in raw binary form
	 *
	 * @param buffer the buffer to write to
	 */
	public void emit(edu.arizona.cs.mbel.ByteBuffer buffer, edu.arizona.cs.mbel.emit.ClassEmitter emitter)
	{
		for(int i = 0; i < constraints.size(); i++)
		{
			((Constraint) constraints.get(i)).emit(buffer, emitter);
		}
		if(byref)
		{
			buffer.put(ELEMENT_TYPE_BYREF);
		}
		type.emit(buffer, emitter);
	}
   
/*
   public void output(){
      System.out.print("LocalVar[");
      for (int i=0;i<constraints.size();i++){
         System.out.print(constraints.get(i) + ",");
      }
      if (byref)
         System.out.print("BYREF,");
      type.output();
      System.out.print("]");
   }
*/
}
