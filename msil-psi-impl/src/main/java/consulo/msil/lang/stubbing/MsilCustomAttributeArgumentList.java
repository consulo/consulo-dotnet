package consulo.msil.lang.stubbing;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import consulo.msil.lang.stubbing.values.MsiCustomAttributeValue;

/**
 * @author VISTALL
 * @since 10.07.2015
 */
public class MsilCustomAttributeArgumentList
{
	private List<MsiCustomAttributeValue> myConstructorArguments;
	private Map<String, MsiCustomAttributeValue> myNamedArguments;

	public MsilCustomAttributeArgumentList(List<MsiCustomAttributeValue> constructorArguments, Map<String, MsiCustomAttributeValue> namedArguments)
	{
		myConstructorArguments = constructorArguments;
		myNamedArguments = namedArguments;
	}

	@Nonnull
	public Map<String, MsiCustomAttributeValue> getNamedArguments()
	{
		return myNamedArguments;
	}

	@Nonnull
	public List<MsiCustomAttributeValue> getConstructorArguments()
	{
		return myConstructorArguments;
	}
}
