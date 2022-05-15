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

package consulo.dotnet.util;

import consulo.util.dataholder.Key;
import consulo.util.io.NetUtil;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DebugConnectionInfo
{
	public static final Key<DebugConnectionInfo> KEY = Key.create(DebugConnectionInfo.class);

	private final String myHost;
	private final int myPort;
	private final boolean myServer;

	public DebugConnectionInfo(String host, int port, boolean server)
	{
		myHost = host;
		myServer = server;
		if(port == -1)
		{
			myPort = NetUtil.tryToFindAvailableSocketPort();
		}
		else
		{
			myPort = port;
		}
	}

	public int getPort()
	{
		return myPort;
	}

	public boolean isServer()
	{
		return myServer;
	}

	public String getHost()
	{
		return myHost;
	}
}
