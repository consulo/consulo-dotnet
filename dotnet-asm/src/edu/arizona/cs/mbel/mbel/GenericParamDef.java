package edu.arizona.cs.mbel.mbel;

/**
 * @author VISTALL
 * @since 23.11.13.
 */
public class GenericParamDef
{
	private final String myName;
	private final int myFlags;

	public GenericParamDef(String name, int flags)
	{
		myName = name;
		myFlags = flags;
	}

	public String getName()
	{
		return myName;
	}

	public int getFlags()
	{
		return myFlags;
	}
}
