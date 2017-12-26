/*
 * Copyright 2013-2016 must-be.org
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

package consulo.msil.lang.psi.impl.type;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.resolve.DotNetGenericExtractor;
import consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public class MsilGenericExtractorImpl implements DotNetGenericExtractor
{
	private Map<DotNetGenericParameter, DotNetTypeRef> myMap;

	public MsilGenericExtractorImpl(DotNetGenericParameter[] genericParameters, DotNetTypeRef[] arguments)
	{
		myMap = new HashMap<>(genericParameters.length);
		for(int i = 0; i < genericParameters.length; i++)
		{
			DotNetGenericParameter genericParameter = genericParameters[i];
			DotNetTypeRef argument = arguments[i];

			myMap.put(genericParameter, argument);
		}
	}

	@Nullable
	@Override
	public DotNetTypeRef extract(@NotNull DotNetGenericParameter parameter)
	{
		return myMap.get(parameter);
	}
}
