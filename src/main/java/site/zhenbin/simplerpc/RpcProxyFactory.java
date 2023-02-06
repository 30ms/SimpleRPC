package site.zhenbin.simplerpc;

import java.lang.reflect.Proxy;

/**
 * rpc代理工厂，用于创建远程服务的代理对象
 *
 * @author Liuzhenbin
 * @date 2023/2/3 11:46
 **/
public class RpcProxyFactory {

    private RpcClientProxyInvocationHandler handler;

    public RpcProxyFactory(Client client) {
        handler = new RpcClientProxyInvocationHandler(client);
    }

    public static RpcProxyFactory build(Client client) {
        return new RpcProxyFactory(client);
    }

    public <T> T create(Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{serviceInterface}, handler);
    }
}
