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

package consulo.dotnet.debugger.proxy;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetCharValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetVirtualMachineProxy
{
	@NotNull
	List<DotNetThreadProxy> getThreads();

	@NotNull
	DotNetStringValueProxy createStringValue(@NotNull String value);

	@NotNull
	DotNetCharValueProxy createCharValue(char value);

	@NotNull
	DotNetBooleanValueProxy createBooleanValue(boolean value);

	@NotNull
	DotNetNumberValueProxy createNumberValue(int tag, @NotNull Number value);

	@NotNull
	DotNetNullValueProxy createNullValue();
}
