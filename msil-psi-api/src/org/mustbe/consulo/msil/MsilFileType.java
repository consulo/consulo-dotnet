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

package org.mustbe.consulo.msil;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;

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

	@NotNull
	@Override
	public String getName()
	{
		return "MSIL";
	}

	@NotNull
	@Override
	public String getDescription()
	{
		return "Microsoft Intermediate Language files";
	}

	@NotNull
	@Override
	public String getDefaultExtension()
	{
		return "msil";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return AllIcons.FileTypes.JavaClass;
	}
}
