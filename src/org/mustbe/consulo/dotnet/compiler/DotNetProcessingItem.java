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

package org.mustbe.consulo.dotnet.compiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.compiler.EmptyValidityState;
import com.intellij.openapi.compiler.FileProcessingCompiler;
import com.intellij.openapi.compiler.ValidityState;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetProcessingItem implements FileProcessingCompiler.ProcessingItem
{
	private final VirtualFile myVirtualFile;
	private final DotNetModuleExtension myExtension;

	public DotNetProcessingItem(VirtualFile virtualFile, DotNetModuleExtension<?> dotNetModuleExtension)
	{
		myVirtualFile = virtualFile;
		myExtension = dotNetModuleExtension;
	}

	@NotNull
	@Override
	public VirtualFile getFile()
	{
		return myVirtualFile;
	}

	@Nullable
	@Override
	public ValidityState getValidityState()
	{
		return new EmptyValidityState();
	}

	public DotNetModuleExtension getExtension()
	{
		return myExtension;
	}
}
