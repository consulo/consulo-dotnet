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
 * Abstract parent of all exception clause classes.
 * All exception clauses have a try block and a handler block, so this class
 * contains the start and end InstructionHandles of both of these blocks.
 *
 * @author Michael Stepp
 */
public abstract class StructuredExceptionClause implements InstructionTargeter
{
	protected InstructionHandle tryStart;
	protected InstructionHandle tryEnd;
	protected InstructionHandle handlerStart;
	protected InstructionHandle handlerEnd;

	/**
	 * Makes an exception clause with the given try and handler blocks.
	 * This will also register this StructuredExceptionClause as an InstructionTargeter to the given handles.
	 *
	 * @param ts the start of the try block
	 * @param te the end of the try block
	 * @param hs the start of the handler block
	 * @param he the end of the handler block
	 */
	public StructuredExceptionClause(InstructionHandle ts, InstructionHandle te, InstructionHandle hs, InstructionHandle he)
	{
		tryStart = ts;
		tryEnd = te;
		handlerStart = hs;
		handlerEnd = he;

		if(tryStart != null)
		{
			tryStart.addTargeter(this);
		}
		if(tryEnd != null)
		{
			tryEnd.addTargeter(this);
		}
		if(handlerStart != null)
		{
			handlerStart.addTargeter(this);
		}
		if(handlerEnd != null)
		{
			handlerEnd.addTargeter(this);
		}
	}

	/**
	 * Returns the InstructionHandle of the start of the try block
	 */
	public InstructionHandle getTryStart()
	{
		return tryStart;
	}

	/**
	 * Sets the handle fo the start of the try block
	 */
	public void setTryStart(InstructionHandle ih)
	{
		if(tryStart != null)
		{
			tryStart.removeTargeter(this);
		}
		if(ih != null)
		{
			ih.addTargeter(this);
		}
		tryStart = ih;
	}

	/**
	 * Returns the InstructionHandle of the end of the try block
	 */
	public InstructionHandle getTryEnd()
	{
		return tryEnd;
	}

	/**
	 * Sets the handle of the end of the try block
	 */
	public void setTryEnd(InstructionHandle ih)
	{
		if(tryEnd != null)
		{
			tryEnd.removeTargeter(this);
		}
		if(ih != null)
		{
			ih.addTargeter(this);
		}
		tryEnd = ih;
	}

	/**
	 * Returns the InstructionHandle of the start of the handler block
	 */
	public InstructionHandle getHandlerStart()
	{
		return handlerStart;
	}

	/**
	 * Sets the handle of the start of the handler block
	 */
	public void setHandlerStart(InstructionHandle ih)
	{
		if(handlerStart != null)
		{
			handlerStart.removeTargeter(this);
		}
		if(ih != null)
		{
			ih.addTargeter(this);
		}
		handlerStart = ih;
	}

	/**
	 * Returns the InstructionHandle of the end of the handler block
	 */
	public InstructionHandle getHandlerEnd()
	{
		return handlerEnd;
	}

	/**
	 * Sets the handle of the end of the handler block
	 */
	public void setHandlerEnd(InstructionHandle ih)
	{
		if(handlerEnd != null)
		{
			handlerEnd.removeTargeter(this);
		}
		if(ih != null)
		{
			ih.addTargeter(this);
		}
		handlerEnd = ih;
	}

	public boolean containsTarget(InstructionHandle ih)
	{
		return ((tryStart == ih) || (tryEnd == ih) || (handlerStart == ih) || (handlerEnd == ih));
	}

	public void updateTarget(InstructionHandle oldh, InstructionHandle newh)
	{
		if(tryStart == oldh)
		{
			if(oldh != null)
			{
				oldh.removeTargeter(this);
			}
			if(newh != null)
			{
				newh.addTargeter(this);
			}
			tryStart = newh;
		}
		else if(tryEnd == oldh)
		{
			if(oldh != null)
			{
				oldh.removeTargeter(this);
			}
			if(newh != null)
			{
				newh.addTargeter(this);
			}
			tryEnd = newh;
		}
		else if(handlerStart == oldh)
		{
			if(oldh != null)
			{
				oldh.removeTargeter(this);
			}
			if(newh != null)
			{
				newh.addTargeter(this);
			}
			handlerStart = newh;
		}
		else if(handlerEnd == oldh)
		{
			if(oldh != null)
			{
				oldh.removeTargeter(this);
			}
			if(newh != null)
			{
				newh.addTargeter(this);
			}
			handlerEnd = newh;
		}
	}
   
/*
   public abstract void output();
*/
}
