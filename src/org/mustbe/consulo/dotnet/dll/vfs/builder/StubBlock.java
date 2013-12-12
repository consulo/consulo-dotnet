package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public class StubBlock
{
	private String myStartText;
	private char[] myIndents;

	private List<StubBlock> myBlocks = new ArrayList<StubBlock>(2);

	public StubBlock(String startText, char... indents)
	{
		myStartText = startText;
		myIndents = indents;
	}

	public List<StubBlock> getBlocks()
	{
		return myBlocks;
	}

	public char[] getIndents()
	{
		return myIndents;
	}

	public String getStartText()
	{
		return myStartText;
	}
}
