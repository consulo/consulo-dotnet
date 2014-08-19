package org.mustbe.consulo.dotnet.library;

import org.mustbe.consulo.msil.MsilFileType;
import com.intellij.openapi.roots.libraries.ui.FileTypeBasedRootFilter;
import com.intellij.openapi.roots.types.BinariesOrderRootType;

/**
 * @author VISTALL
 * @since 13.08.14
 */
public class DotNetLibraryDirectoryDetector extends FileTypeBasedRootFilter
{
	public DotNetLibraryDirectoryDetector()
	{
		super(BinariesOrderRootType.getInstance(), true, MsilFileType.INSTANCE, ".NET libraries directory");
	}
}
