package nl.jk_5.pumpkin.server.text.chat;

import nl.jk_5.pumpkin.api.text.chat.ChatType;

public class PumpkinChatType implements ChatType {

    private final byte id;

    public PumpkinChatType(byte id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return "minecraft:" + this.id;
    }

    @Override
    public String getName() {
        return getId(); // todo actually pick up a name
    }

    public byte getByteId() {
        return this.id;
    }
}
