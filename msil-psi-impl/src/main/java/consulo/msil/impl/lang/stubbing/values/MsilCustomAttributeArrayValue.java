/*
 * Copyright 2013-2026 consulo.io
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

package consulo.msil.impl.lang.stubbing.values;

import consulo.internal.dotnet.asm.signature.SZArrayTypeSignature;
import consulo.internal.dotnet.asm.signature.TypeSignature;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

/**
 * Value of a single-dimension array argument of a custom attribute, e.g. {@code [Nullable(new byte[] { 1, 0 })]}.
 * {@link #getValue()} is the list of the element values, presented ({@code toString()}) as the C# array creation
 * expression - so presentations which append values as text need no knowledge of arrays.
 */
public class MsilCustomAttributeArrayValue extends MsiCustomAttributeValue
{
	private static final Map<TypeSignature, String> ourKeywords = Map.ofEntries(
			Map.entry(TypeSignature.BOOLEAN, "bool"),
			Map.entry(TypeSignature.STRING, "string"),
			Map.entry(TypeSignature.OBJECT, "object"),
			Map.entry(TypeSignature.I1, "sbyte"),
			Map.entry(TypeSignature.U1, "byte"),
			Map.entry(TypeSignature.I2, "short"),
			Map.entry(TypeSignature.U2, "ushort"),
			Map.entry(TypeSignature.I4, "int"),
			Map.entry(TypeSignature.U4, "uint"),
			Map.entry(TypeSignature.I8, "long"),
			Map.entry(TypeSignature.U8, "ulong"),
			Map.entry(TypeSignature.R4, "float"),
			Map.entry(TypeSignature.R8, "double")
	);

	private final List<MsiCustomAttributeValue> myElements;
	private final List<Object> myElementValues;

	public MsilCustomAttributeArrayValue(SZArrayTypeSignature typeSignature, List<MsiCustomAttributeValue> elements)
	{
		super(typeSignature, null);
		myElements = List.copyOf(elements);
		myElementValues = new ElementValues();
	}

	public List<MsiCustomAttributeValue> getElements()
	{
		return myElements;
	}

	/**
	 * @return the values of the elements ({@link Number}s, {@link CharSequence}s, ...), presented as C#
	 */
	@Override
	public List<Object> getValue()
	{
		return myElementValues;
	}

	@Override
	public String toString()
	{
		return myElementValues.toString();
	}

	private String present()
	{
		String keyword = ourKeywords.get(((SZArrayTypeSignature) getTypeSignature()).getElementType());

		StringBuilder builder = new StringBuilder();
		builder.append(keyword == null ? "new[]" : "new " + keyword + "[]");
		builder.append(" { ");
		for(int i = 0; i < myElements.size(); i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			Object value = myElements.get(i).getValue();
			if(value instanceof CharSequence)
			{
				builder.append('"').append((CharSequence) value).append('"');
			}
			else
			{
				builder.append(value);
			}
		}
		builder.append(" }");
		return builder.toString();
	}

	private class ElementValues extends AbstractList<Object>
	{
		@Override
		public Object get(int index)
		{
			return myElements.get(index).getValue();
		}

		@Override
		public int size()
		{
			return myElements.size();
		}

		@Override
		public String toString()
		{
			return present();
		}
	}
}
