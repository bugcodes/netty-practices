package bugcodes.server.factorial;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;

/**
 * 服务端channel配置
 */
public class FactorialServerInitializer extends ChannelInitializer<SocketChannel>{

    private final SslContext sslCtx;

    public FactorialServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (null != sslCtx){
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //启动压缩流
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        //添加编解码
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());
        //添加业务逻辑
        //为每个新channel创建一个处理器，因为它是有状态的
        pipeline.addLast(new FactorialServerHandler());

    }
}
