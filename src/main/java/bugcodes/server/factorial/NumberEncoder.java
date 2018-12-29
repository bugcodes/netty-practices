package bugcodes.server.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;

/**
 * 把Number类型的数据编码为二进制表示，该该表示包含一个魔数('F' or 0x46)和一个32位整型的长度前缀
 * 例如，42最终表示为{ 'F', 0, 0, 0, 1, 42 }
 */
public class NumberEncoder extends MessageToByteEncoder<Number>{

    @Override
    protected void encode(ChannelHandlerContext ctx, Number msg, ByteBuf out) throws Exception {
        //把msg转换BigInteger
        BigInteger v;
        if (msg instanceof BigInteger){
            v = (BigInteger)msg;
        }else {
            v = new BigInteger(String.valueOf(msg));
        }
        //再把数字转换为byte数组
        byte[] data = v.toByteArray();
        int dataLength = data.length;
        //写消息
        //'F'做为一个魔法前缀
        out.writeByte((byte)'F');
        out.writeInt(dataLength);
        out.writeBytes(data);
    }
}
