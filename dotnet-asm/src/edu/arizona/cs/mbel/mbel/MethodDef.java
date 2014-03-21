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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.consulo.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import edu.arizona.cs.mbel.signature.MethodAttributes;
import edu.arizona.cs.mbel.signature.MethodImplAttributes;
import edu.arizona.cs.mbel.signature.MethodSignature;

/**
 * This class represents a .NET Method. Not all methods will have a MethodBody. A method may
 * optionally have a MethodSemantics instance, an ImplementationMap, a DeclSecurity, and a native RVA.
 *
 * @author Michael Stepp
 */
public class MethodDef extends MethodDefOrRef implements MethodAttributes,
		MethodImplAttributes, HasSecurity, GenericParamOwner
{
	private long MethodRID = -1L;

	private int ImplFlags;
	private int Flags;
	private MethodSemantics semantics;
	private ImplementationMap implMap;
	private DeclSecurity security;
	private MethodSignature signature;
	private long methodRVA = -1L;

	private List<GenericParamDef> myGenericParamDefs = Collections.emptyList();

	/**
	 * Makes a Method with the given name, implementation flags, method flags, signature, and parent type
	 *
	 * @param name      the name of this method
	 * @param implFlags a bit vector of flags for the method implementation (defined in MethodImplAttributes)
	 * @param flags     a bit vector of flags for this method (defined in MethodAttributes)
	 * @param sig       the signature of this method
	 */
	public MethodDef(String name, int implFlags, int flags, MethodSignature sig)
	{
		super(name, null);
		signature = sig;
		ImplFlags = implFlags;
		Flags = flags;
	}

	/**
	 * Sets the RVA of this method (only for native methods)
	 */
	protected void setMethodRVA(long rva)
	{
		methodRVA = rva;
	}

	/**
	 * Returns the RVA of the start of this method in the module (only used for native methods)
	 */
	public long getMethodRVA()
	{
		return methodRVA;
	}

	/**
	 * Returns the Method RID of this Method (used by emitter)
	 */
	public long getMethodRID()
	{
		return MethodRID;
	}

	/**
	 * Sets the Method RID of this Method (used by emitter).
	 * This method can only be called once.
	 */
	public void setMethodRID(long rid)
	{
		if(MethodRID == -1L)
		{
			MethodRID = rid;
		}
	}

	/**
	 * Returns the method signature for this method.
	 * This is a definition signature, not a callsite signature.
	 */
	public MethodSignature getSignature()
	{
		return signature;
	}

	/**
	 * Sets the method signature for this method
	 */
	public void setSignature(MethodSignature sig)
	{
		signature = sig;
	}

	/**
	 * Returns the MethodSemantics for this method (if any)
	 */
	public MethodSemantics getMethodSemantics()
	{
		return semantics;
	}

	/**
	 * Sets the MethodSemantics for this method.
	 * Passing null removes the MethodSemantics.
	 */
	public void setMethodSemantics(MethodSemantics sem)
	{
		semantics = sem;
	}

	/**
	 * Returns the DeclSecurity on this Method (if any)
	 */
	public DeclSecurity getDeclSecurity()
	{
		return security;
	}

	/**
	 * Sets the DeclSecurity in this method.
	 * Passing null removes the DeclSecurity.
	 */
	public void setDeclSecurity(DeclSecurity decl)
	{
		if(decl == null)
		{
			Flags &= ~HasSecurity;
		}
		else
		{
			Flags |= HasSecurity;
		}

		security = decl;
	}

	/**
	 * Returns the Implementation map for this method (if any)
	 */
	public ImplementationMap getImplementationMap()
	{
		return implMap;
	}

	/**
	 * Sets the ImplementationMap for this method.
	 * Passing null removes the implementation map.
	 */
	public void setImplementationMap(ImplementationMap map)
	{
		implMap = map;
	}

	/**
	 * Sets the parent type of this method (this is called in TypeDef.addMethod)
	 * This method does not succeed if ref is not a TypeDef.
	 */
	public void setParent(AbstractTypeReference ref)
	{
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
	 * Returns a bit vector of implementation flags (defined in MethodImplAttributes)
	 */
	public int getImplFlags()
	{
		return ImplFlags;
	}

	/**
	 * Sets the implementation flags
	 */
	public void setImplFlags(int impl)
	{
		ImplFlags = impl;
	}

	/**
	 * Returns a bit vector of method flags (defined in MethodAttributes)
	 */
	public int getFlags()
	{
		return Flags;
	}

	/**
	 * Sets the method flags
	 */
	public void setFlags(int flags)
	{
		Flags = flags;
	}

	@Override
	public void addGenericParam(GenericParamDef genericParamDef)
	{
		if(myGenericParamDefs == Collections.<GenericParamDef>emptyList())
		{
			myGenericParamDefs = new ArrayList<GenericParamDef>(5);
		}
		myGenericParamDefs.add(genericParamDef);
	}

	@NotNull
	@Override
	@Immutable
	public List<GenericParamDef> getGenericParams()
	{
		return myGenericParamDefs;
	}


/*
   public void output(){
      System.out.print("Method[Name=\""+getName()+"\", Signature=");
      signature.output();
      if (semantics!=null){
         System.out.print(", Semantics=");
         semantics.output();
      }
      if (implMap!=null){
         System.out.print(", ImplMap=");
         implMap.output();
      }
      if (security!=null){
         System.out.print(", Security=");
         security.output();
      }
      System.out.print("]");
   }
*/
}
