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

package consulo.dotnet.impl.externalAttribute;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.AllIcons;
import consulo.content.bundle.Sdk;
import consulo.dotnet.externalAttributes.ExternalAttributesRootOrderType;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.ide.ui.OrderRootTypeUIFactory;
import consulo.ide.ui.SdkPathEditor;
import consulo.ui.image.Image;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 02.09.14
 */
@ExtensionImpl
public class ExternalAttributesOrderRootTypeUIFactory implements OrderRootTypeUIFactory
{
	@Nonnull
	@Override
	public String getOrderRootTypeId()
	{
		return "dotNetExternalAttributes";
	}

	@Nonnull
	@Override
	public SdkPathEditor createPathEditor(Sdk sdk)
	{
		return new SdkPathEditor(getNodeText(), ExternalAttributesRootOrderType.getInstance(), FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), sdk);
	}

	@Nonnull
	@Override
	public Image getIcon()
	{
		return AllIcons.Nodes.Annotationtype;
	}

	@Nonnull
	@Override
	public String getNodeText()
	{
		return "External Attributes";
	}
}
