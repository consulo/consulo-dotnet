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

package consulo.dotnet.util;

import java.util.List;

import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 09.03.14
 */
public class ArrayUtil2
{
	public static int safeGet(@Nullable int[] array, int index)
	{
		if(array == null)
			return 0;
		if(index < 0 || array.length <= index)
			return 0;
		return array[index];
	}

	@Nullable
	public static <T> T safeGet(@Nullable T[] array, int index)
	{
		if(array == null)
			return null;
		if(index < 0 || array.length <= index)
			return null;
		return array[index];
	}

	@Nullable
	public static <T> T safeGet(@Nullable List<? extends T> array, int index)
	{
		if(array == null)
			return null;
		if(index < 0 || array.size() <= index)
			return null;
		return array.get(index);
	}
}
