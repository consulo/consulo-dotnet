package org.mustbe.consulo.dotnet.library;

import org.mustbe.consulo.msil.MsilFileType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.ui.FileTypeBasedRootFilter;

/**
 * @author VISTALL
 * @since 13.08.14
 */
public class DotNetLibraryDirectoryDetector extends FileTypeBasedRootFilter
{
	public DotNetLibraryDirectoryDetector()
	{
		super(OrderRootType.BINARIES, true, MsilFileType.INSTANCE, ".NET libraries directory");
	}
}
