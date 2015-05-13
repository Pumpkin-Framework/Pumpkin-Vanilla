package nl.jk_5.pumpkin.server.mixin.core.text;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatList;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.action.HoverAction;
import nl.jk_5.pumpkin.api.text.action.TextActions;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinHoverEvent;

import java.util.UUID;

@Mixin(HoverEvent.class)
public abstract class MixinHoverEvent implements IMixinHoverEvent {

    @Shadow private HoverEvent.Action action;
    @Shadow private IChatComponent value;

    private HoverAction<?> handle;
    private boolean initialized;

    @Override
    public HoverAction<?> getHandle() {
        if (!this.initialized) {
            try {
                // This is inefficient, but at least we only need to do it once
                switch (this.action) {
                    case SHOW_TEXT:
                        setHandle(TextActions.showText(((IMixinChatComponent) this.value).toText()));
                        break;
                    case SHOW_ACHIEVEMENT:
                        String stat = this.value.getUnformattedText();
                        setHandle(TextActions.showAchievement((Achievement) checkNotNull(StatList.getOneShotStat(stat), "Unknown statistic: %s", stat)));
                        break;
                    case SHOW_ITEM:
                        setHandle(TextActions.showItem(ItemStack.loadItemStackFromNBT(loadNbt())));
                        break;
                    case SHOW_ENTITY:
                        NBTTagCompound nbt = loadNbt();
                        String name = nbt.getString("name");
                        Class<? extends Entity> type = null;
                        if (nbt.hasKey("type", 8)) {
                            type = ((Class<? extends Entity>) EntityList.stringToClassMapping.get(name));
                        }

                        UUID uniqueId = UUID.fromString(nbt.getString("id"));
                        setHandle(TextActions.showEntity(uniqueId, name, type));
                        break;
                    default:
                }
            } finally {
                this.initialized = true;
            }
        }

        return this.handle;
    }

    private NBTTagCompound loadNbt() {
        try {
            return checkNotNull(JsonToNBT.getTagFromJson(this.value.getUnformattedText()), "NBT");
        } catch (NBTException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void setHandle(HoverAction<?> handle) {
        if (this.initialized) {
            return;
        }

        this.handle = checkNotNull(handle, "handle");
        this.initialized = true;
    }

}
