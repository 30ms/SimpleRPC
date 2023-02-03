package site.zhenbin.simplerpc.service;

/**
 * 服务实现
 *
 * @author Liuzhenbin
 * @date 2023/2/3 10:24
 **/
public class HelloServiceImpl implements HelloService{
    @Override
    public String say(String msg) {
        return "say:" + msg;
    }
}
