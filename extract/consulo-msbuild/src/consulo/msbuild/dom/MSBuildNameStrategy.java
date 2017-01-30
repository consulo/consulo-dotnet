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

package consulo.msbuild.dom;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xml.DomNameStrategy;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class MSBuildNameStrategy extends DomNameStrategy
{
	@Override
	public String convertName(String methodName)
	{
		return StringUtil.capitalize(methodName);
	}

	@Override
	public String splitIntoWords(String methodName)
	{
		return DomNameStrategy.HYPHEN_STRATEGY.splitIntoWords(methodName);
	}
}
