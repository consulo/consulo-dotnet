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

import edu.arizona.cs.mbel.signature.MethodSemanticsAttributes;

/**
 * This class represents a set of special semantics for a method. These special
 * semantics can be property getters or setters, or event methods. While a MethodSemantics
 * has both getEvent and getProperty methods, only one of these will be valid for a
 * given instance.
 *
 * @author Michael Stepp
 */
public class MethodSemantics implements MethodSemanticsAttributes
{
	// Setter, Getter, Other, AddOn, RemoveOn, Fire
	private int Semantics;  // 2 bytes
	private Event event;
	private Property property;
	// only one of these will be nonnull

	/**
	 * Makes a MethodSemantics object with the given semantics code and Event parent.
	 *
	 * @param sem the semantics code value (from MethodSemanticsAttributes). Legal values are AddOn, RemoveOn, Fire, or Other
	 * @param e   the event parent
	 */
	public MethodSemantics(int sem, Event e)
	{
		Semantics = sem;
		event = e;
	}

	/**
	 * Makes a MethodSemantics object with the given semantics code and Property parent.
	 *
	 * @param sem the semantics code value (from MethodSemanticsAttributes). Legal values are Setter, Getter, or Other
	 * @param p   the Property parent
	 */
	public MethodSemantics(int sem, Property p)
	{
		Semantics = sem;
		property = p;
	}

	/**
	 * Returns the semantics code value (defined in MethodSemanticsAttributes)
	 */
	public int getSemantics()
	{
		return Semantics;
	}

	/**
	 * Returns the Event parent for these MethodSemantics (if this is a property method, this will return null)
	 */
	public Event getEvent()
	{
		return event;
	}

	/**
	 * Returns the Property parent for these MethodSemantics (if this is an event method, this will return null)
	 */
	public Property getProperty()
	{
		return property;
	}
   
/*
   public void output(){
      System.out.print("MethodSemantics[Semantics="+Semantics);
      if (event!=null){
         System.out.print(", Event=");
         event.output();
      }else{
         System.out.print(", Property=");
         property.output();
      }
      System.out.print("]");
   }
*/
}
