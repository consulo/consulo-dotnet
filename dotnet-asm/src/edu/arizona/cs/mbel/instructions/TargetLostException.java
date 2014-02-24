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

import java.lang.Exception;

/**
 * Thrown when an Instructionhandle is removed from an InstructionList,
 * but the list still has other handles that target it.
 *
 * @author Michael Stepp
 */
public final class TargetLostException extends Exception
{
	private InstructionHandle[] handles;

	/**
	 * Makes a TargetLostException, taking the list of handles that were removed
	 * that had targeters elsewhere in the list.
	 */
	public TargetLostException(InstructionHandle[] lost)
	{
		handles = lost;
	}

	/**
	 * Returns the list of handles that still had targeters.
	 */
	public InstructionHandle[] getTargets()
	{
		return handles;
	}
}
