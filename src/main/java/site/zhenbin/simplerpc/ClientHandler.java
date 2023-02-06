package site.zhenbin.simplerpc;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
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
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        CompletableFuture<RpcResponse> requestFuture = new CompletableFuture<>();
        futureMap.putIfAbsent(request.requestId, requestFuture);
        super.write(ctx, msg, promise.addListener((ChannelFutureListener) future -> {
            if (future.cause() != null) {
                requestFuture.completeExceptionally(future.cause());
            } else if (future.isCancelled()) {
                requestFuture.cancel(false);
            }
        }));
    }

    public RpcResponse getRpcResponse(RpcRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        try {
            CompletableFuture<RpcResponse> future = futureMap.get(request.requestId);
            return future.get(5, TimeUnit.SECONDS);
        }  finally {
            futureMap.remove(request.requestId);
        }
    }
}
