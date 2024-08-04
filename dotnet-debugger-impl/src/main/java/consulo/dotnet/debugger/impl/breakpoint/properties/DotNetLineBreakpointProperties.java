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

package consulo.dotnet.debugger.impl.breakpoint.properties;

import consulo.execution.debug.breakpoint.XBreakpointProperties;
import consulo.util.xml.serializer.XmlSerializerUtil;
import consulo.util.xml.serializer.annotation.OptionTag;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22.07.2015
 */
public class DotNetLineBreakpointProperties extends XBreakpointProperties<DotNetLineBreakpointProperties>
{
	// null - All, will pause for method and lambdas
	// -1 - pause only method
	// 0++ - will pause only lambda by index
	private Integer myExecutableChildrenAtLineIndex = null;

	@Nullable
	@Override
	public DotNetLineBreakpointProperties getState()
	{
		return this;
	}

	@Override
	public void loadState(DotNetLineBreakpointProperties state)
	{
		XmlSerializerUtil.copyBean(state, this);
	}

	@Nullable
	@OptionTag("executable-children-at-line-index")
	public Integer getExecutableChildrenAtLineIndex()
	{
		return myExecutableChildrenAtLineIndex;
	}

	public void setExecutableChildrenAtLineIndex(Integer executableChildrenAtLineIndex)
	{
		this.myExecutableChildrenAtLineIndex = executableChildrenAtLineIndex;
	}
}
