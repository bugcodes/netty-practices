package bugcodes.server.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

/**
 * @author bugcoder
 */
public class BigIntegerDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //验证数据长度是否可用
        if (in.readableBytes() < 5){
            return;
        }
        in.markReaderIndex();
        //校验魔数'F'
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F'){
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic Number:" + magicNumber);
        }
        //一直等到所有的数据到达
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        //把消息转换为Biginteger
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);
        out.add(new BigInteger(decoded));
    }
}
