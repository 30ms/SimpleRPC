package site.zhenbin.simplerpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 客户端远程方法调用代理
 *
 * @author Liuzhenbin
 * @date 2023/2/3 10:17
 **/
public class RpcClientProxyInvocationHandler implements InvocationHandler {
    private Client client;

    public RpcClientProxyInvocationHandler(Client client) {
        this.client = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        String requestId = UUID.randomUUID().toString();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        request.requestId = requestId;
        request.className = className;
        request.methodName = methodName;
        request.parameterClassNames = Arrays.stream(parameterTypes).map(Class::getName).toArray(String[]::new);
        request.parameters = args;
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = client.send(request);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        if (rpcResponse.error != null) {
            throw new RuntimeException(rpcResponse.error);
        }
        return rpcResponse.result;
    }
}
