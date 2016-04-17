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

package consulo.dotnet.microsoft.debugger;

import java.util.Map;
import java.util.UUID;

import org.jboss.netty.channel.Channel;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Ref;
import com.intellij.util.Consumer;
import com.intellij.util.concurrency.Semaphore;
import consulo.dotnet.microsoft.debugger.protocol.ClientMessage;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class MicrosoftDebuggerClientContext
{
	private final Map<String, Consumer<Object>> myWaitMap;
	private final Channel myChannel;

	public MicrosoftDebuggerClientContext(Channel channel, Map<String, Consumer<Object>> waitMap)
	{
		myChannel = channel;
		myWaitMap = waitMap;
	}

	public <T> T sendAndReceive(Object request)
	{
		final ClientMessage clientMessage = new ClientMessage();
		clientMessage.Id = UUID.randomUUID().toString();

		clientMessage.Type = request.getClass().getSimpleName();
		clientMessage.Object = request;

		final String jsonText = new Gson().toJson(clientMessage);

		//System.out.println("send " + jsonText);
		final Ref<Object> ref = Ref.create();
		final Semaphore semaphore = new Semaphore();
		semaphore.down();

		myWaitMap.put(clientMessage.Id, new Consumer<Object>()
		{
			@Override
			public void consume(Object o)
			{
				ref.set(o);
				semaphore.up();
			}
		});

		myChannel.write(jsonText);

		semaphore.waitFor();
		return (T) ref.get();
	}

	public <T> void sendAndReceiveInAnotherThread(Object request, final Consumer<T> after)
	{
		final ClientMessage clientMessage = new ClientMessage();
		clientMessage.Id = UUID.randomUUID().toString();

		clientMessage.Type = request.getClass().getSimpleName();
		clientMessage.Object = request;

		final String jsonText = new Gson().toJson(clientMessage);

		//System.out.println("send: " + jsonText);
		ApplicationManager.getApplication().executeOnPooledThread(new Runnable()
		{
			@Override
			public void run()
			{
				final Ref<Object> ref = Ref.create();
				final Semaphore semaphore = new Semaphore();
				semaphore.down();

				myWaitMap.put(clientMessage.Id, new Consumer<Object>()
				{
					@Override
					public void consume(Object o)
					{
						ref.set(o);
						semaphore.up();
					}
				});

				myChannel.write(jsonText);

				semaphore.waitFor();
				after.consume((T) ref.get());
			}
		});
	}
}
