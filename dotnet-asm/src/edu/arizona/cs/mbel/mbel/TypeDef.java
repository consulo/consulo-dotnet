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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.arizona.cs.mbel.signature.TypeAttributes;

/**
 * This class represents a .NET type definition (analogous to a Class in Java).
 * A TypeDef can be Class, an Interface, or a ValueType. TypeDef extends TypeRef
 * both because of similar state and because a TypeRef can refer to a TypeDef in
 * its own module.
 *
 * @author Michael Stepp
 */
public class TypeDef extends TypeRef implements HasSecurity, TypeAttributes, GenericParamOwner
{
	private long TypeDefRID = -1L;

	private Vector events;
	private Vector fields;
	private Vector<MethodDef> methods;
	private Vector properties;
	private Vector interfaces;      // InterfaceImplementations
	private Vector attributes;
	private Vector nestedClasses;   // TypeDefs
	private Vector<MethodMap> methodMaps;      // MethodMaps
	////////////////////////////////////
	private long Flags;
	//////////////////////////
	private Module parent;
	private ClassLayout classLayout;
	private TypeRef superClass;               // will NOT be a TypeSpec
	private DeclSecurity security;

	private Vector typeDefAttributes;
	private List<GenericParamDef> myGenericParamDefs;

	/**
	 * Constructs a TypeDef with the given namespace, name and flags
	 *
	 * @param ns    a string representing the namespace of the type (i.e. "System.Drawing")
	 * @param name  a string representing the name of this type (i.e. "Color")
	 * @param flags a bit field of flags (defined in the TypeAttributes)
	 */
	public TypeDef(String ns, String name, long flags)
	{
		super(ns, name);
		Flags = flags;

		events = new Vector(5);
		fields = new Vector(10);
		methods = new Vector(10);
		properties = new Vector(10);
		interfaces = new Vector(5);
		attributes = new Vector(5);
		nestedClasses = new Vector(2);
		methodMaps = new Vector(5);
		typeDefAttributes = new Vector(10);

		superClass = AssemblyTypeRef.OBJECT;
		if(name.equals("<Module>"))
		{
			superClass = null;
		}
	}

