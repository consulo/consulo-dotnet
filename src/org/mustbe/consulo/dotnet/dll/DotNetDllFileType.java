package org.mustbe.consulo.dotnet.dll;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.highlighter.ArchiveFileType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class DotNetDllFileType extends ArchiveFileType
{
	public static final DotNetDllFileType INSTANCE = new DotNetDllFileType();
	public static final String PROTOCOL = "netdll";

	@Override
	public String getProtocol()
	{
		return PROTOCOL;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "DLL";
	}
}
