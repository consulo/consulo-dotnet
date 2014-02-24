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
 * This interface is implemented by all classes that target instructions somehow.
 * Examples are BranchInstruction, SWITCH, and the StructuredExcepti0onClause classes.
 *
 * @author Michael Stepp
 */
public interface InstructionTargeter
{
	/**
	 * Returns true iff the given handle is targetted by this InstructionTargeter (comparison by reference)
	 */
	public boolean containsTarget(InstructionHandle ih);

	/**
	 * Updates one of this InstructionTargeter's targets.
	 * If this instance targets oldh (comparison by reference), then that target is replaced by newh.
	 * This method should also call oldh.removeTargeter(this) and newh.addTargeter(this) in every implementing class.
	 */
	public void updateTarget(InstructionHandle oldh, InstructionHandle newh);
}
