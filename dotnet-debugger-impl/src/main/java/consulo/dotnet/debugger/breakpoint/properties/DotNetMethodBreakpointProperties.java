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

package consulo.dotnet.debugger.breakpoint.properties;

import consulo.debugger.breakpoint.XBreakpointProperties;
import consulo.util.xml.serializer.XmlSerializerUtil;

import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 03.05.2016
 */
public class DotNetMethodBreakpointProperties extends XBreakpointProperties<DotNetMethodBreakpointProperties>
{
	public boolean METHOD_ENTRY = true;
	public boolean METHOD_EXIT = true;

	@Nullable
	@Override
	public DotNetMethodBreakpointProperties getState()
	{
		return this;
	}

	@Override
	public void loadState(DotNetMethodBreakpointProperties state)
	{
		XmlSerializerUtil.copyBean(state, this);
	}
}
