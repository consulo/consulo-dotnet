package consulo.dotnet.microsoft.debugger;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
class MicrosoftDebuggerClient
{
	private ClientBootstrap myClientBootstrap;
	private DebugConnectionInfo myDebugConnectionInfo;

	private ChannelFuture myChannelFuture;

	MicrosoftDebuggerClient(DebugConnectionInfo debugConnectionInfo)
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
						ctx.getChannel().write("{}");

						System.out.println("wrote");
					}

					@Override
					public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
					{
						Object message = e.getMessage();
						if(message instanceof String)
						{
							System.out.println("string");
						}
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
					{
						e.getCause().printStackTrace();
					}
				});
				return pipeline;
			}
		});

		myClientBootstrap.setOption("tcpNoDelay", true);
		myClientBootstrap.setOption("keepAlive", true);
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
