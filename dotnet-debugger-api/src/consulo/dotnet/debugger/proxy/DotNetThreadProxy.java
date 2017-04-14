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
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.util.ArrayUtil2;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public abstract class DotNetThreadProxy
{
	public abstract long getId();

	public abstract boolean isRunning();

	public abstract boolean isSuspended();

	@Nullable
	public abstract String getName();

	@NotNull
	public abstract List<DotNetStackFrameProxy> getFrames() throws DotNetNotSuspendedException;

	@Nullable
	public DotNetStackFrameProxy getFrame(int index) throws DotNetNotSuspendedException
	{
		List<DotNetStackFrameProxy> frames = getFrames();
		return ArrayUtil2.safeGet(frames, index);
	}
}
