/*
 * Copyright 2013-2017 consulo.io
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

package consulo.msbuild.solution;

import org.jetbrains.annotations.NotNull;

/**
 * @author VISTALL
 * @since 29-Jan-17
 */
public enum SolutionVirtualFileSubType
{
	Form, Designer, __generator, __unknown;

	@NotNull
	public static SolutionVirtualFileSubType find(@NotNull String name)
	{
		for(SolutionVirtualFileSubType type : values())
		{
			if(name.equals(type.name()))
			{
				return type;
			}
		}
		return __unknown;
	}
}
