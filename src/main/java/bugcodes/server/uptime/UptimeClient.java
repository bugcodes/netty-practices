package bugcodes.server.uptime;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author bugcoder
 */
public final class UptimeClient {

    static final String HOST =System.getProperty("host","127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port","8080"));
    /**
     * 休息5秒，然后尝试重连接
     */
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay","5"));
    /**
     * 当服务器10秒钟没有发送任何消息时重新连接
     */
    private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout","10"));

    private static final UptimeClientHandler handler = new UptimeClientHandler();

    private static Bootstrap bs = new Bootstrap();

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        bs
                .group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(HOST,PORT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT,0,0),handler);
                    }
                });
        bs.connect();
    }

    static void connect(){
        bs.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause()!=null){
                    handler.startTime = -1;
                    handler.println("Failed to connect:"+future.cause());
                }
            }
        });
    }

}
