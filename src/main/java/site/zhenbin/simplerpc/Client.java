package site.zhenbin.simplerpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rpc连接的客户端
 *
 * @author Liuzhenbin
 * @date 2023/2/2 10:55
 **/
public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private ClientHandler clientHandler = new ClientHandler();

    public Client(String host, int port) throws InterruptedException {
        eventLoopGroup = new NioEventLoopGroup();
        JSONSerializer jsonSerializer = new JSONSerializer(new ObjectMapper());
        try {
            channel = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new RpcDecoder<>(RpcResponse.class, jsonSerializer))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new RpcEncoder(jsonSerializer))
                                    .addLast(clientHandler);

                        }
                    })
                    .connect(host, port).sync().channel();
            channel.closeFuture().addListener(future -> LOGGER.info("channel is closed!"));
        } catch (Throwable e) {
            eventLoopGroup.shutdownGracefully();
            throw e;
        }
    }

    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public RpcResponse send(RpcRequest request) throws InterruptedException {
        channel.writeAndFlush(request).sync();
        return clientHandler.getRpcResponse(request.requestId);
    }
}
