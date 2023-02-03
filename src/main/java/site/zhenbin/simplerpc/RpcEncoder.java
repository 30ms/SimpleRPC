package site.zhenbin.simplerpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Rpc编码器
 *
 * @author Liuzhenbin
 * @date 2023/2/2 15:34
 **/
public class RpcEncoder extends MessageToByteEncoder<Object> {
    private Serializer serializer;

    public RpcEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {
        byte[] bytes = serializer.serialize(msg);
        byteBuf.writeBytes(bytes);
    }
}
