package consulo.dotnet.microsoft.debugger;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.consulo.lombok.annotations.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.intellij.openapi.util.Ref;
import com.intellij.util.ConcurrencyUtil;
import com.intellij.util.Consumer;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.concurrency.Semaphore;
import consulo.dotnet.microsoft.debugger.protocol.ClientMessage;
import consulo.dotnet.microsoft.debugger.protocol.ServerMessage;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.BadRequestResult;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.OnEventVisitor;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
@Logger
public class MicrosoftDebuggerClient
{
	private class MicrosoftDebuggerNettyHandler extends SimpleChannelUpstreamHandler
	{
		private final ExecutorService myExecutorService = Executors.newCachedThreadPool(ConcurrencyUtil.newNamedThreadFactory("MS Task Executors"));

		private Map<String, Consumer<Object>> myWaitMap = new ConcurrentHashMap<String, Consumer<Object>>();
		private final OnEventVisitor myVisitor;

		public MicrosoftDebuggerNettyHandler(OnEventVisitor visitor)
		{
			myVisitor = visitor;
		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
		{
			stopAllWaiters();
		}

		@Override
		public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
		{
			stopAllWaiters();
		}

		@Override
		public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception
		{
			//System.out.println(e.getMessage() + " " + myWaitMap);

			myExecutorService.execute(new Runnable()
			{
				@Override
				public void run()
				{
					Object message = e.getMessage();
					if(message instanceof String)
					{
						Gson gson = buildGson();

						ServerMessage event = gson.fromJson((String) message, ServerMessage.class);
						Consumer<Object> objectConsumer = myWaitMap.remove(event.Id);
						if(objectConsumer != null)
						{
							objectConsumer.consume(event.Object);
						}
						else
						{
							boolean c = event.accept(myVisitor, MicrosoftDebuggerClient.this);

							ClientMessage clientMessage = new ClientMessage();
							clientMessage.Id = event.Id;
							clientMessage.Type = event.Type;
							clientMessage.Continue = c;

							ctx.getChannel().write(gson.toJson(clientMessage));
						}
					}
				}
			});
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
		{
			e.getCause().printStackTrace();

			stopAllWaiters();
		}

		public void stopAllWaiters()
		{
			for(Consumer<Object> objectConsumer : myWaitMap.values())
			{
				objectConsumer.consume(null);
			}
			myWaitMap.clear();
		}
	}

	private ClientBootstrap myClientBootstrap;
	private DebugConnectionInfo myDebugConnectionInfo;

	private ChannelFuture myChannelFuture;

	private MicrosoftDebuggerNettyHandler myChannelHandler;

	MicrosoftDebuggerClient(MicrosoftDebuggerProcess debuggerProcess, DebugConnectionInfo debugConnectionInfo)
	{
		myDebugConnectionInfo = debugConnectionInfo;
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

		myClientBootstrap = new ClientBootstrap(factory);

		myChannelHandler = new MicrosoftDebuggerNettyHandler(new MicrosoftDebuggerEventVisitor(debuggerProcess));

		myClientBootstrap.setPipelineFactory(new ChannelPipelineFactory()
		{
			@Override
			public ChannelPipeline getPipeline()
			{
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));

				// Encoder
				pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));

				pipeline.addLast("handler", myChannelHandler);
				return pipeline;
			}
		});

		myClientBootstrap.setOption("tcpNoDelay", true);
		myClientBootstrap.setOption("keepAlive", true);
	}

	private static Gson buildGson()
	{
		return new GsonBuilder().registerTypeAdapter(ServerMessage.class, new JsonDeserializer<ServerMessage>()
		{
			@Override
			public ServerMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{
				assert jsonElement instanceof JsonObject;
				ServerMessage event = new ServerMessage();
				event.Id = ((JsonObject) jsonElement).getAsJsonPrimitive("Id").getAsString();
				event.Type = ((JsonObject) jsonElement).getAsJsonPrimitive("Type").getAsString();

				Class<?> typeClass = null;
				try
				{
					typeClass = Class.forName("consulo.dotnet.microsoft.debugger.protocol.serverMessage." + event.Type, true, MicrosoftDebuggerClient.class.getClassLoader());
				}
				catch(ClassNotFoundException e)
				{
					typeClass = BadRequestResult.class;
				}

				JsonObject object = ((JsonObject) jsonElement).getAsJsonObject("Object");

				event.Object = jsonDeserializationContext.deserialize(object, typeClass);

				return event;
			}
		}).create();
	}

	public <T> T sendAndReceive(Object request, final Class<T> clazz)
	{
		Channel channel = myChannelFuture.getChannel();
		if(channel == null || !channel.isConnected())
		{
			return ReflectionUtil.newInstance(clazz);
		}

		final ClientMessage clientMessage = new ClientMessage();
		clientMessage.Id = UUID.randomUUID().toString();

		clientMessage.Type = request.getClass().getSimpleName();
		clientMessage.Object = request;

		final String jsonText = new Gson().toJson(clientMessage);

		//System.out.println("send " + jsonText);
		final Ref<Object> ref = Ref.create();
		final Semaphore semaphore = new Semaphore();
		semaphore.down();

		myChannelHandler.myWaitMap.put(clientMessage.Id, new Consumer<Object>()
		{
			@Override
			public void consume(Object o)
			{
				ref.set(o == null ? ReflectionUtil.newInstance(clazz) : o);
				semaphore.up();
			}
		});

		channel.write(jsonText);

		semaphore.waitFor();
		Object value = ref.get();
		if(clazz.isInstance(value))
		{
			return clazz.cast(value);
		}
		else
		{
			LOGGER.error("Receive wrong value: " + request);
			return ReflectionUtil.newInstance(clazz);
		}
	}

	public void connect()
	{
		myChannelFuture = myClientBootstrap.connect(new InetSocketAddress(myDebugConnectionInfo.getHost(), myDebugConnectionInfo.getPort()));
	}

	public void disconnect()
	{
		if(myChannelFuture != null)
		{
			myChannelFuture.getChannel().close();
		}
	}
}
