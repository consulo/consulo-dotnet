/*
 * Copyright 2013 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.arizona.cs.mbel.signature;

import java.util.List;

/**
 * @author VISTALL
 * @since 13.12.13.
 */
public class TypeSignatureWithGenericParameters extends TypeSignature
{
	private final TypeSignature mySignature;
	private final List<TypeSignature> myGenericArguments;

	public TypeSignatureWithGenericParameters(TypeSignature signature, List<TypeSignature> genericArguments)
	{
		super(ELEMENT_TYPE_GENERIC_INST);
		mySignature = signature;
		myGenericArguments = genericArguments;
	}

	public List<TypeSignature> getGenericArguments()
	{
		return myGenericArguments;
	}

	public TypeSignature getSignature()
	{
		return mySignature;
	}
}
