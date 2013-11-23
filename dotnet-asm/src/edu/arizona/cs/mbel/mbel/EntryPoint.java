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
 * This class represents the Entry Point for a .NET module. Not all modules
 * will know their entrypoint, as in DLLs. The entry point in metadata will either be a
 * method token (meaning that the Main method is defined in this module) or a File token
 * (pointing to the module where it is defined).
 *
 * @author Michael Stepp
 */
public class EntryPoint
{
	private MethodDef method;
	private FileReference file;
	// either one or the other

	/**
	 * Makes an EntryPoint object saying the given method is the entry point
	 *
	 * @param meth the Main method (entry point)
	 */
	public EntryPoint(MethodDef meth)
	{
		method = meth;
	}

	/**
	 * Makes an EntryPoint object saying that the given File contains the real entrypoint
	 */
	public EntryPoint(FileReference ref)
	{
		file = ref;
	}

	/**
	 * Returns the method of the entrypoint (if defined, may be null)
	 */
	public MethodDef getEntryPointMethod()
	{
		return method;
	}

	/**
	 * Returns the File in which the Entry Point is defined (may be null)
	 */
	public FileReference getEntryPointFile()
	{
		return file;
	}
   
/*
   public void output(){
      System.out.print("EntryPoint[");
      if (file!=null){
         System.out.print("File=");
         file.output();
      }else{
         System.out.print("Method=");
         method.output();
      }
      System.out.print("]");
   }
*/
}
