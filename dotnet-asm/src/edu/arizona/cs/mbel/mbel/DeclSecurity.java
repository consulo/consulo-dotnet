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

import java.util.Vector;

import edu.arizona.cs.mbel.signature.SecurityActions;

/**
 * This class contains the security permission information for certain .NET objects.
 * These canonly be applied to classes that implenent HasSecurity (AssemblyInfo, TypeDef, and Method).
 * The actual data of the security permission is an XML description of the persmission set, given
 * as a byte array of unicode. DeclSecuritys may also have CustomAttributes.
 *
 * @author Michael Stepp
 */
public class DeclSecurity implements SecurityActions
{
	private int Action;           // 2 byte constant
	private byte[] permissionSet;
	// blob that is an XML serialization of the permission set

	private Vector declSecurityAttributes;

	/**
	 * Makes a new DeclSecurity object with the given action code and permission set
	 *
	 * @param action     the security action code (defined in SecurityActions)
	 * @param permission a raw byte array representing an XML permission set (in unicode)
	 */
	public DeclSecurity(int action, byte[] permission)
	{
		Action = action;
		permissionSet = permission;

		declSecurityAttributes = new Vector(10);
	}

	/**
	 * Adds a CustomAttribute to this DeclSecurity
	 */
	public void addDeclSecurityAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			declSecurityAttributes.add(ca);
		}
	}

	/**
	 * Returns a non-null array of CustomAttributes on this DeclSecurity (DeclSecurity)
	 */
	public CustomAttribute[] getDeclSecurityAttributes()
	{
		CustomAttribute[] cas = new CustomAttribute[declSecurityAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (CustomAttribute) declSecurityAttributes.get(i);
		}
		return cas;
	}

	/**
	 * Removes a CustomAttribute fromt his DeclSecurity
	 */
	public void removeDeclSecurityAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			declSecurityAttributes.remove(ca);
		}
	}


	/**
	 * Returns the action code for this DeclSecurity (defined in SecurityActions)
	 */
	public int getAction()
	{
		return Action;
	}

	/**
	 * Returns the byte array of the XML permission ste for this DeclSecurity
	 */
	public byte[] getPermissionSet()
	{
		return permissionSet;
	}

/*   
   public void output(){
      System.out.print("DeclSecurity[Action="+Action+", Permission={ ");
      for (int i=0;(i+1)<permissionSet.length;i+=2){
         System.out.print((char)((permissionSet[i]&0xFF) | (permissionSet[i+1]&0xFF)<<8));
      }
      System.out.print(" }]");
   }
*/
}
