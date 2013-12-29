/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.dotnet.ide.projectView;

/**
 * @author VISTALL
 * @since 28.12.13.
 */
public class DotNetPackageViewHelper //implements PackageViewHelper
{
	/*@NotNull
	@Override
	public String getPresentationName()
	{
		return IdeBundle.message("title.namespaces");
	}

	@Override
	public boolean hasNodesFromModule(@NotNull Module module)
	{
		val has = new Ref<Boolean>(false);

		StubIndex.getInstance().processAllKeys(DotNetIndexKeys.NAMESPACE_BY_QNAME_INDEX, new Processor<String>()
		{
			@Override
			public boolean process(String s)
			{
				has.set(true);
				return false;
			}
		}, GlobalSearchScope.moduleScope(module), IdFilter.getProjectIdFilter(module.getProject(), false));

		return has.get();
	}

	@NotNull
	@Override
	public PackageViewNode[] getNodesFromModule(@NotNull Module module)
	{
		return new PackageViewNode[0];
	}   */
}
