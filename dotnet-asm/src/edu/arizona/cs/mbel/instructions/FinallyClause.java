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

package edu.arizona.cs.mbel.instructions;

/**
 * Represents a Finally clause; the code after an excpeiton handler
 * block that is executed whether an exception is thrown or not.
 *
 * @author Michael Stepp
 */
public class FinallyClause extends StructuredExceptionClause
{
	/**
	 * Creates a finally clause with the given try block and handler block.
	 *
	 * @param ts the start of the try block
	 * @param te the end of the try block
	 * @param hs the start of the handler block
	 * @param he the end of the handler block
	 */
	public FinallyClause(InstructionHandle ts, InstructionHandle te, InstructionHandle hs, InstructionHandle he)
	{
		super(ts, te, hs, he);
	}
   
/*
   public void output(){
      System.out.print("FinallyClause[");
      System.out.print("\n  TryStart=[");
      getTryStart().output();
      System.out.print("]\n  TryEnd=[");
      getTryEnd().output();
      System.out.print("]\n  HandlerStart=[");
      getHandlerStart().output();
      System.out.print("]\n  HandlerEnd=[");
      getHandlerEnd().output();
      System.out.print("]\n]");
   }
*/
}
