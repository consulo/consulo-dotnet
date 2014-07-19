package org.mustbe.consulo.msil;

import org.junit.Assert;
import org.junit.Test;
import org.mustbe.consulo.dotnet.dll.vfs.builder.MsilStubBuilder;

/**
 * @author VISTALL
 * @since 20.07.14
 */
public class MsilStubNameTest extends Assert
{
	@Test
	public void testNumberAsName()
	{
		StringBuilder builder = new StringBuilder();
		MsilStubBuilder.appendValidName(builder, "1");

		assertEquals(builder.toString(), "\'1\'");
	}

	@Test
	public void testNumberInNameAnd()
	{
		StringBuilder builder = new StringBuilder();
		MsilStubBuilder.appendValidName(builder, "sun.net.www.protocol.http.BasicAuthentication/1");

		assertEquals(builder.toString(), "\'sun.net.www.protocol.http.BasicAuthentication/1\'");
	}
}
