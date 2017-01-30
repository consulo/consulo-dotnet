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

package consulo.msbuild;

import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.util.LazyInstance;
import com.intellij.util.xmlb.annotations.Attribute;

/**
 * @author VISTALL
 * @since 30-Jan-17
 */
public class MSBuildProjectTypeEP<T extends MSBuildProjectType> extends AbstractExtensionPointBean
{
	@Attribute("ext")
	public String ext;

	@Attribute("guid")
	public String guid;

	@Attribute("implementationClass")
	public String implementationClass;

	private final LazyInstance<T> myHandler = new LazyInstance<T>()
	{
		@Override
		protected Class<T> getInstanceClass() throws ClassNotFoundException
		{
			return findClass(implementationClass);
		}
	};

	public T getInstance()
	{
		return myHandler.getValue();
	}

	public String getGuid()
	{
		return guid;
	}

	public String getExt()
	{
		return ext;
	}
}