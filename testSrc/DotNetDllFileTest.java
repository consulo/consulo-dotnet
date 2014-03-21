import java.io.FileInputStream;
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
		ModuleParser moduleParser = new ModuleParser(new FileInputStream("C:\\Windows\\Microsoft.NET\\Framework\\v4.0.30319\\System.Core.dll"));

		DotNetArchiveFile archiveFile = new DotNetArchiveFile(moduleParser, 0);

		Iterator<? extends ArchiveEntry> entries = archiveFile.entries();
		while(entries.hasNext())
		{
			ArchiveEntry next = entries.next();

			System.out.println((next.isDirectory() ? "Dir: " : "File: ") + next.getName());

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
