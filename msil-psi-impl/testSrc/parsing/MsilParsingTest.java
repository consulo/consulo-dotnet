/*
 * Copyright 2013-2015 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package parsing;

import org.mustbe.consulo.testFramework.ParsingTestCase;

/**
 * @author VISTALL
 * @since 13.05.2015
 */
public class MsilParsingTest extends ParsingTestCase
{
	public MsilParsingTest()
	{
		super("parsing", "msil");
	}

	public void testFieldConstWithValue()
	{
		doTest(true);
	}

	public void testArrayList()
	{
		doTest(true);
	}

	public void testFieldWithAttribute()
	{
		doTest(true);
	}

	public void testTestCustomAttributeSignature()
	{
		doTest(true);
	}

	@Override
	protected String getTestDataPath()
	{
		return "/msil-psi-impl/testData";
	}
}
