package bugcodes.server.uptime;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.TimeUnit;

/**
 * @author bugcoder
 */
public class UptimeClientHandler extends SimpleChannelInboundHandler<Object>{

    long startTime = -1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (startTime<0){
            startTime = System.currentTimeMillis();
        }
        println("Connected to:" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //Discard received data
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)){
            return;
        }
        IdleStateEvent e = (IdleStateEvent)evt;
        if (e.state()== IdleState.READER_IDLE){
            println("Disconnecting due to no inbund traffic");
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        println("Disconnected from :"+ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        println("Sleeping for:"+UptimeClient.RECONNECT_DELAY+'s');
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                println("Reconnecting to:"+UptimeClient.HOST+":"+UptimeClient.PORT);
                UptimeClient.connect();
            }
        },UptimeClient.RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    void println(String msg) {
        if (startTime < 0){
            System.err.format("[Service is Down]%s%n",msg);
        }else {
            System.err.format("[Uptime:%5ds]%s%n",(System.currentTimeMillis()-startTime)/1000,msg);
        }

    }

}
