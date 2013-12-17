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

import edu.arizona.cs.mbel.signature.EventAttributes;

/**
 * Represents a .NET event. Events may have references to their
 * add, remove, and fire methods, and also their handler type.
 *
 * @author Michael Stepp
 */
public class Event implements EventAttributes
{
	private long EventRID = -1L;

	private int EventFlags;
	private String Name;
	private Object EventType; // type def, type ref, type spec
	private MethodDef addOn, removeOn, fire;

	private Vector eventAttributes;

	/**
	 * Makes a new Event with the given name, flags, and handler type
	 *
	 * @param name    the name of this event
	 * @param flags   a bit vector of event flags, as defined in EventAttributes
	 * @param handler the TypeRef of the handler for this event
	 */
	public Event(String name, int flags, Object handler)
	{
		Name = name;
		EventFlags = flags;
		EventType = handler;

		eventAttributes = new Vector(10);
	}

	/**
	 * Adds a CustomAttribute to this event
	 */
	public void addEventAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			eventAttributes.add(ca);
		}
	}

	/**
	 * Returns a non-null array of CustomAttributes on this Event
	 */
	public CustomAttribute[] getEventAttributes()
	{
		CustomAttribute[] cas = new CustomAttribute[eventAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (CustomAttribute) eventAttributes.get(i);
		}
		return cas;
	}

	/**
	 * Removes a CustomAttribute from this Event
	 */
	public void removeEventAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			eventAttributes.remove(ca);
		}
	}

	/**
	 * Returns the Event RID of this Event (used by emitter)
	 */
	public long getEventRID()
	{
		return EventRID;
	}

	/**
	 * Sets the Event RID for this event (used by emitter).
	 * This method can only be called once.
	 */
	public void setEventRID(long rid)
	{
		if(EventRID == -1L)
		{
			EventRID = rid;
		}
	}

	/**
	 * Returns the "AddOn" method for this event
	 */
	public MethodDef getAddOnMethod()
	{
		return addOn;
	}

	/**
	 * Sets the "AddOn" method for this Event
	 */
	public void setAddOnMethod(MethodDef add)
	{
		addOn = add;
	}

	/**
	 * Returns the "RemoveOn" method for this event
	 */
	public MethodDef getRemoveOnMethod()
	{
		return removeOn;
	}

	/**
	 * Sets the "RemoveOn" method for this event
	 */
	public void setRemoveOnMethod(MethodDef rem)
	{
		removeOn = rem;
	}

	/**
	 * Returns the "Fire" method for this event
	 */
	public MethodDef getFireMethod()
	{
		return fire;
	}

	/**
	 * Sets the "Fire" method for this event
	 */
	public void setFireMethod(MethodDef f)
	{
		fire = f;
	}

	/**
	 * Returns the TypeRef of the handler for this event
	 */
	public Object getEventType()
	{
		return EventType;
	}

	/**
	 * Sets the TypeRef for the handler for this event
	 */
	public void setEventType(Object type)
	{
		EventType = type;
	}

	/**
	 * Returns the bit vector of flags for this event (as defined in EventAttributes)
	 */
	public int getEventFlags()
	{
		return EventFlags;
	}

	/**
	 * Sets the flags for this event
	 */
	public void setEventFlags(int flags)
	{
		EventFlags = flags;
	}

	/**
	 * Returns the name of this event
	 */
	public String getName()
	{
		return Name;
	}

	/**
	 * Sets the name of this event
	 */
	public void setName(String name)
	{
		Name = name;
	}

	/**
	 * Compares 2 events. Events are equal if they have the same name (within a TypeDef)
	 */
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof Event))
		{
			return false;
		}
		Event e = (Event) o;
		return Name.equals(e.getName());
	}
   
/*
   public void output(){
      System.out.print("Event[Name=\""+Name+"\", Type=");
      EventType.output();
      if (addOn!=null){
         System.out.print(", AddOn="+addOn.getName());
      }
      if (removeOn!=null){
         System.out.print(", RemoveOn="+removeOn.getName());
      }
      if (fire!=null){
         System.out.print(", Fire="+fire.getName());
      }
      System.out.print("]");
   }
*/
}