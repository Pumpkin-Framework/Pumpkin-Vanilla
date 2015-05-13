package nl.jk_5.pumpkin.server.mixin.core.status;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.PingResponseHandler;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.server.status.PumpkinLegacyMinecraftVersion;
import nl.jk_5.pumpkin.server.status.PumpkinStatusResponse;

import java.net.InetSocketAddress;

@Mixin(PingResponseHandler.class)
public abstract class MixinPingResponseHandler extends ChannelInboundHandlerAdapter {

    @Shadow
    private static Logger logger;

    @Shadow
    private NetworkSystem networkSystem;

    private ByteBuf buf;

    @Shadow
    abstract void writeAndFlush(ChannelHandlerContext ctx, ByteBuf data);

    @Shadow
    abstract ByteBuf getStringBuffer(String string);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.buf = ctx.alloc().buffer();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if(this.buf != null){
            this.buf.release();
            this.buf = null;
        }
    }

    @Override
    @Overwrite
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        this.buf.writeBytes(m);
        m.release();

        this.buf.markReaderIndex();
        boolean result = false;

        try{
            result = readLegacy(ctx, this.buf);
        }finally{
            this.buf.resetReaderIndex();
            if(!result){
                ByteBuf buf = this.buf;
                this.buf = null;

                ctx.pipeline().remove("legacy_query");
                ctx.fireChannelRead(buf);
            }
        }
    }

    private boolean readLegacy(ChannelHandlerContext ctx, ByteBuf buf) {
        if (buf.readUnsignedByte() != 0xFE) {
            return false;
        }

        MinecraftServer server = this.networkSystem.getServer();
        InetSocketAddress client = (InetSocketAddress) ctx.channel().remoteAddress();
        ServerStatusResponse response;

        int i = buf.readableBytes();
        switch (i) {
            case 0:
                logger.debug("Ping: (<=1.3) from {}:{}", client.getAddress(), client.getPort());

                response = PumpkinStatusResponse.postLegacy(server, client, PumpkinLegacyMinecraftVersion.V1_3, null);
                if (response != null) {
                    this.writeResponse(ctx, String.format("%s§%d§%d",
                            PumpkinStatusResponse.getUnformattedMotd(response),
                            response.getPlayerCountData().getOnlinePlayerCount(),
                            response.getPlayerCountData().getMaxPlayers()));
                } else {
                    ctx.close();
                }

                break;
            case 1:
                if (buf.readUnsignedByte() != 0x01) {
                    return false;
                }

                logger.debug("Ping: (1.4-1.5) from {}:{}", client.getAddress(), client.getPort());

                response = PumpkinStatusResponse.postLegacy(server, client, PumpkinLegacyMinecraftVersion.V1_5, null);
                if (response != null) {
                    this.writeResponse(ctx, String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                            response.getProtocolVersionInfo().getProtocol(),
                            response.getProtocolVersionInfo().getName(),
                            PumpkinStatusResponse.getMotd(response),
                            response.getPlayerCountData().getOnlinePlayerCount(),
                            response.getPlayerCountData().getMaxPlayers()));
                } else {
                    ctx.close();
                }

                break;
            default:
                if (buf.readUnsignedByte() != 0x01 || buf.readUnsignedByte() != 0xFA) {
                    return false;
                }
                if (!buf.isReadable(2)) {
                    break;
                }
                short length = buf.readShort();
                if (!buf.isReadable(length * 2)) {
                    break;
                }
                if (!buf.readBytes(length * 2).toString(Charsets.UTF_16BE).equals("MC|PingHost")) {
                    return false;
                }
                if (!buf.isReadable(2)) {
                    break;
                }
                length = buf.readShort();
                if (!buf.isReadable(length)) {
                    break;
                }

                int protocol = buf.readUnsignedByte();
                length = buf.readShort();
                String host = buf.readBytes(length * 2).toString(Charsets.UTF_16BE);
                int port = buf.readInt();

                logger.debug("Ping: (1.6) from {}:{}", client.getAddress(), client.getPort());

                response =
                        PumpkinStatusResponse.postLegacy(server, client,
                                new PumpkinLegacyMinecraftVersion(PumpkinLegacyMinecraftVersion.V1_6, protocol),
                                InetSocketAddress.createUnresolved(host, port));
                if (response != null) {
                    this.writeResponse(ctx, String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                            response.getProtocolVersionInfo().getProtocol(),
                            response.getProtocolVersionInfo().getName(),
                            PumpkinStatusResponse.getMotd(response),
                            response.getPlayerCountData().getOnlinePlayerCount(),
                            response.getPlayerCountData().getMaxPlayers()));
                } else {
                    ctx.close();
                }

                break;
        }

        return true;
    }

    private void writeResponse(ChannelHandlerContext ctx, String response) {
        writeAndFlush(ctx, getStringBuffer(response));
    }
}
