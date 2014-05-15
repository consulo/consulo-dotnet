/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.dotnet.dll.vfs.builder.util;

import java.io.UnsupportedEncodingException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.dll.vfs.builder.XStubBuilder;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.ArrayUtil;
import com.intellij.util.PairFunction;
import edu.arizona.cs.mbel.ByteBuffer;
import edu.arizona.cs.mbel.mbel.AbstractTypeReference;
import edu.arizona.cs.mbel.mbel.TypeRef;
import edu.arizona.cs.mbel.signature.Signature;
import lombok.val;

/**
 * @author VISTALL
 * @since 13.12.13.
 */
public class XStubUtil
{
	public static final String CONSTRUCTOR_NAME = ".ctor";
	public static final String STATIC_CONSTRUCTOR_NAME = ".cctor";

	public static final char GENERIC_MARKER_IN_NAME = '`';
	private static final char[] ILLEGAL_CHARS = new char[] {'{', '}', '<', '>', '=', '\\', '/'};

	public static String[] KEYWORDS = new String[]{
			"event",
			"params",
			"break",
			"continue",
			"lock",
			"explicit",
			"implicit",
			"this",
			"in",
			"out",
			"abstract",
			"sealed",
			"lock",
			"object",
			"case",
			"delegate",
			"finally",
			"internal",
	};

	public static boolean isSet(long value, int mod)
	{
		return (value & mod) == mod;
	}

	public static boolean isSet(long value, int mod, int v)
	{
		return (value & mod) == v;
	}

	public static String getUtf8(ByteBuffer byteBuffer)
	{
		byte b = byteBuffer.get();
		if(b == 0xFF)
		{
			return "";
		}
		else
		{
			byteBuffer.back();
			int size = Signature.readCodedInteger(byteBuffer);
			if(size == 0)
			{
				return "";
			}
			else
			{
				try
				{
					return convertTo(new String(byteBuffer.get(size), "UTF-8"));
				}
				catch(UnsupportedEncodingException e)
				{
					return "UnsupportedEncodingException";
				}
			}
		}
	}

	@NotNull
	public static String cutSuperTypeName(@NotNull String name, @Nullable Ref<Boolean> callback)
	{
		if(name.equals(CONSTRUCTOR_NAME) || name.equals(STATIC_CONSTRUCTOR_NAME))
		{
			return name;
		}
		else if(StringUtil.containsChar(name, '.'))
		{
			// method override(implement) from superclass, cut owner of super method
			val dotIndex = name.lastIndexOf('.');
			name = name.substring(dotIndex + 1, name.length());
			if(callback != null)
			{
				callback.set(Boolean.TRUE);
			}
			return name;
		}
		else
		{
			return name;
		}
	}

	public static String convertTo(String old)
	{
		char[] chars = old.toCharArray();
		StringBuilder builder = new StringBuilder(chars.length);
		for(int i = 0; i < chars.length; i++)
		{
			char aChar = chars[i];
			builder.append(toValidStringSymbol(aChar));
		}
		return builder.capacity() == old.length() ? old : builder.toString();
	}

	public static Object toValidStringSymbol(char a)
	{
		switch(a)
		{
			case '\n':
				return "\\n";
			case '\r':
				return "\\r";
			case '\t':
				return "\\t";
			case '\f':
				return "\\f";
			case '\'':
				return "\\\'";
			case '"':
				return "\\\"";
			case '\\':
				return "\\\\";
		}
		return a;
	}

	public static String cutGenericMarker(String name)
	{
		int i = name.lastIndexOf(GENERIC_MARKER_IN_NAME);
		if(i > 0)
		{
			name = name.substring(0, i);
		}
		return name;
	}

	public static boolean isInvisibleMember(String name)
	{
		for(char illegalChar : ILLEGAL_CHARS)
		{
			if(StringUtil.containsChar(name, illegalChar))
			{
				return true;
			}
		}
		return false;
	}

	public static void appendValidName(StringBuilder builder, String name)
	{
		if(ArrayUtil.contains(name, KEYWORDS))
		{
			builder.append('@');
		}

		int index = name.indexOf(GENERIC_MARKER_IN_NAME);
		if(index != -1)
		{
			builder.append(name, 0, index);
		}
		else
		{
			builder.append(name);
		}
	}

	/**
	 * Method for ignore {@link AbstractTypeReference#getFullName()} - dont create twice StringBuilder
	 * @param builder
	 * @param typeRef
	 */
	public static void appendTypeRefFullName(StringBuilder builder, TypeRef typeRef)
	{
		String namespace = typeRef.getNamespace();
		String name = typeRef.getName();
		if(!StringUtil.isEmpty(namespace))
		{
			appendDottedValidName(builder, namespace);
			builder.append('.');
		}
		appendValidName(builder, name);
	}

	public static void appendDottedValidName(StringBuilder builder, String dottedName)
	{
		QualifiedName qualifiedName = QualifiedName.fromDottedString(dottedName);

		XStubBuilder.join(builder, qualifiedName.getComponents(), new PairFunction<StringBuilder, String, Void>()
		{
			@Nullable
			@Override
			public Void fun(StringBuilder t, String v)
			{
				appendValidName(t, v);
				return null;
			}
		}, ".");
	}
}
