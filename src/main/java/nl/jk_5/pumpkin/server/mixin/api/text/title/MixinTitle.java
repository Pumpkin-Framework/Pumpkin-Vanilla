package nl.jk_5.pumpkin.server.mixin.api.text.title;

import com.google.common.base.Optional;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.title.Title;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinTitle;

import java.util.Arrays;

@Mixin(value = Title.class, remap = false)
public abstract class MixinTitle implements IMixinTitle {

    @Shadow protected Optional<Text> title;
    @Shadow protected Optional<Text> subtitle;
    @Shadow protected Optional<Integer> fadeIn;
    @Shadow protected Optional<Integer> stay;
    @Shadow protected Optional<Integer> fadeOut;
    @Shadow protected boolean clear;
    @Shadow protected boolean reset;

    private S45PacketTitle[] packets;

    @Override
    public void send(EntityPlayerMP player) {
        if (this.packets == null) {
            S45PacketTitle[] packets = new S45PacketTitle[5];
            int i = 0;

            if (this.clear) {
                packets[i++] = new S45PacketTitle(S45PacketTitle.Type.CLEAR, null);
            }
            if (this.reset) {
                packets[i++] = new S45PacketTitle(S45PacketTitle.Type.RESET, null);
            }
            if (this.fadeIn.isPresent() || this.stay.isPresent() || this.fadeOut.isPresent()) {
                packets[i++] = new S45PacketTitle(this.fadeIn.or(20), this.stay.or(60), this.fadeOut.or(20));
            }
            //TODO: fix this
            /*if (this.subtitle.isPresent()) {
                packets[i++] = new S45PacketTitle(S45PacketTitle.Type.SUBTITLE, ((IMixinText) this.subtitle.get()).toComponent(((Player) player)
                        .getLocale()));
            }
            if (this.title.isPresent()) {
                packets[i++] = new S45PacketTitle(S45PacketTitle.Type.TITLE, ((IMixinText) this.title.get()).toComponent(((Player) player)
                        .getLocale()));
            }*/

            this.packets = i == packets.length ? packets : Arrays.copyOf(packets, i);
        }

        for (S45PacketTitle packet : this.packets) {
            player.playerNetServerHandler.sendPacket(packet);
        }
    }
}
