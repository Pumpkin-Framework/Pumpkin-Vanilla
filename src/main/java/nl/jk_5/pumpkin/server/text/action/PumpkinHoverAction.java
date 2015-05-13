package nl.jk_5.pumpkin.server.text.action;

import net.minecraft.entity.EntityList;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.action.HoverAction;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;

import java.util.Locale;

public class PumpkinHoverAction {

    private PumpkinHoverAction() {
    }

    private static HoverEvent.Action getType(HoverAction<?> action) {
        if (action instanceof HoverAction.ShowAchievement) {
            return HoverEvent.Action.SHOW_ACHIEVEMENT;
        } else if (action instanceof HoverAction.ShowEntity) {
            return HoverEvent.Action.SHOW_ENTITY;
        } else if (action instanceof HoverAction.ShowItem) {
            return HoverEvent.Action.SHOW_ITEM;
        } else if (action instanceof HoverAction.ShowText) {
            return HoverEvent.Action.SHOW_TEXT;
        }

        throw new UnsupportedOperationException(action.getClass().toString());
    }

    public static HoverEvent getHandle(HoverAction<?> action, Locale locale) {
        HoverEvent.Action type = getType(action);
        IChatComponent component;

        switch (type) {
            case SHOW_ACHIEVEMENT:
                component = new ChatComponentText(((StatBase) action.getResult()).statId);
                break;
            case SHOW_ENTITY: {
                HoverAction.ShowEntity.Ref entity = ((HoverAction.ShowEntity) action).getResult();

                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString("id", entity.getUniqueId().toString());

                if (entity.getType().isPresent()) {
                    nbt.setString("type", ((String) EntityList.classToStringMapping.get(entity.getType().get())));
                }

                nbt.setString("name", entity.getName());
                component = new ChatComponentText(nbt.toString());
                break;
            }
            case SHOW_ITEM: {
                net.minecraft.item.ItemStack item = (net.minecraft.item.ItemStack) action.getResult();
                NBTTagCompound nbt = new NBTTagCompound();
                item.writeToNBT(nbt);
                component = new ChatComponentText(nbt.toString());
                break;
            }
            case SHOW_TEXT:
                component = PumpkinTexts.toComponent((Text) action.getResult());
                break;
            default:
                throw new AssertionError();
        }

        return new HoverEvent(type, component);
    }

}
