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

package consulo.dotnet.debugger.impl.breakpoint.properties;

import consulo.execution.debug.breakpoint.XBreakpointProperties;
import consulo.util.xml.serializer.XmlSerializerUtil;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
public class DotNetExceptionBreakpointProperties extends XBreakpointProperties<DotNetExceptionBreakpointProperties>
{
	public boolean NOTIFY_CAUGHT = true;
	public boolean NOTIFY_UNCAUGHT = true;
	public String VM_QNAME;
	public boolean SUBCLASSES = false;

	@Nullable
	@Override
	public DotNetExceptionBreakpointProperties getState()
	{
		return this;
	}

	@Override
	public void loadState(DotNetExceptionBreakpointProperties state)
	{
		XmlSerializerUtil.copyBean(state, this);
	}
}
