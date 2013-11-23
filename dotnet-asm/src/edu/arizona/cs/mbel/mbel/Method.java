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

import edu.arizona.cs.mbel.instructions.CALL;
import edu.arizona.cs.mbel.instructions.InstructionList;
import edu.arizona.cs.mbel.instructions.LDARG;
import edu.arizona.cs.mbel.instructions.RET;
import edu.arizona.cs.mbel.signature.MethodAttributes;
import edu.arizona.cs.mbel.signature.MethodImplAttributes;
import edu.arizona.cs.mbel.signature.MethodSignature;
import edu.arizona.cs.mbel.signature.ReturnTypeSignature;
import edu.arizona.cs.mbel.signature.SignatureException;

/**
 * This class represents a .NET Method. Not all methods will have a MethodBody. A method may
 * optionally have a MethodSemantics instance, an ImplementationMap, a DeclSecurity, and a native RVA.
 *
 * @author Michael Stepp
 */
public class Method extends MethodDefOrRef implements MethodAttributes,
		MethodImplAttributes, HasSecurity
{
	private long MethodRID = -1L;

	private int ImplFlags;
	private int Flags;
	private MethodBody body;
	private MethodSemantics semantics;
	private ImplementationMap implMap;
	private DeclSecurity security;
	private MethodSignature signature;
	private long methodRVA = -1L;

	private Vector methodAttributes;

	/**
	 * This method will create a default constructor for any object. The constructor
	 * takes no arguments, has no local vars, and does nothing more than call Object.ctor().
	 * Of course, this should only be used by classes that directly extend System.Object.
	 * The access modifier for this Method will be Public.
	 */
	public static Method makeDefaultConstructor()
	{
		Method ctor = null;
		MethodRef super_ctor = null;
		try
		{
			MethodSignature callsitesig = new MethodSignature(new ReturnTypeSignature(ReturnTypeSignature.ELEMENT_TYPE_VOID), null);
			super_ctor = new MethodRef(".ctor", AssemblyTypeRef.OBJECT, callsitesig);

			ctor = new Method(".ctor", 0, (Method.Public | Method.HideBySig | Method.SpecialName | Method.RTSpecialName),
					new MethodSignature(new ReturnTypeSignature(ReturnTypeSignature.ELEMENT_TYPE_VOID), null));
		}
		catch(SignatureException se)
		{
		}

		MethodBody body = new MethodBody(true, 1, null);
		ctor.setMethodBody(body);

		InstructionList ilist = body.getInstructionList();
		ilist.append(new LDARG(LDARG.LDARG_0));
		ilist.append(new CALL(super_ctor));
		ilist.append(new RET());

		return ctor;
	}

	/**
	 * Makes a Method with the given name, implementation flags, method flags, signature, and parent type
	 *
	 * @param name      the name of this method
	 * @param implFlags a bit vector of flags for the method implementation (defined in MethodImplAttributes)
	 * @param flags     a bit vector of flags for this method (defined in MethodAttributes)
	 * @param sig       the signature of this method
	 * @param par       the TypeDef in which this method is defined
	 */
	public Method(String name, int implFlags, int flags, MethodSignature sig)
	{
		super(name, null);
		signature = sig;
		ImplFlags = implFlags;
		Flags = flags;
		methodAttributes = new Vector(10);
	}

	/**
	 * Adds a CustomAttribute to this Method
	 */
	public void addMethodAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			methodAttributes.add(ca);
		}
	}

	/**
	 * Returns a non-null array of CustomAttributes on this Method (Method)
	 */
	public CustomAttribute[] getMethodAttributes()
	{
		CustomAttribute[] cas = new CustomAttribute[methodAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (CustomAttribute) methodAttributes.get(i);
		}
		return cas;
	}

	/**
	 * Removes a CustomAttribute from this Method
	 */
	public void removeMethodAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			methodAttributes.remove(ca);
		}
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
	 * Returns the Method body of this Method (if any)
	 */
	public MethodBody getMethodBody()
	{
		return body;
	}

	/**
	 * Sets the methodbody of this method.
	 * Passing null removes the method body.
	 */
	public void setMethodBody(MethodBody mb)
	{
		body = mb;
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
