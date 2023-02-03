package site.zhenbin.simplerpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Rpc解码器
 *
 * @author Liuzhenbin
 * @date 2023/2/2 15:35
 **/
public class RpcDecoder<T> extends MessageToMessageDecoder<ByteBuf> {
    private Serializer serializer;
    private Class<T> clazz;

    public RpcDecoder(Class<T> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        T obj = serializer.deserialize(clazz, bytes);
        list.add(obj);
    }
}
