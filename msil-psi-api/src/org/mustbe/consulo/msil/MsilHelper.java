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

package org.mustbe.consulo.msil;

import com.intellij.openapi.util.text.StringUtil;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilHelper
{
	public static final String CONSTRUCTOR_NAME = ".ctor";
	public static final String STATIC_CONSTRUCTOR_NAME = ".cctor";
	public static final char GENERIC_MARKER_IN_NAME = '`';

	public static String cutGenericMarker(String name)
	{
		int i = name.lastIndexOf(GENERIC_MARKER_IN_NAME);
		if(i > 0)
		{
			name = name.substring(0, i);
		}
		return name;
	}

	public static String appendNoGeneric(String namespace, String name)
	{
		if(StringUtil.isEmpty(namespace))
		{
			return cutGenericMarker(name);
		}
		else
		{
			return namespace + "." + cutGenericMarker(name);
		}
	}

	public static String append(String namespace, String name)
	{
		if(StringUtil.isEmpty(namespace))
		{
			return name;
		}
		else
		{
			return namespace + "." + name;
		}
	}
}
