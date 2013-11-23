package dontnet.asm.test;

import java.io.File;
import java.io.FileInputStream;

import edu.arizona.cs.mbel.mbel.ModuleParser;
import edu.arizona.cs.mbel.mbel.Module;
import junit.framework.TestCase;

/**
 * @author VISTALL
 * @since 23.11.13.
 */
public class DotNetAsmTest extends TestCase
{
	// simple reader test
	public void test1() throws Exception
	{
	}

	// test with class generic parameter
	public void test2() throws Exception
	{
	}

	// test with method generic parameter
	public void test3() throws Exception
	{
	}

	@Override
	protected void runTest() throws Throwable
	{
		ModuleParser moduleParser = new ModuleParser(new FileInputStream(new File("dotnet-asm/testData/" + getName() + "/Program.exe")));
		Module module = moduleParser.parseModule();
		assertNotNull(module);
	}
}
