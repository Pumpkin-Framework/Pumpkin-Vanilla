package nl.jk_5.pumpkin.server.mixin.core.status;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.MinecraftVersion;
import nl.jk_5.pumpkin.api.event.server.StatusPingEvent;
import nl.jk_5.pumpkin.api.status.Favicon;
import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.server.status.PumpkinFavicon;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;

import java.io.IOException;
import javax.annotation.Nullable;

@Mixin(ServerStatusResponse.class)
public abstract class MixinServerStatusResponse implements StatusPingEvent.Response {

    @Shadow
    private IChatComponent serverMotd;
    private Text description;

    @Shadow
    private ServerStatusResponse.PlayerCountData playerCount;
    private ServerStatusResponse.PlayerCountData playerBackup;

    @Shadow
    private ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersion;

    @Shadow
    private String favicon;
    private Favicon faviconHandle;

    @Override
    public Text getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(Text description) {
        this.description = checkNotNull(description, "description");
        this.serverMotd = PumpkinTexts.toComponent(description); // TODO: Hope we get sent the locale
    }

    @Overwrite
    public void setServerDescription(IChatComponent motd) {
        this.serverMotd = checkNotNull(motd, "motd");
        this.description = PumpkinTexts.toText(motd);
    }

    @Override
    public Optional<Players> getPlayers() {
        return Optional.fromNullable((Players) this.playerCount);
    }

    @Override
    public void setHidePlayers(boolean hide) {
        if ((this.playerCount == null) != hide) {
            if (hide) {
                this.playerBackup = this.playerCount;
                this.playerCount = null;
            } else {
                this.playerCount = this.playerBackup;
                this.playerBackup = null;
            }
        }
    }

    @Override
    public MinecraftVersion getVersion() {
        return (MinecraftVersion) this.protocolVersion;
    }

    @Override
    public Optional<Favicon> getFavicon() {
        return Optional.fromNullable(this.faviconHandle);
    }

    @Override
    public void setFavicon(@Nullable Favicon favicon) {
        this.faviconHandle = favicon;
        if (this.faviconHandle != null) {
            this.favicon = ((PumpkinFavicon) this.faviconHandle).getEncoded();
        } else {
            this.favicon = null;
        }
    }

    @Overwrite
    public void setFavicon(String faviconBlob) {
        if (faviconBlob == null) {
            this.favicon = null;
            this.faviconHandle = null;
        } else {
            try {
                this.faviconHandle = new PumpkinFavicon(faviconBlob);
                this.favicon = faviconBlob;
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
    }
}
