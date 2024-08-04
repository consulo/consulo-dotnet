/*
 * Copyright 2013-2016 consulo.io
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

package consulo.dotnet.debugger.proxy.light;

import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 28-Nov-16.
 */
public class LightSourceLocation implements DotNetSourceLocation
{
	private String myFilePath;
	private int myLineZeroBased;
	private int myLineOneBased;
	private int myColumn;
	private DotNetMethodProxy myMethodProxy;

	public LightSourceLocation(DotNetSourceLocation location)
	{
		myFilePath = location.getFilePath();
		myLineZeroBased = location.getLineZeroBased();
		myLineOneBased = location.getLineOneBased();
		myColumn = location.getColumn();
		myMethodProxy = location.getMethod().lightCopy();
	}

	@Nullable
	@Override
	public String getFilePath()
	{
		return myFilePath;
	}

	@Override
	public int getLineZeroBased()
	{
		return myLineZeroBased;
	}

	@Override
	public int getLineOneBased()
	{
		return myLineOneBased;
	}

	@Override
	public int getColumn()
	{
		return myColumn;
	}

	@Nonnull
	@Override
	public DotNetMethodProxy getMethod()
	{
		return myMethodProxy;
	}
}
