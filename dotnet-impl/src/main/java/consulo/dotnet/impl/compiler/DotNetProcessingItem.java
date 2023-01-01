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

package consulo.dotnet.impl.compiler;

import consulo.compiler.FileProcessingCompiler;
import consulo.compiler.TimestampValidityState;
import consulo.compiler.ValidityState;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetProcessingItem implements FileProcessingCompiler.ProcessingItem
{
	private final File myFile;
	private final DotNetRunModuleExtension<?> myExtension;

	public DotNetProcessingItem(File file, DotNetRunModuleExtension<?> dotNetModuleExtension)
	{
		myFile = file;
		myExtension = dotNetModuleExtension;
	}

	@Nonnull
	@Override
	public File getFile()
	{
		return myFile;
	}

	@Nullable
	@Override
	public ValidityState getValidityState()
	{
		return new TimestampValidityState(myFile.lastModified());
	}

	public DotNetRunModuleExtension<?> getExtension()
	{
		return myExtension;
	}
}
