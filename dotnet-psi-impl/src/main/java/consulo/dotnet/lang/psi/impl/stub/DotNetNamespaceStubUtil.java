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

package consulo.dotnet.lang.psi.impl.stub;

import javax.annotation.Nonnull;

import consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.util.QualifiedName;

/**
 * @author VISTALL
 * @since 22.10.14
 */
public class DotNetNamespaceStubUtil
{
	public static String ROOT_FOR_INDEXING = "<root>";

	@Nonnull
	public static String getIndexableNamespace(@Nonnull String namespace)
	{
		return StringUtil.notNullizeIfEmpty(namespace, ROOT_FOR_INDEXING);
	}

	@Nonnull
	public static String getIndexableNamespace(@Nonnull QualifiedName qualifiedName)
	{
		return StringUtil.notNullizeIfEmpty(qualifiedName.join("."), ROOT_FOR_INDEXING);
	}

	public static void indexStub(@Nonnull IndexSink indexSink,
			@Nonnull StubIndexKey<String, ? extends DotNetQualifiedElement> elementByQNameKey,
			@Nonnull StubIndexKey<String, ? extends DotNetQualifiedElement> namespaceKey,
			@Nonnull String namespace,
			@Nonnull String name)
	{
		String indexableNamespace = getIndexableNamespace(namespace);

		name = consulo.internal.dotnet.msil.decompiler.util.MsilHelper.cutGenericMarker(name);

		indexSink.occurrence(elementByQNameKey, indexableNamespace + "." + name);

		if(!StringUtil.isEmpty(namespace))
		{
			QualifiedName parent = QualifiedName.fromDottedString(namespace);
			do
			{
				indexSink.occurrence(namespaceKey, getIndexableNamespace(parent));
			}
			while((parent = parent.getParent()) != null);
		}
		else
		{
			indexSink.occurrence(namespaceKey, indexableNamespace);
		}
	}
}
