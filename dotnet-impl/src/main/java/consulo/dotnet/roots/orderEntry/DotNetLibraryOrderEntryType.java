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

package consulo.dotnet.roots.orderEntry;

import javax.annotation.Nonnull;

import org.jdom.Element;
import com.intellij.openapi.util.InvalidDataException;
import consulo.roots.ModuleRootLayer;
import consulo.roots.impl.ModuleRootLayerImpl;
import consulo.roots.orderEntry.OrderEntryType;

/**
 * @author VISTALL
 * @since 21.08.14
 */
public class DotNetLibraryOrderEntryType implements OrderEntryType<DotNetLibraryOrderEntryImpl>
{
	@Nonnull
	public static DotNetLibraryOrderEntryType getInstance()
	{
		return EP_NAME.findExtension(DotNetLibraryOrderEntryType.class);
	}

	@Nonnull
	@Override
	public String getId()
	{
		return "dot-net-library";
	}

	@Nonnull
	@Override
	public DotNetLibraryOrderEntryImpl loadOrderEntry(@Nonnull Element element, @Nonnull ModuleRootLayer moduleRootLayer) throws InvalidDataException
	{
		String name = element.getAttributeValue("name");
		if(name.endsWith(".dll"))
		{
			int lastIndex = name.lastIndexOf(".dll");
			name = name.substring(0, lastIndex);
		}
		return new DotNetLibraryOrderEntryImpl((ModuleRootLayerImpl) moduleRootLayer, name);
	}

	@Override
	public void storeOrderEntry(@Nonnull Element element, @Nonnull DotNetLibraryOrderEntryImpl dotNetLibraryOrderEntry)
	{
		element.setAttribute("name", dotNetLibraryOrderEntry.getPresentableName());
	}
}
