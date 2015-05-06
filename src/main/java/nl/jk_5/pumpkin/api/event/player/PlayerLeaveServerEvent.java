package nl.jk_5.pumpkin.api.event.player;

import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;

public interface PlayerLeaveServerEvent extends PlayerEvent {

    @Nullable
    IChatComponent getLeaveMessage();

    void setLeaveMessage(@Nullable IChatComponent leaveMessage);
}
