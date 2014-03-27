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

package org.mustbe.consulo.visualStudio.csproj;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 27.03.14
 */
public class PropertyGroup
{
	private final Map<String, Object> myData = new HashMap<String, Object>();

	public void putAll(Map<String, Object> map)
	{
		myData.putAll(map);
	}

	public void put(String key, Object value)
	{
		myData.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue)
	{
		Object o = myData.get(key);
		return o == null ? defaultValue : (T) o;
	}

	public Map<String, Object> get()
	{
		return myData;
	}
}
