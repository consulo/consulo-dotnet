/*
 * Copyright 2013-2016 must-be.org
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

package consulo.debugger.impl;

import consulo.util.lang.Couple;

import org.junit.Assert;
import org.junit.Test;
import consulo.dotnet.debugger.impl.nodes.DotNetDebuggerCompilerGenerateUtil;

/**
 * @author VISTALL
 * @since 20-Nov-16.
 */
public class DotNetDebuggerCompilerGenerateUtilTest extends Assert
{
	@Test
	public void testLambdaMethod()
	{
		// the names are the ones of Roslyn (csc) and mcs for the same lambdas
		assertEquals(Couple.of("M", "1_0"), DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo("<M>b__1_0", "<>c"));
		assertEquals(Couple.of("M", "1"), DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo("<M>b__1", "<>c__DisplayClass1_0"));
		assertEquals(Couple.of("M", "0"), DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo("<M>m__0", "C"));
		assertEquals(Couple.of("M", "2"), DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo("<>m__2", "<M>c__AnonStorey0"));

		// a local function, an async state machine, a plain method
		assertNull(DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo("<M>g__Local|4", "<>c__DisplayClass1_0"));
		assertNull(DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo("MoveNext", "<<M>b__3>d"));
		assertNull(DotNetDebuggerCompilerGenerateUtil.extractLambdaInfo("M", "C"));
	}

	@Test
	public void testLambdaHolderType()
	{
		assertTrue(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("<>c"));
		assertTrue(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("<>c__DisplayClass1_0"));
		assertTrue(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("<>c__DisplayClass1_1`1"));
		assertTrue(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("<>c__DisplayClass5"));
		assertTrue(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("<M>c__AnonStorey0"));

		assertFalse(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("<<M>b__3>d"));
		assertFalse(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("<M>d__1"));
		assertFalse(DotNetDebuggerCompilerGenerateUtil.isLambdaHolderTypeName("Nested"));
	}

	@Test
	public void testYieldPattern()
	{
		assertTrue(DotNetDebuggerCompilerGenerateUtil.YieldNestedTypePattern.matcher("<_LoadPic26>c__Iterator1").matches());
		assertTrue(DotNetDebuggerCompilerGenerateUtil.YieldNestedTypePattern.matcher("<_LoadPic26>c__IteratorA").matches());
		assertTrue(DotNetDebuggerCompilerGenerateUtil.YieldNestedTypePattern.matcher("<_LoadPic26>c__Iterator1B1333").matches());
	}
}
