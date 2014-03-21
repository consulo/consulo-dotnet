package dontnet.asm.test;

import java.io.FileInputStream;

import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * @author VISTALL
 * @since 10.12.13.
 */
public class Test
{
	public static void main(String[] args) throws Exception
	{
		ModuleParser moduleParser = new ModuleParser(new FileInputStream("C:\\Users\\VISTALL\\Documents\\visual studio " +
				"2010\\Projects\\ConsoleApplication1\\ConsoleApplication1\\bin\\Debug\\ConsoleApplication1.dll"));


		System.out.println(moduleParser);
	}
}
