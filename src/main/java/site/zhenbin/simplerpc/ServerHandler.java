package site.zhenbin.simplerpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Rpc服务端处理器，用于查找服务和调用
 *
 * @author Liuzhenbin
 * @date 2023/2/2 17:49
 **/
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.requestId = msg.requestId;
        Method method = null;
        Object service = null;
        try {
            Class<?> serviceClass = Class.forName(msg.className);
            List<Class<?>> list = new ArrayList<>();
            for (String parameterClassName : msg.parameterClassNames) {
                Class<?> aClass = Class.forName(parameterClassName);
                list.add(aClass);
            }
            Class<?>[] parameterClasses = list.toArray(Class[]::new);
            method = serviceClass.getMethod(msg.methodName, parameterClasses);
            ServiceLoader<?> serviceLoader = ServiceLoader.load(serviceClass);
            for (Object o : serviceLoader) {
                service = o;
                break;
            }
            if (service != null) {
                rpcResponse.result = method.invoke(service, msg.parameters);
            } else {
                throw new RuntimeException(String.format("service class [%s] not find",serviceClass));
            }
        } catch (Throwable e) {
            rpcResponse.error = e.getMessage();
        }
        ctx.writeAndFlush(rpcResponse);
    }
}
