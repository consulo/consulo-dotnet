package dontnet.asm.test;

import edu.arizona.cs.mbel.signature.MethodAttributes;

/**
 * @author VISTALL
 * @since 10.12.13.
 */
public class Test
{
	public static void main(String[] args) throws Exception
	{
		/*ModuleParser moduleParser = new ModuleParser(new FileInputStream("C:\\Windows\\Microsoft.NET\\Framework\\v4.0.30319\\System.Core.dll"));

		Module module = moduleParser.parseModule();*/
		/*ZipFile zipFile = new ZipFile("out\\artifacts\\dist\\csharp\\lib\\csharp.jar");
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements())
		{
			ZipEntry zipEntry = entries.nextElement();

			System.out.println(zipEntry.getName() + " " + zipEntry.getSize());
		}  */
		long value = 2182;
		System.out.println((value & MethodAttributes.SpecialName) == MethodAttributes.SpecialName);
	}
}
