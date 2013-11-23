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
 * This class represents a .NET Field. All Fields will be owned by TypeDefs, and any new fields
 * constructed by the user must be added to the parent TypeDef using TypeDef.addField(Field).
 *
 * @author Michael Stepp
 */
public class Field extends FieldRef implements edu.arizona.cs.mbel.signature.FieldAttributes
{
	private long FieldRID = -1L;

	private int Flags;
	private edu.arizona.cs.mbel.signature.MarshalSignature fieldMarshal;
	private byte[] defaultValue;  // only from Constant
	private long Offset = -1L;      // from FieldLayout (only present if Flags&ExplicitLayout)
	private long FieldRVA = -1L;

	private java.util.Vector fieldAttributes;

	/**
	 * Constructs a Field witht he given name, flags, and signature.
	 *
	 * @param name  the name of this Field
	 * @param flags a bit vector of flag values (defined in FieldAttributes)
	 * @param sig   the field signature for this field
	 */
	public Field(String name, int flags, edu.arizona.cs.mbel.signature.FieldSignature sig)
	{
		super(name, sig, null);
		Flags = flags;
		fieldAttributes = new java.util.Vector(10);
	}

	/**
	 * Convenience constructor for a private, non-static field.
	 * Note: the parent of this Field is set to null. The parent must be set by either
	 * a call to setParent or by a call to TypeDef.addField(this).
	 *
	 * @param name the name of this Field
	 * @param sig  the FieldSignature for this Field
	 */
	public Field(String name, edu.arizona.cs.mbel.signature.FieldSignature sig)
	{
		super(name, sig, null);
		Flags = Private;
		fieldAttributes = new java.util.Vector(10);
	}

	/**
	 * Adds a CustomAttribute to this Field
	 *
	 * @param ca the CustomAttribute
	 */
	public void addFieldAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			fieldAttributes.add(ca);
		}
	}

	/**
	 * Returns the CustomAttributes applied to this Field
	 *
	 * @return a non-null array of CustomAttributes
	 */
	public CustomAttribute[] getFieldAttributes()
	{
		CustomAttribute[] cas = new CustomAttribute[fieldAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (CustomAttribute) fieldAttributes.get(i);
		}
		return cas;
	}

	/**
	 * Removes a CustomAttribute from this Field
	 */
	public void removeFieldAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			fieldAttributes.remove(ca);
		}
	}


	protected void setFieldRVA(long rva)
	{
		FieldRVA = rva;
	}

	/**
	 * Returns the FieldRVA of this Field (used by emitter only)
	 */
	public long getFieldRVA()
	{
		return FieldRVA;
	}

	/**
	 * Returns the RID of this Field (used by emitter only)
	 */
	public long getFieldRID()
	{
		return FieldRID;
	}

	/**
	 * Sets the RID of this Field (used by emitter only)
	 *
	 * @param rid the RID to assign to this field
	 */
	public void setFieldRID(long rid)
	{
		if(FieldRID == -1L)
		{
			FieldRID = rid;
		}
	}

	/**
	 * Returns the default value for this Field in raw byte form or null if no default value is specified.
	 *
	 * @return an array of bytes corresponding to the default value of this field, or null if none
	 */
	public byte[] getDefaultValue()
	{
		// this applies for either Constant values or values contained at FieldRVAs
		return defaultValue;
	}

	/**
	 * Sets the default value of this field and the appropriate flag.
	 * Note that this method does not enforce that rawdata.length is the correct
	 * size considering the type of this field.
	 * If (rawdata==null || rawdata.length==0), turns off Flags.HasDefault
	 * If (rawdata!=null && rawdata.length!=0), turns on Flags.HasDefault
	 *
	 * @param rawdata the raw byte value to assign to this field
	 */
	public void setDefaultValue(byte[] rawdata)
	{
		if(rawdata == null || rawdata.length == 0)
		{
			Flags &= ~HasDefault;
			defaultValue = null;
		}
		else
		{
			Flags |= HasDefault;
			defaultValue = rawdata;
		}
	}

	/**
	 * Sets the parent of this Field (inherited from MemberRef.setParent()).
	 * Overrides this method so that only a TypeDef can be given as a parent to a Field.
	 *
	 * @param ref the new parent of this Field, only assigned if it is a non-null TypeDef.
	 */
	public void setParent(AbstractTypeReference ref)
	{
		// can set my parent to null, but not to a non-TypeDef
		if(ref == null)
		{
			super.setParent(ref);
		}
		else if(!(ref instanceof TypeDef))
		{
			return;
		}
		super.setParent(ref);
	}

	/**
	 * Sets the flag bit vector of this Field.
	 * Valid values are found in the FieldAttributes interface.
	 *
	 * @param flags the new flags to assign
	 */
	public void setFlags(int flags)
	{
		Flags = flags;
	}

	/**
	 * Gets the flag bit vector for this Field (values defined in FieldAttributes interface)
	 */
	public int getFlags()
	{
		return Flags;
	}

	/**
	 * Returns the FieldOffset of this field.
	 * The FieldOffset is the byte offset of this field from the start of its parent TypeDef.
	 *
	 * @return the FieldOffset of this field, or -1 if there is no specified offset
	 */
	public long getOffset()
	{
		return Offset;
	}

	/**
	 * Sets the FieldOffset of this field.
	 *
	 * @param off the new FieldOffset for this field. A value of -1 means no field offset.
	 */
	public void setOffset(long off)
	{
		// this field has a field layout iff Offset!=-1
		if(off < 0)
		{
			Offset = -1L;
			return;
		}
		Offset = off;
	}

	/**
	 * Returns the FieldMarshal information for this Field (or null if none defined)
	 */
	public edu.arizona.cs.mbel.signature.MarshalSignature getFieldMarshal()
	{
		return fieldMarshal;
	}

	/**
	 * Sets the FieldMarshal information for this field.
	 *
	 * @param sig the new FieldMarshal for this field. if sig==null, removes the FieldMarshal
	 */
	public void setFieldMarshal(edu.arizona.cs.mbel.signature.MarshalSignature sig)
	{
		if(sig == null)
		{
			Flags &= ~HasFieldMarshal;
		}
		else
		{
			Flags |= HasFieldMarshal;
		}
		fieldMarshal = sig;
	}

/*
   public void output(){
      System.out.print("Field[Name=\""+getName()+"\", Signature=");
      getSignature().output();
      if (fieldMarshal!=null){
         System.out.print(", FieldMarshal="+fieldMarshal);
      }
      System.out.print("]");
   }
*/
}
