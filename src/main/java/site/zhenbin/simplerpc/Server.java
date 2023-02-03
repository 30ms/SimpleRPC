package site.zhenbin.simplerpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rpc服务端
 *
 * @author Liuzhenbin
 * @date 2023/2/2 10:54
 **/
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;
    private final Channel channel;

    public Server(int port) throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        JSONSerializer jsonSerializer = new JSONSerializer(new ObjectMapper());
        channel = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                //等待队列大小
                .option(ChannelOption.SO_BACKLOG, 100)
                //地址复用
                .option(ChannelOption.SO_REUSEADDR, true)
                //开启Nagle算法，
                //网络好的时候：对响应要求比较高的业务，不建议开启，比如玩游戏，键盘数据，鼠标响应等，需要实时呈现；
                //            对响应比较低的业务，建议开启，可以有效减少小数据包传输。
                //网络差的时候：不建议开启，否则会导致整体效果更差。
//                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast(new RpcDecoder<>(RpcRequest.class, jsonSerializer))
                                .addLast(new LengthFieldPrepender(4))
                                .addLast(new RpcEncoder(jsonSerializer))
                                .addLast(new ServerHandler());
                    }
                })
                .bind(port).sync().channel();
        LOGGER.info("auth server channel (port: {}) start",port);
        channel.closeFuture().addListener((ChannelFutureListener) future -> LOGGER.info("auth server channel (port: {}) is closed", port));
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }
}
