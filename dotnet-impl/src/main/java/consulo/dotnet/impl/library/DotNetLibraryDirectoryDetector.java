package consulo.dotnet.impl.library;

import consulo.content.base.BinariesOrderRootType;
import consulo.ide.impl.idea.openapi.roots.libraries.ui.FileTypeBasedRootFilter;
import consulo.msil.MsilFileType;

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
