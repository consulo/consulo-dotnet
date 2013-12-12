package org.mustbe.consulo.dotnet.dll;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public class DotNetDllFileTypeFactory extends FileTypeFactory
{
	@Override
	public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer)
	{
		fileTypeConsumer.consume(DotNetDllFileType.INSTANCE);
	}
}
