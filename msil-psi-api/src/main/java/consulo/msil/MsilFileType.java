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

package consulo.msil;

import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilFileType extends LanguageFileType
{
	public static final MsilFileType INSTANCE = new MsilFileType();

	private MsilFileType()
	{
		super(MsilLanguage.INSTANCE);
	}

	@Nonnull
	@Override
	public String getId()
	{
		return "MSIL";
	}

	@Nonnull
	@Override
	public LocalizeValue getDescription()
	{
		return LocalizeValue.localizeTODO("Microsoft Intermediate Language files");
	}

	@Nonnull
	@Override
	public String getDefaultExtension()
	{
		return "il";
	}

	@Nonnull
	@Override
	public Image getIcon()
	{
		return PlatformIconGroup.fileTypesBinary();
	}
}
