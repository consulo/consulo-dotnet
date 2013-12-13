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

package org.mustbe.consulo.dotnet.dll.vfs.builder;

import com.intellij.openapi.util.text.StringUtil;
import edu.arizona.cs.mbel.mbel.TypeDef;
import lombok.val;

/**
 * @author VISTALL
 * @since 13.12.13.
 */
public class StubToStringUtil
{
	public static final char GENERIC_MARKER_IN_NAME = '`';
	private static final char[] ILLEGAL_CHARS = new char[] {'{', '}', '<', '>', '='};

	public static String getUserTypeDefName(TypeDef typeDef)
	{
		return getUserTypeDefName(typeDef.getName());
	}

	public static String getUserTypeDefName(String name)
	{
		int i = name.lastIndexOf(GENERIC_MARKER_IN_NAME);
		if(i > 0)
		{
			name = name.substring(0, i);
		}
		return name;
	}

	public static boolean isInvisibleTypeDef(TypeDef typeDef)
	{
		val name = typeDef.getName();
		for(char illegalChar : ILLEGAL_CHARS)
		{
			if(StringUtil.containsChar(name, illegalChar))
			{
				return true;
			}
		}
		return false;
	}
}
