package site.zhenbin.simplerpc;

import java.lang.reflect.Proxy;

/**
 * rpc代理工厂，用于创建远程服务的代理对象
 *
 * @author Liuzhenbin
 * @date 2023/2/3 11:46
 **/
public class RpcProxyFactory {

    public static  <T> T create(Class<T> serviceInterface, Client client) {
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{serviceInterface}, new RpcClientProxyInvocationHandler(client));
    }
}
