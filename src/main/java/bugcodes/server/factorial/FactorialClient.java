package bugcodes.server.factorial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

/**
 * @author
 */
public final class FactorialClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host","127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port","8088"));
    static final int COUNT = Integer.parseInt(System.getProperty("count","1000"));

    public static void main(String[] args) throws SSLException, InterruptedException {
        //注册ssl
        final SslContext sslCtx;
        if (SSL){
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }else {
            sslCtx = null;
        }
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new FactorialClientInitializer(sslCtx));
            //建立连接
            ChannelFuture f = b.connect(HOST, PORT).sync();
            //获取处理实例以便检索信息
            FactorialCilentHandler handler = (FactorialCilentHandler)f.channel().pipeline().last();
            //打印结果
            System.err.format("Factorial of %,d is: %,d",COUNT,handler.getFactorial());
        }finally {
            workGroup.shutdownGracefully();
        }

    }

}
