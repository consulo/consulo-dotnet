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

package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.util.QualifiedName;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public class DotNetNamespaceUtil
{
	public static String ROOT_FOR_INDEXING = "<root>";

	@NotNull
	public static String getIndexableNamespace(@NotNull String namespace)
	{
		return StringUtil.notNullizeIfEmpty(namespace, ROOT_FOR_INDEXING);
	}

	@NotNull
	public static String getIndexableNamespace(@NotNull QualifiedName qualifiedName)
	{
		return StringUtil.notNullizeIfEmpty(qualifiedName.join("."), ROOT_FOR_INDEXING);
	}
}
