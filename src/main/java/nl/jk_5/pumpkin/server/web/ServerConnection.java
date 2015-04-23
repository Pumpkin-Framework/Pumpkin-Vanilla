package nl.jk_5.pumpkin.server.web;

import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.settings.Settings;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nullable;

@NonnullByDefault
public class ServerConnection {

    private static final Logger logger = LogManager.getLogger();

    @Nullable
    private Channel channel;

    public void connect(){
        URI uri;
        try{
            uri = new URI(Settings.server.url);
        }catch(URISyntaxException e){
            logger.error("Invalid url syntax for server.url config value: {}", e.getMessage());
            return;
        }
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port;
        if(uri.getPort() == -1){
            if("http".equalsIgnoreCase(scheme)){
                port = 80;
            }else if("https".equalsIgnoreCase(scheme)){
                port = 443;
            }else{
                port = -1;
            }
        }else{
            port = uri.getPort();
        }

        if(!"ws".equalsIgnoreCase(scheme)){
            logger.error("Only ws is supported. Found {}", scheme);
            return;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        PacketHandler handler = new PacketHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(new ConnectionInitializer(handler));
        b.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                channel = future.channel();
            }
        });
    }

    public void send(JsonObject obj){
        if(!obj.has("type")){
            logger.warn("Packet has no 'type' tag: {}", obj.toString());
        }
        if(channel != null && channel.isActive()){
            channel.writeAndFlush(new TextWebSocketFrame(obj.toString()));
        }
    }
}
