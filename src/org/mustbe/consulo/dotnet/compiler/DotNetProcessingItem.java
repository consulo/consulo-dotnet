package org.mustbe.consulo.dotnet.compiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.compiler.EmptyValidityState;
import com.intellij.openapi.compiler.FileProcessingCompiler;
import com.intellij.openapi.compiler.ValidityState;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetProcessingItem implements FileProcessingCompiler.ProcessingItem
{
	private VirtualFile myVirtualFile;
	private Module myModule;

	public DotNetProcessingItem(VirtualFile virtualFile, Module module)
	{
		myVirtualFile = virtualFile;
		myModule = module;
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

	public Module getModule()
	{
		return myModule;
	}
}
