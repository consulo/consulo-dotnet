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
 * This class represents a .NET property. Properties have a name and property signature,
 * as well as references to their getter and setter methods. A Property may optionally have
 * a default value, given in raw byte array form.
 *
 * @author Michael Stepp
 */
public class Property implements edu.arizona.cs.mbel.signature.PropertyAttributes
{
	private long PropertyRID = -1L;

	private int Flags;
	private String Name;
	private edu.arizona.cs.mbel.signature.PropertySignature signature;
	private Method getter, setter;
	private byte[] defaultValue;

	private java.util.Vector propertyAttributes;

	/**
	 * Makes a new Property with the given name, flags, and signature
	 *
	 * @param name  the name of the property
	 * @param flags a bit vector of flags (defined in PropertyAttributes)
	 * @param sig   the property signature for this property
	 */
	public Property(String name, int flags, edu.arizona.cs.mbel.signature.PropertySignature sig)
	{
		Name = name;
		Flags = flags;
		signature = sig;

		propertyAttributes = new java.util.Vector(10);
	}

	/**
	 * Adds a CustomAttribute to this Property
	 */
	public void addPropertyAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			propertyAttributes.add(ca);
		}
	}

	/**
	 * Returns a non-null array of CustomAttributes on this Property
	 */
	public CustomAttribute[] getPropertyAttributes()
	{
		CustomAttribute[] cas = new CustomAttribute[propertyAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (CustomAttribute) propertyAttributes.get(i);
		}
		return cas;
	}

	/**
	 * Removes a CustomAttribute from this Property
	 */
	public void removePropertyAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			propertyAttributes.remove(ca);
		}
	}

	/**
	 * Returns the Property RID of this Property (used by emitter)
	 */
	public long getPropertyRID()
	{
		return PropertyRID;
	}

	/**
	 * Sets the Property RID of this Property (used by emitter).
	 * This method can only be called once.
	 */
	public void setPropertyRID(long rid)
	{
		if(PropertyRID == -1L)
		{
			PropertyRID = rid;
		}
	}

	/**
	 * Returns the default value for this property, or null if none
	 */
	public byte[] getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Sets the default value for this property. Note that this method does not
	 * enforce that the byte given is the correct size for this property's underlying type.
	 * Passing null or a byte array with 0 length removes the default value.
	 */
	public void setDefaultValue(byte[] blob)
	{
		if(blob == null || blob.length == 0)
		{
			Flags &= ~HasDefault;
			defaultValue = null;
		}
		else
		{
			Flags |= HasDefault;
			defaultValue = blob;
		}
	}

	/**
	 * Returns the method reference of the Getter of this property
	 */
	public Method getGetter()
	{
		return getter;
	}

	/**
	 * Sets the Getter method for this property
	 */
	public void setGetter(Method get)
	{
		getter = get;
	}

	/**
	 * Returns the method reference of the Setter of this property
	 */
	public Method getSetter()
	{
		return setter;
	}

	/**
	 * Sets the Setter method for this property
	 */
	public void setSetter(Method set)
	{
		setter = set;
	}

	/**
	 * Returns the name fot his property
	 */
	public String getName()
	{
		return Name;
	}

	/**
	 * Sets the name of this property
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * Returns a bit vector of flags for this property (defined in PropertyAttributes)
	 */
	public int getFlags()
	{
		return Flags;
	}

	/**
	 * Sets the flags for this property
	 */
	public void setFlags(int flags)
	{
		Flags = flags;
	}

	/**
	 * Returns the property signature for this property
	 */
	public edu.arizona.cs.mbel.signature.PropertySignature getSignature()
	{
		return signature;
	}

	/**
	 * Sets the property signature for this property
	 */
	public void setSignature(edu.arizona.cs.mbel.signature.PropertySignature sig)
	{
		signature = sig;
	}

	/**
	 * Compares 2 properties
	 * Returns true iff the names are equal (within a TypeDef)
	 */
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof Property))
		{
			return false;
		}
		Property p = (Property) o;
		return Name.equals(p.Name);
	}
   
/*
   public void output(){
      System.out.print("Property[Name=\""+Name+"\", Signature=");
      signature.output();
      if (getter!=null){
         System.out.print(", Getter="+getter.getName());
      }
      if (setter!=null){
         System.out.print(", Setter="+setter.getName());
      }
      System.out.print("]");
   }
*/
}
