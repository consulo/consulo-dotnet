import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import org.mustbe.consulo.dotnet.dll.vfs.DotNetArchiveFile;
import com.intellij.openapi.vfs.ArchiveEntry;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * @author VISTALL
 * @since 11.12.13.
 */
public class DotNetDllFileTest
{
	public static void main(String[] args) throws Exception
	{
		ModuleParser moduleParser = new ModuleParser(new File("G:\\ikvm-7.2.4630.5\\bin\\IKVM.OpenJDK.Core.dll"));

		DotNetArchiveFile archiveFile = new DotNetArchiveFile(moduleParser, 0);

		Iterator<? extends ArchiveEntry> entries = archiveFile.entries();
		while(entries.hasNext())
		{
			ArchiveEntry next = entries.next();

			if(!next.getName().contains("StringPrep.msil"))
			{
				continue;
			}

			if(!next.isDirectory())
			{

				InputStream inputStream = archiveFile.getInputStream(next);
				int available = inputStream.available();
				byte[] data = new byte[available];
				inputStream.read(data);
				String str = new String(data);
				System.out.println(str);
			}
		}
	}
}
