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

package consulo.dotnet.dll;

import javax.annotation.Nonnull;
import consulo.fileTypes.ArchiveFileType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class DotNetModuleFileType extends ArchiveFileType
{
	public static final DotNetModuleFileType INSTANCE = new DotNetModuleFileType();
	public static final String PROTOCOL = "netdll";

	@Nonnull
	@Override
	public String getProtocol()
	{
		return PROTOCOL;
	}

	@Nonnull
	@Override
	public String getDescription()
	{
		return ".NET libraries";
	}

	@Nonnull
	@Override
	public String getId()
	{
		return "DLL_ARCHIVE";
	}

	@Nonnull
	@Override
	public String getDefaultExtension()
	{
		return "dll";
	}
}
