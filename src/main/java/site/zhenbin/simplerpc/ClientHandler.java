package site.zhenbin.simplerpc;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 用于客户端请求的状态管理
 *
 * @author Liuzhenbin
 * @date 2023/2/2 17:26
 **/
public class ClientHandler extends ChannelDuplexHandler {
    private final Map<String, CompletableFuture<RpcResponse>> futureMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response = (RpcResponse) msg;
        CompletableFuture<RpcResponse> defaultFuture = futureMap.get(response.requestId);
        defaultFuture.complete(response);
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        futureMap.putIfAbsent(request.requestId, new CompletableFuture<>());
        super.write(ctx, msg, promise);
    }

    public RpcResponse getRpcResponse(String requestId) {
        try {
            CompletableFuture<RpcResponse> future = futureMap.get(requestId);
            return future.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            futureMap.remove(requestId);
        }
    }
}
