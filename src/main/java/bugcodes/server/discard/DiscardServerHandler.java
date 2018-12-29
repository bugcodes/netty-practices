package bugcodes.server.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bugcoder
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter{

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscardServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //默默的丢弃
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()){
                LOGGER.info("receive msg is "+(char)in.readByte());
            }
        }finally {
            ReferenceCountUtil.release(in);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //出现异常时关闭链接
        cause.printStackTrace();
        ctx.close();
    }
}
