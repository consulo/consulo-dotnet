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
 * Represents a Filtered exception clause. A Filtered clause has a special
 * block of code called the filter which comes before the handler. By definition,
 * the handler must start immediately after the filter. Thus getFilterEnd().getNext()
 * should equal getHandlerStart(). Changing the handler start will also change the filter end,
 * and changing the filter end will change the handler start. The instruction pointed to by
 * getFilterEnd() must be an ENDFILTER instruction.
 *
 * @author Michael Stepp
 */
public class FilteredExceptionClause extends StructuredExceptionClause
{
	private InstructionHandle filterStart, filterEnd;

	/**
	 * Creates a FilteredExceptionClause with the given try, handler, and filter blocks
	 *
	 * @param fs the start of the filter block
	 * @param fe the end of the filter block
	 * @param ts the start of the try block
	 * @param te the end of the try block
	 * @param hs the start of the handler block
	 * @param he the end of the handler block
	 */
	public FilteredExceptionClause(InstructionHandle fs, InstructionHandle fe, InstructionHandle ts, InstructionHandle te, InstructionHandle hs,
			InstructionHandle he)
	{
		super(ts, te, hs, he);
		filterStart = fs;
		filterEnd = fe;

		if(filterStart != null)
		{
			filterStart.addTargeter(this);
		}
		if(filterEnd != null)
		{
			filterEnd.addTargeter(this);
		}
	}

	/**
	 * Sets the handle of the start of the handler block.
	 * This method will also call set the end of the filter block to ih.getPrev(),
	 * assuming ih!=null.
	 */
	public void setHandlerStart(InstructionHandle ih)
	{
		super.setHandlerStart(ih);
		if(ih != null)
		{
			InstructionHandle temp = ih.getPrev();
			if(filterEnd != null)
			{
				filterEnd.removeTargeter(this);
			}
			if(temp != null)
			{
				temp.addTargeter(this);
			}
			filterEnd = temp;
		}
	}

	/**
	 * Returns the handle to the start of the filter block
	 */
	public InstructionHandle getFilterStart()
	{
		return filterStart;
	}

	/**
	 * Sets the handle of the start of the filter block
	 */
	public void setFilterStart(InstructionHandle ih)
	{
		if(filterStart != null)
		{
			filterStart.removeTargeter(this);
		}
		if(ih != null)
		{
			ih.addTargeter(this);
		}
		filterStart = ih;
	}

	/**
	 * Sets the handle of the end of the filter block.
	 * Since, by definition, the handler block starts immediately after the filter block,
	 * this method will set the start of the handler also (assuming that ih!=null)
	 */
	public void setFilterEnd(InstructionHandle ih)
	{
		if(filterEnd != null)
		{
			filterEnd.removeTargeter(this);
		}
		if(ih != null)
		{
			ih.addTargeter(this);
		}
		filterEnd = ih;
		if(ih != null)
		{
			InstructionHandle temp = ih.getNext();
			if(handlerStart != null)
			{
				handlerStart.removeTargeter(this);
			}
			if(temp != null)
			{
				temp.addTargeter(this);
			}
			handlerStart = temp;
		}
	}

	/**
	 * Returns the handle to the end of the filter block
	 */
	public InstructionHandle getFilterEnd()
	{
		return filterEnd;
	}

	public boolean containsTarget(InstructionHandle ih)
	{
		return (super.containsTarget(ih) || (filterStart == ih) || (filterEnd == ih));
	}

	/**
	 * If multiple copies of oldh, the filter handles are changed first.
	 */
	public void updateTarget(InstructionHandle oldh, InstructionHandle newh)
	{
		if(filterStart == oldh)
		{
			if(oldh != null)
			{
				oldh.removeTargeter(this);
			}
			if(newh != null)
			{
				newh.addTargeter(this);
			}
			filterStart = newh;
		}
		else if(filterEnd == oldh)
		{
			if(oldh != null)
			{
				oldh.removeTargeter(this);
			}
			if(newh != null)
			{
				newh.addTargeter(this);
			}
			filterEnd = newh;
		}
		else
		{
			super.updateTarget(oldh, newh);
		}
	}
   
/*
   public void output(){
      System.out.print("FilteredExceptionClause[");
      System.out.print("\n  FilterStart=[");
      filterStart.output();
      System.out.print("]\n  FilterEnd=[");
      filterEnd.output();
      System.out.print("]\n  TryStart=[");
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
