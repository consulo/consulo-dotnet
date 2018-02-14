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

package consulo.dotnet.mono.debugger.proxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import mono.debugger.Location;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoSourceLocation implements DotNetSourceLocation
{
	private Location myLocation;

	public MonoSourceLocation(Location location)
	{
		myLocation = location;
	}

	@Nullable
	@Override
	public String getFilePath()
	{
		return myLocation.sourcePath();
	}

	@Override
	public int getLineZeroBased()
	{
		return myLocation.lineNumber() - 1;
	}

	@Override
	public int getLineOneBased()
	{
		return myLocation.lineNumber();
	}

	@Override
	public int getColumn()
	{
		return myLocation.columnNumber();
	}

	@Nonnull
	@Override
	public DotNetMethodProxy getMethod()
	{
		return new MonoMethodProxy(myLocation.method());
	}
}
