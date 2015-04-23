package nl.jk_5.pumpkin.server.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

@NonnullByDefault
class PacketHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LogManager.getLogger();
    private static final JsonParser parser = new JsonParser();

    private final WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    public PacketHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture getHandshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.handshakeFuture = ctx.newPromise();
        this.handshakeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                PacketProcessor.onOpen(future.channel());
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("WebSocket Client disconnected");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if(!handshaker.isHandshakeComplete()){
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            logger.info("WebSocket Client connected");
            handshakeFuture.setSuccess();
            return;
        }

        if(msg instanceof FullHttpResponse){
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (status=" + response.getStatus() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if(frame instanceof TextWebSocketFrame){
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            JsonObject in = parser.parse(textFrame.text()).getAsJsonObject();
            PacketProcessor.handle(in);
        }else if(frame instanceof CloseWebSocketFrame){
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("Pipeline exception in server connection", cause);
        if(!handshakeFuture.isDone()){
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
