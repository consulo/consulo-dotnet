/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.csharp.lang.parser.macro;

import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class MacroActiveBlockInfo
{
	private final IElementType myElementType;
	private final int myStartOffset;
	private int myStopOffset = -1;

	public MacroActiveBlockInfo(IElementType elementType, int startOffset)
	{
		myElementType = elementType;
		myStartOffset = startOffset;
	}

	public IElementType getElementType()
	{
		return myElementType;
	}

	public int getStartOffset()
	{
		return myStartOffset;
	}

	public int getStopOffset()
	{
		return myStopOffset;
	}

	public void setStopOffset(int stopOffset)
	{
		myStopOffset = stopOffset;
	}
}
