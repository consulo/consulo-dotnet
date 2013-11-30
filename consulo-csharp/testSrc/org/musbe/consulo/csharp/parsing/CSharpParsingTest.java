package org.musbe.consulo.csharp.parsing;

import org.mustbe.consulo.csharp.lang.CSharpParserDefinition;
import com.intellij.testFramework.ParsingTestCase;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class CSharpParsingTest extends ParsingTestCase
{
	public CSharpParsingTest()
	{
		super("parsing", "cs", new CSharpParserDefinition());
	}

	public void testSoftKeywords()
	{
		doTest(true);
	}

	@Override
	protected boolean shouldContainTempFiles()
	{
		return false;
	}

	@Override
	protected String getTestDataPath()
	{
		return "consulo-csharp/testData";
	}
}