	/**
	 * Adds a CustomAttribute to this TypeDef
	 *
	 * @param ca the CustomAttribute to apply to this TypeDef
	 */
	public void addTypeDefAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			typeDefAttributes.add(ca);
		}
	}

	/**
	 * Returns all CustomAttributes applied to this TypeDef
	 *
	 * @return an arrayof the CustomAttributes on this TypeDef
	 */
	public CustomAttribute[] getTypeDefAttributes()
	{
		CustomAttribute[] cas = new CustomAttribute[typeDefAttributes.size()];
		for(int i = 0; i < cas.length; i++)
		{
			cas[i] = (CustomAttribute) typeDefAttributes.get(i);
		}
		return cas;
	}

	/**
	 * Removes a CustomAttribute from this TypeDef
	 */
	public void removeTypeDefAttribute(CustomAttribute ca)
	{
		if(ca != null)
		{
			typeDefAttributes.remove(ca);
		}
	}

	/**
	 * Returns the RID for this TypeDef (used only by the emitter)
	 *
	 * @return the TypeDef RID of this TypeDef
	 */
	public long getTypeDefRID()
	{
		return TypeDefRID;
	}

	/**
	 * Sets the TypeDef RID of this TypeDef (used only by emitter).
	 * This method may only be called once, after which the RID cannot be changed again.
	 *
	 * @param rid the RID to assign to this TypeDef
	 */
	public void setTypeDefRID(long rid)
	{
		if(TypeDefRID == -1L)
		{
			TypeDefRID = rid;
		}
	}

	/**
	 * Returns true iff this TypeDef is an Enum (i.e. it extends System.Enum)
	 *
	 * @return true iff this class directly extends System.Enum
	 */
	public boolean isEnum()
	{
		if(superClass == null)
		{
			return false;
		}
		if(!(superClass instanceof AssemblyTypeRef))
		{
			return false;
		}

		AssemblyTypeRef ref = (AssemblyTypeRef) superClass;
		return (ref.getAssemblyRefInfo().equals(AssemblyRefInfo.MSCORLIB) &&
				ref.getNamespace().equals("System") &&
				ref.getName().equals("Enum"));
	}

	/**
	 * Returns true iff this TypeDef is a ValueType (i.e. it extends System.ValueType or System.Enum)
	 *
	 * @return true iff isEnum() or this class directly extends System.ValueType
	 */
	public boolean isValueType()
	{
		if(isEnum())
		{
			return true;
		}
		if(superClass == null)
		{
			return false;
		}
		if(!(superClass instanceof AssemblyTypeRef))
		{
			return false;
		}
		AssemblyTypeRef ref = (AssemblyTypeRef) superClass;
		return (ref.getAssemblyRefInfo().equals(AssemblyRefInfo.MSCORLIB) &&
				ref.getNamespace().equals("System") &&
				ref.getName().equals("ValueType"));
	}

	/**
	 * Returns a bit field of flags (as defined in the TypeAttributes interface)
	 */
	public long getFlags()
	{
		return Flags;
	}

	/**
	 * Sets the flags for this TypeDef (as defined in the TypeAttributes interface)
	 *
	 * @param flags the flags
	 */
	public void setFlags(long flags)
	{
		Flags = flags;
	}

	/**
	 * Returns the Module in which this TypeDef is defined
	 */
	public Module getParent()
	{
		return parent;
	}

	protected void setParent(Module mod)
	{
		parent = mod;
	}

	/**
	 * Returns the ClassLayout information for this TypeDef.
	 * If null, no ClassLayout is defined. Interfaces will not have ClassLayouts.
	 */
	public ClassLayout getClassLayout()
	{
		return classLayout;
	}

	/**
	 * Sets the ClassLayout of this TypeDef.
	 * If (Flags & ClassSemanticsMask)==Interface, this method does nothing.
	 */
	public void setClassLayout(ClassLayout lay)
	{
		if((Flags & ClassSemanticsMask) != Interface)
		{
			classLayout = lay;
		}
	}

	/**
	 * Returns the DeclSecurity information for this TypeDef
	 */
	public DeclSecurity getDeclSecurity()
	{
		return security;
	}

	/**
	 * Sets the DeclSecurity information for this TypeDef and the corresponding flag.
	 * If decl==null, this method will turn off Flags.HasSecurity
	 * If decl!=null, this method will turn on Flags.HasSecurity
	 *
	 * @param decl the DeclSecurity object
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
	 * Returns the superclass of this TypeDef. For interfaces, this will be null.
	 * For ValueTypes, this should be System.ValueType or System.Enum
	 */
	public TypeRef getSuperClass()
	{
		return superClass;
	}

	/**
	 * Sets the superclass of this TypeDef.
	 * If Flags&ClassSemanticsMask == Interface, does nothing
	 *
	 * @param ref the TypeRef of the superclass
	 */
	public void setSuperClass(TypeRef ref)
	{
		if((Flags & ClassSemanticsMask) != Interface)
		{
			superClass = ref;
		}
	}

	/**
	 * Returns the nested classes defined within this TypeDef.
	 * This array may have length 0, but will never be null.
	 */
	public TypeDef[] getNestedClasses()
	{
		TypeDef[] nested = new TypeDef[nestedClasses.size()];
		for(int i = 0; i < nestedClasses.size(); i++)
		{
			nested[i] = (TypeDef) nestedClasses.get(i);
		}
		return nested;
	}

	/**
	 * Adds a nested class definition to this TypeDef
	 *
	 * @param def the inner class TypeDef
	 */
	public void addNestedClass(TypeDef def)
	{
		if(def != null)
		{
			nestedClasses.add(def);
		}
	}

	/**
	 * Removes a nested class definition from this TypeDef
	 *
	 * @param nest the nested TypeDef to remove, based on TypeDef.equals()
	 */
	public void removeNestedClass(TypeDef nest)
	{
		nestedClasses.remove(nest);
	}

	// Event methods ///////////////////////////////

	/**
	 * Adds an event to the list of events in this TypeDef.
	 *
	 * @param event the event to add
	 */
	public void addEvent(Event event)
	{
		if(event != null)
		{
			events.add(event);
		}
	}

	/**
	 * Returns the event whose name is 'name', or null if not found.
	 *
	 * @param name of the Event (to be compared with Event.getName())
	 * @return the event specified, or null if not found
	 */
	public Event getEventByName(String name)
	{
		Iterator iter = events.iterator();
		while(iter.hasNext())
		{
			Event event = (Event) iter.next();
			if(event.getName().equals(name))
			{
				return event;
			}
		}
		return null;
	}

	/**
	 * Returns all the events owned by this TypeDef.
	 *
	 * @return a non-null array of Events
	 */
	public Event[] getEvents()
	{
		Event e[] = new Event[events.size()];
		for(int i = 0; i < e.length; i++)
		{
			e[i] = (Event) events.get(i);
		}
		return e;
	}

	// Field methods ///////////////////////////////

	/**
	 * Adds a field to this TypeDef.
	 * (also calls Field.setParent(this))
	 *
	 * @param field the field to add
	 */
	public void addField(Field field)
	{
		if(field != null)
		{
			fields.add(field);
			field.setParent(this);
		}
	}

	/**
	 * Returns the field whose name is 'name', or null if not found.
	 *
	 * @param name the name of the field, to be compared with Field.getName()
	 * @return the specified field, or null if not found
	 */
	public Field getFieldByName(String name)
	{
		Iterator iter = fields.iterator();
		while(iter.hasNext())
		{
			Field f = (Field) iter.next();
			if(f.getName().equals(name))
			{
				return f;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all the fields owned by this TypeDef
	 *
	 * @return a non-null array of fields
	 */
	public Field[] getFields()
	{
		Field[] fa = new Field[fields.size()];
		for(int i = 0; i < fa.length; i++)
		{
			fa[i] = (Field) fields.get(i);
		}
		return fa;
	}

	/**
	 * Removes the given field from this TypeDef.
	 * (also calls Field.setParent(null))
	 *
	 * @param field the field to remove, using Field.equals()
	 */
	public void removeField(Field field)
	{
		if(fields.remove(field))
		{
			field.setParent(null);
		}
	}

	// Method methods (metamethods?) /////////////////

	/**
	 * Adds a method to this TypeDef.
	 *
	 * @param method the method to add
	 */
	public void addMethod(MethodDef method)
	{
		if(method != null)
		{
			methods.add(method);
			method.setParent(this);
		}
	}

	/**
	 * Returns the method whose name is 'name', or null if not found.
	 *
	 * @param name the name of the method, to be compared to Method.getName()
	 * @return the specified method, or null if not found
	 */
	public MethodDef getMethodByName(String name)
	{
		Iterator iter = methods.iterator();
		while(iter.hasNext())
		{
			MethodDef m = (MethodDef) iter.next();
			if(m.getName().equals(name))
			{
				return m;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all the methods owned by this TypeDef.
	 *
	 * @return a non-null array of methods
	 */
	public MethodDef[] getMethods()
	{
		return methods.toArray(new MethodDef[methods.size()]);
	}

	// Property methods ////////////////////////////

	/**
	 * Adds a property to this TypeDef.
	 *
	 * @param prop the property to add
	 */
	public void addProperty(Property prop)
	{
		if(prop != null)
		{
			properties.add(prop);
		}
	}

	/**
	 * Returns the property whose name is 'name', or null if not found
	 *
	 * @param name the name of the property, to be compared with Property.getName()
	 * @return the given property, or null if not found
	 */
	public Property getPropertyByName(String name)
	{
		Iterator iter = properties.iterator();
		while(iter.hasNext())
		{
			Property p = (Property) iter.next();
			if(p.getName().equals(name))
			{
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns a list of properties owned by this TypeDef
	 *
	 * @return a non-null array of properties
	 */
	public Property[] getProperties()
	{
		Property[] pa = new Property[properties.size()];
		for(int i = 0; i < pa.length; i++)
		{
			pa[i] = (Property) properties.get(i);
		}
		return pa;
	}

	// Interface methods /////////////////////////////////

	/**
	 * Adds an interface to this TypeDef.
	 *
	 * @param inter the InterfaceImplementation for the given interface
	 */
	public void addInterface(InterfaceImplementation inter)
	{
		if(inter != null)
		{
			interfaces.add(inter);
		}
	}

	/**
	 * Returns true if this TypeDef implements the given interface.
	 * This method returns true iff:
	 * 1. 'this' is a class (not an interface or a ValueType)
	 * 2. 'inter' is an interface (not a class or ValueType)
	 * 3. 'this' implements 'inter' directly (i.e. 'inter' is not implemented by the superclass of 'this')
	 *
	 * @param inter the TypeRef of the given interface
	 */
	public boolean implementsInterface(TypeRef inter)
	{
		// this method returns true iff
		// 1. I am a class (not an interface)
		// 2. inter is an interface
		// 3. I implement inter directly (i.e. my superclass does not implement it)

		for(Object anInterface : interfaces)
		{
			InterfaceImplementation impl = (InterfaceImplementation) anInterface;
			if(impl.getInterface().equals(inter))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the interface whose name is given by Namespace and Name.
	 *
	 * @param namespace the namespace of the target interface (to be compared to TypeRef.getNamespace())
	 * @param name      the name of the target interface (to be compared to TypeRef.getName())
	 * @return the InterfaceImplementation containing the given interface, or null if not found
	 */
	public InterfaceImplementation getInterfaceByName(String namespace, String name)
	{
		for(Object anInterface : interfaces)
		{
			InterfaceImplementation impl = (InterfaceImplementation) anInterface;
			TypeRef ref = impl.getInterface();
			if(ref.getNamespace().equals(namespace) && ref.getName().equals(name))
			{
				return impl;
			}
		}
		return null;
	}

	/**
	 * Returns a list of the interfaces implemented by this TypeDef.
	 *
	 * @return a non-null array of interfaces
	 */
	public InterfaceImplementation[] getInterfaceImplementations()
	{
		InterfaceImplementation[] ia = new InterfaceImplementation[interfaces.size()];
		for(int i = 0; i < ia.length; i++)
		{
			ia[i] = (InterfaceImplementation) interfaces.get(i);
		}
		return ia;
	}
	//////////////////////////////////////////////////////

	/**
	 * Returns all the MethodMaps for this TypeDef.
	 *
	 * @return a non-null array of MethodMaps
	 */
	public MethodMap[] getMethodMaps()
	{
		MethodMap[] maps = new MethodMap[methodMaps.size()];
		for(int i = 0; i < maps.length; i++)
		{
			maps[i] = (MethodMap) methodMaps.get(i);
		}
		return maps;
	}

	/**
	 * Adds a MethodMap to this TypeDef.
	 * (note: this method does no checking to see that the method named in the MethodMap is defined in this TypeDef)
	 *
	 * @param map MethodMap to add
	 */
	public void addMethodMap(MethodMap map)
	{
		if(map != null)
		{
			methodMaps.add(map);
		}
	}

	/**
	 * Removes a MethodMap from this TypeDef, based on MethodMap.equals()
	 *
	 * @param map the MethodMap to remove
	 */
	public void removeMethodMap(MethodMap map)
	{
		methodMaps.remove(map);
	}
	//////////////////////////

	/**
	 * Compares 2 TypeDefs.
	 * TypeDefs are considered equal if their names and namespaces are equal.
	 */
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof TypeDef))
		{
			return false;
		}

		TypeDef m = (TypeDef) o;
		return (getName().equals(m.getName()) && getNamespace().equals(m.getNamespace()));
	}

	@Override
	public void addGenericParam(GenericParamDef genericParamDef)
	{
		if(myGenericParamDefs == null)
		{
			myGenericParamDefs = new ArrayList<GenericParamDef>(5);
		}
		myGenericParamDefs.add(genericParamDef);
	}

	@Override
	public List<GenericParamDef> getGenericParams()
	{
		return myGenericParamDefs == null ? Collections.<GenericParamDef>emptyList() : myGenericParamDefs;
	}

/*
   public void output(){
      System.out.print("TypeDef[Name=\"" + getName() + "\", Namespace=\"" + getNamespace() + "\"");
      if (parent!=null){
         System.out.print(", Parent=");
         parent.output();
      }
      if (classLayout!=null){
         System.out.print(", ClassLayout=");
         classLayout.output();
      }
      System.out.print("]");
   }
*/
}
