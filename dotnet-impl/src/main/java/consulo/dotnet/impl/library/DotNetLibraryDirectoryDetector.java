package consulo.dotnet.impl.library;

import consulo.annotation.component.ExtensionImpl;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.library.ui.FileTypeBasedRootFilter;
import consulo.msil.MsilFileType;

/**
 * @author VISTALL
 * @since 13.08.14
 */
@ExtensionImpl
public class DotNetLibraryDirectoryDetector extends FileTypeBasedRootFilter
{
	public DotNetLibraryDirectoryDetector()
	{
		super(BinariesOrderRootType.getInstance(), true, MsilFileType.INSTANCE, ".NET libraries directory");
	}
}
