package dontnet.asm.test;

import java.io.FileInputStream;

import edu.arizona.cs.mbel.mbel.Module;
import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * @author VISTALL
 * @since 10.12.13.
 */
public class Test
{
	public static void main(String[] args) throws Exception
	{
		ModuleParser moduleParser = new ModuleParser(new FileInputStream("C:\\Windows\\Microsoft.NET\\Framework\\v4.0.30319\\System.ServiceModel.dll"));

		Module module = moduleParser.parseModule();

	}
}
