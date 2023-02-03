import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import site.zhenbin.simplerpc.Client;
import site.zhenbin.simplerpc.RpcProxyFactory;
import site.zhenbin.simplerpc.Server;
import site.zhenbin.simplerpc.service.HelloService;

/**
 * Rpc测试类
 *
 * @author Liuzhenbin
 * @date 2023/2/3 10:38
 **/
public class RpcTest {

    private static Client client;
    private static Server server;

    @BeforeAll
    public static void init() throws InterruptedException {
        server = new Server(8000);
        client = new Client("localhost", 8000);
    }

    @AfterAll
    public static void clean() {
        server.close();
        client.disconnect();
    }

    @Test
    public void helloService() {
        HelloService helloService = RpcProxyFactory.create(HelloService.class, client);
        System.out.println(helloService.say("你好"));
    }
}
