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

package consulo.dotnet;

import org.jetbrains.annotations.PropertyKey;
import com.intellij.AbstractBundle;

/**
 * @author VISTALL
 * @since 20.12.13.
 */
public class DotNetBundle extends AbstractBundle
{
	private static final DotNetBundle ourInstance = new DotNetBundle();

	private DotNetBundle()
	{
		super("messages.DotNetBundle");
	}

	public static String message(@PropertyKey(resourceBundle = "messages.DotNetBundle") String key)
	{
		return ourInstance.getMessage(key);
	}

	public static String message(@PropertyKey(resourceBundle = "messages.DotNetBundle") String key, Object... params)
	{
		return ourInstance.getMessage(key, params);
	}
}
