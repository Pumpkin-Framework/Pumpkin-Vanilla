package nl.jk_5.pumpkin.api.effect;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.text.chat.ChatType;
import nl.jk_5.pumpkin.api.text.title.Title;

/**
 * A Viewer is something that sees effects.
 * The Viewer class contains methods for spawning particles and playing sound effects.
 */
public interface Viewer {

    /**
     * Spawn a {@link ParticleEffect} at a given position.
     * All players within a default radius around the position will see the
     * particles.
     *
     * @param particleEffect The particle effect to spawn
     * @param position The position at which to spawn the particle effect
     */
    //void spawnParticles(ParticleEffect particleEffect, Vector3d position);

    /**
     * Spawn a {@link ParticleEffect} at a given position.
     * All players within a given radius around the position will see the
     * particles.
     *
     * @param particleEffect The particle effect to spawn
     * @param position The position at which to spawn the particle effect
     * @param radius The radius around the position where the particles can be
     *            seen by players
     */
    //void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius);

    /**
     * Plays the given {@link SoundType} at the given position. All
     * players within range will hear the sound with the given volume.
     *
     * @param sound The sound to play
     * @param position The position to play the sound
     * @param volume The volume to play the sound at, usually between 0 and 2
     */
    //void playSound(SoundType sound, Vector3d position, double volume);

    /**
     * Plays the given {@link SoundType} at the given position. All
     * players within range will hear the sound with the given volume.
     *
     * @param sound The sound to play
     * @param position The position to play the sound
     * @param volume The volume to play the sound at, usually between 0 and 2
     * @param pitch The modulation of the sound to play at, usually between 0 and 2
     */
    //void playSound(SoundType sound, Vector3d position, double volume, double pitch);

    /**
     * Plays the given {@link SoundType} at the given position. All
     * players within range will hear the sound with the given volume.
     *
     * @param sound The sound to play
     * @param position The position to play the sound
     * @param volume The volume to play the sound at, usually between 0 and 2
     * @param pitch The modulation of the sound to play at, usually between 0 and 2
     * @param minVolume The minimum volume to play the sound at, usually between 0 and 2
     */
    //void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume);

    /**
     * Sends the plain text message(s) with the specified {@link ChatType} on
     * the client.
     * <p>
     * Use {@link #sendMessage(ChatType, Text...)} for a formatted message.
     * </p>
     *
     * @param type The chat type to send the messages to
     * @param message The message(s) to send
     */
    void sendMessage(ChatType type, String... message);

    /**
     * Sends the message(s) with the specified {@link ChatType} on the client.
     *
     * @param type The chat type to send the messages to
     * @param messages The message(s) to send
     */
    void sendMessage(ChatType type, Text... messages);

    /**
     * Sends the message(s) with the specified {@link ChatType} on the client.
     *
     * @param type The chat type to send the messages to
     * @param messages The message(s) to send
     */
    void sendMessage(ChatType type, Iterable<Text> messages);

    /**
     * Sends a {@link Title} to this player.
     *
     * @param title The {@link Title} to send to the player
     */
    void sendTitle(Title title);

    /**
     * Removes the currently displayed {@link Title} from the player and resets
     * all settings back to default values.
     */
    void resetTitle();

    /**
     * Removes the currently displayed {@link Title} from the player's screen.
     */
    void clearTitle();

}