package nl.jk_5.pumpkin.server.mixin.api.text;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.action.ClickAction;
import nl.jk_5.pumpkin.api.text.action.HoverAction;
import nl.jk_5.pumpkin.api.text.action.ShiftClickAction;
import nl.jk_5.pumpkin.api.text.format.TextColor;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.api.text.format.TextStyle;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinChatComponent;
import nl.jk_5.pumpkin.server.mixin.interfaces.text.IMixinText;
import nl.jk_5.pumpkin.server.text.PumpkinTexts;
import nl.jk_5.pumpkin.server.text.action.PumpkinClickAction;
import nl.jk_5.pumpkin.server.text.action.PumpkinHoverAction;
import nl.jk_5.pumpkin.server.text.format.PumpkinTextColor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Mixin(value = Text.class, remap = false)
public abstract class MixinText implements IMixinText {

    @Shadow protected TextColor color;
    @Shadow protected TextStyle style;
    @Shadow protected ImmutableList<Text> children;
    @Shadow protected Optional<ClickAction<?>> clickAction;
    @Shadow protected Optional<HoverAction<?>> hoverAction;
    @Shadow protected Optional<ShiftClickAction<?>> shiftClickAction;

    private Map<Locale, IChatComponent> localizedComponents;
    private String json;

    protected ChatComponentStyle createComponent(Locale locale) {
        throw new UnsupportedOperationException();
    }

    private IChatComponent initializeComponent(Locale locale) {
        if (this.localizedComponents == null) {
            this.localizedComponents = Collections.synchronizedMap(new HashMap<Locale, IChatComponent>());
        }
        IChatComponent component = this.localizedComponents.get(locale);
        if (component == null) {
            component = createComponent(locale);
            ChatStyle style = component.getChatStyle();

            if (this.color != TextColors.NONE) {
                style.setColor(((PumpkinTextColor) this.color).getHandle());
            }

            if (!this.style.isEmpty()) {
                style.setBold(this.style.isBold().orNull());
                style.setItalic(this.style.isItalic().orNull());
                style.setUnderlined(this.style.hasUnderline().orNull());
                style.setStrikethrough(this.style.hasStrikethrough().orNull());
                style.setObfuscated(this.style.isObfuscated().orNull());
            }

            if (this.clickAction.isPresent()) {
                style.setChatClickEvent(PumpkinClickAction.getHandle(this.clickAction.get()));
            }

            if (this.hoverAction.isPresent()) {
                style.setChatHoverEvent(PumpkinHoverAction.getHandle(this.hoverAction.get(), locale));
            }

            if (this.shiftClickAction.isPresent()) {
                ShiftClickAction.InsertText insertion = (ShiftClickAction.InsertText) this.shiftClickAction.get();
                style.setInsertion(insertion.getResult());
            }

            for (Text child : this.children) {
                component.appendSibling(((IMixinText) child).toComponent(locale));
            }
            this.localizedComponents.put(locale, component);
        }
        return component;
    }

    private IChatComponent getHandle(Locale locale) {
        return initializeComponent(locale);
    }

    @Override
    public IChatComponent toComponent(Locale locale) {
        return getHandle(locale).createCopy(); // Mutable instances are not nice :(
    }

    @Override
    public String toPlain(Locale locale) {
        return ((IMixinChatComponent) getHandle(locale)).toPlain();
    }

    @Override
    public String toJson(Locale locale) {
        if (this.json == null) {
            this.json = IChatComponent.Serializer.componentToJson(getHandle(locale));
        }

        return this.json;
    }

    @Override
    public String getLegacyFormatting() {
        return ((IMixinChatComponent) getHandle(PumpkinTexts.getDefaultLocale())).getLegacyFormatting();
    }

    @Override
    public String toLegacy(char code, Locale locale) {
        return ((IMixinChatComponent) getHandle(locale)).toLegacy(code);
    }

}
