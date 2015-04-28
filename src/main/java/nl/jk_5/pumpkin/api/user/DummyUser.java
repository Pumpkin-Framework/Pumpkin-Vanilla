package nl.jk_5.pumpkin.api.user;

import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.UUID;
import javax.annotation.Nullable;

@NonnullByDefault
public final class DummyUser implements User {

    public static final DummyUser INSTANCE = new DummyUser();

    private DummyUser() {
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public String getUsername() {
        return "dummy";
    }

    @Override
    public String getPasswordHash() {
        return "";
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Nullable
    @Override
    public UUID getOnlineMojangId() {
        return null;
    }

    @Nullable
    @Override
    public UUID getOfflineMojangId() {
        return null;
    }

    @Override
    public void setOnlineMojangId(@Nullable UUID uuid) {

    }

    @Override
    public void setOfflineMojangId(@Nullable UUID uuid) {

    }
}
