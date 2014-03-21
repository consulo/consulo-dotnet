package dontnet.asm.test;

import java.io.File;
import java.io.FileInputStream;

import edu.arizona.cs.mbel.mbel.ModuleParser;
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

	// test with method generic parameter and A constraint
	public void test4() throws Exception
	{
	}

	// test with method generic parameter and struct constraint
	public void test5() throws Exception
	{
	}

	public void test6() throws Exception
	{
	}

	@Override
	protected void runTest() throws Throwable
	{
		new ModuleParser(new FileInputStream(new File("dotnet-asm/testData/" + getName() + "/Program.exe")));

	}
}
