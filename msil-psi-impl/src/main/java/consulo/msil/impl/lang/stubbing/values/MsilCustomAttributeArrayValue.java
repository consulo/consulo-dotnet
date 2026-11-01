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

import java.util.List;
import java.util.Map;

/**
 * Value of a single-dimension array argument of a custom attribute, e.g. {@code [Nullable(new byte[] { 1, 0 })]}.
 * Its {@link #toString()} is the C# array creation expression, which is how attribute values are presented.
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

	public MsilCustomAttributeArrayValue(SZArrayTypeSignature typeSignature, List<MsiCustomAttributeValue> elements)
	{
		super(typeSignature, null);
		myElements = List.copyOf(elements);
	}

	public List<MsiCustomAttributeValue> getElements()
	{
		return myElements;
	}

	@Override
	public Object getValue()
	{
		return this;
	}

	@Override
	public String toString()
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
}
