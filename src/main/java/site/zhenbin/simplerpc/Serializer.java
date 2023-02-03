package site.zhenbin.simplerpc;

/**
 * 序列化接口,用于RPC请求和响应的编解码器
 *
 * @author Liuzhenbin
 * @date 2023/2/2 16:00
 **/
public interface Serializer {
    /**
     * java Object to binary
     */
    byte[] serialize(Object object);

    /**
     * Binary conversion to java objects
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
