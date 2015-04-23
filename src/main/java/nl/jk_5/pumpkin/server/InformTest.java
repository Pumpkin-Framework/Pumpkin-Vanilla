package nl.jk_5.pumpkin.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class InformTest {

    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(65536));
                ch.pipeline().addLast(new LoggingHandler(LogLevel.WARN));
                ch.pipeline().addLast(new InformHandler());
            }
        });
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
        ChannelFuture future = bootstrap.bind(4000);
        future.syncUninterruptibly();
        future.channel().closeFuture().syncUninterruptibly();
    }

    private static class InformHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            ByteBuf content = msg.content();
            if(content.readableBytes() < 8){
                throw new DecoderException("Content too short");
            }
            if(content.readChar() != 'T' || content.readChar() != 'N' || content.readChar() != 'B' || content.readChar() != 'U'){
                throw new DecoderException("Bad packet magic");
            }



        }
    }
}
