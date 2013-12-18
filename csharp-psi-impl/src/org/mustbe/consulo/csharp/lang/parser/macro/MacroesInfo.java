/*
 * Copyright 2013 must-be.org
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class MacroesInfo
{
	public static final MacroesInfo EMPTY = new MacroesInfo();

	public static final Key<MacroesInfo> MACROES_INFO_KEY = Key.create("MacroesInfo");

	private Set<String> myDefineList = new HashSet<String>(0);
	private List<MacroActiveBlockInfo> myActiveBlockInfos = new ArrayList<MacroActiveBlockInfo>(0);

	public MacroesInfo()
	{
		myDefineList.add("DEBUG"); //TODO [VISTALL] for tests
	}

	public void define(String val)
	{
		myDefineList.add(val);
	}

	public boolean isDefined(String val)
	{
		return myDefineList.contains(val);
	}

	public void addActiveBlock(IElementType t, int currentOffset)
	{
		myActiveBlockInfos.add(new MacroActiveBlockInfo(t, currentOffset));
	}

	public MacroActiveBlockInfo findStartActiveBlock(int offset)
	{
		for(MacroActiveBlockInfo blockInfo : ContainerUtil.iterateBackward(myActiveBlockInfos))
		{
			if(blockInfo.getStartOffset() == offset)
			{
				return blockInfo;
			}
		}
		return null;
	}

	public MacroActiveBlockInfo findStartActiveBlockForStop(IElementType t, int offset)
	{
		for(MacroActiveBlockInfo blockInfo : ContainerUtil.iterateBackward(myActiveBlockInfos))
		{
			if(blockInfo.getElementType() == t && blockInfo.getStopOffset() == -1)
			{
				blockInfo.setStopOffset(offset);
				return blockInfo;
			}
		}
		return null;
	}
}
