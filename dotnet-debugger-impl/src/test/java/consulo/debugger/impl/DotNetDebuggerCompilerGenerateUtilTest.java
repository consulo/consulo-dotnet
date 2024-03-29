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
	public void testYieldPattern()
	{
		assertTrue(DotNetDebuggerCompilerGenerateUtil.YieldNestedTypePattern.matcher("<_LoadPic26>c__Iterator1").matches());
		assertTrue(DotNetDebuggerCompilerGenerateUtil.YieldNestedTypePattern.matcher("<_LoadPic26>c__IteratorA").matches());
		assertTrue(DotNetDebuggerCompilerGenerateUtil.YieldNestedTypePattern.matcher("<_LoadPic26>c__Iterator1B1333").matches());
	}
}
