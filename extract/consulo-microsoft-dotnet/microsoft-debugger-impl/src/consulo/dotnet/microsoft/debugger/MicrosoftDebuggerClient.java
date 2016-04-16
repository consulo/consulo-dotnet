package consulo.dotnet.microsoft.debugger;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

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
import consulo.dotnet.microsoft.debugger.protocol.ClientMessage;
import consulo.dotnet.microsoft.debugger.protocol.event.OnEvent;
import consulo.dotnet.microsoft.debugger.protocol.event.OnEventVisitor;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
class MicrosoftDebuggerClient
{
	private ClientBootstrap myClientBootstrap;
	private DebugConnectionInfo myDebugConnectionInfo;

	private ChannelFuture myChannelFuture;

	MicrosoftDebuggerClient(DebugConnectionInfo debugConnectionInfo, final OnEventVisitor visitor)
	{
		myDebugConnectionInfo = debugConnectionInfo;
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

		myClientBootstrap = new ClientBootstrap(factory);

		myClientBootstrap.setPipelineFactory(new ChannelPipelineFactory()
		{
			@Override
			public ChannelPipeline getPipeline()
			{
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));

				// Encoder
				pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));

				pipeline.addLast("handler", new SimpleChannelUpstreamHandler()
				{
					@Override
					public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
					{
					}

					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
					{
						Object message = e.getMessage();
						if(message instanceof String)
						{
							Gson gson = buildGson();

							OnEvent event = gson.fromJson((String) message, OnEvent.class);

							boolean c = event.accept(visitor, new MicrosoftDebuggerClientContext(ctx.getChannel()));

							ClientMessage clientMessage = new ClientMessage();
							clientMessage.Id = event.Id;
							clientMessage.Type = event.Type;
							clientMessage.Continue = c;

							ctx.getChannel().write(gson.toJson(clientMessage));
						}
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
					{
					}
				});
				return pipeline;
			}
		});

		myClientBootstrap.setOption("tcpNoDelay", true);
		myClientBootstrap.setOption("keepAlive", true);
	}

	private static Gson buildGson()
	{
		return new GsonBuilder().registerTypeAdapter(OnEvent.class, new JsonDeserializer<OnEvent>()
		{
			@Override
			public OnEvent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
			{
				assert jsonElement instanceof JsonObject;
				OnEvent event = new OnEvent();
				event.Id = ((JsonObject) jsonElement).getAsJsonPrimitive("Id").getAsString();
				event.Type = ((JsonObject) jsonElement).getAsJsonPrimitive("Type").getAsString();

				Class<?> typeClass = null;
				try
				{
					typeClass = Class.forName("consulo.dotnet.microsoft.debugger.protocol.event." + event.Type, true, MicrosoftDebuggerClient.class.getClassLoader());
				}
				catch(ClassNotFoundException e)
				{
					throw new JsonParseException(e);
				}

				JsonObject object = ((JsonObject) jsonElement).getAsJsonObject("Object");

				event.Object = jsonDeserializationContext.deserialize(object, typeClass);

				return event;
			}
		}).create();
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
