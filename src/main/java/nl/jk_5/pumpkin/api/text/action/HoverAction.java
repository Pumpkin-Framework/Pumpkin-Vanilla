package nl.jk_5.pumpkin.api.text.action;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import nl.jk_5.pumpkin.api.text.Text;
import nl.jk_5.pumpkin.api.util.Identifiable;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a {@link TextAction} that responds to hovers.
 *
 * @param <R> The type of the result of the action
 */
public abstract class HoverAction<R> extends TextAction<R> {

    /**
     * Constructs a new {@link HoverAction} with the given result.
     *
     * @param result The result of the hover action
     */
    protected HoverAction(R result) {
        super(result);
    }

    /**
     * Shows some text.
     */
    public static final class ShowText extends HoverAction<Text> {

        /**
         * Constructs a new {@link ShowText} instance that will show text when
         * it is hovered.
         *
         * @param text The message to show
         */
        public ShowText(Text text) {
            super(text);
        }
    }

    /**
     * Shows information about an item.
     */
    public static final class ShowItem extends HoverAction<ItemStack> {

        /**
         * Constructs a new {@link ShowItem} instance that will show information
         * about an item when it is hovered.
         *
         * @param item The item to display
         */
        public ShowItem(ItemStack item) {
            super(item);
        }

    }

    /**
     * Shows information about an achievement.
     */
    public static final class ShowAchievement extends HoverAction<Achievement> {

        /**
         * Constructs a new {@link ShowAchievement} instance that will show
         * information about an achievement when it is hovered.
         *
         * @param achievement The achievement to display
         */
        public ShowAchievement(Achievement achievement) {
            super(achievement);
        }

    }

    /**
     * Shows information about an entity.
     */
    public static final class ShowEntity extends HoverAction<ShowEntity.Ref> {

        /**
         * Constructs a new {@link ShowEntity} that will show information about
         * an entity when it is hovered.
         *
         * @param ref The reference to the entity to display
         */
        public ShowEntity(Ref ref) {
            super(ref);
        }

        /**
         * Represents a reference to an entity, used in the underlying JSON of the
         * show entity action.
         */
        public static final class Ref implements Identifiable {

            private final UUID uuid;
            private final String name;
            private final Optional<Class<? extends Entity>> type;

            /**
             * Constructs a Ref to an entity.
             *
             * @param uuid The UUID of the entity
             * @param name The name of the entity
             * @param type The type of the entity
             */
            public Ref(UUID uuid, String name, @Nullable Class<? extends Entity> type) {
                this(uuid, name, Optional.<Class<? extends Entity>>fromNullable(type));
            }

            /**
             * Constructs a Ref to an entity.
             *
             * @param uuid The UUID of the entity
             * @param name The name of the entity
             */
            public Ref(UUID uuid, String name) {
                this(uuid, name, Optional.<Class<? extends Entity>>absent());
            }

            /**
             * Constructs a Ref, given an {@link Entity}.
             *
             * @param entity The entity
             * @param name The name of the entity
             */
            public Ref(Entity entity, String name) {
                this(entity.getUniqueID(), name, entity.getClass());
            }

            /**
             * Constructs a Ref directly.
             *
             * @param uuid The UUID
             * @param name The name
             * @param type The type
             */
            protected Ref(UUID uuid, String name, Optional<Class<? extends Entity>> type) {
                this.uuid = uuid;
                this.name = name;
                this.type = type;
            }

            /**
             * Retrieves the UUID that this {@link Ref} refers to.
             *
             * @return The UUID
             */
            @Override
            public UUID getUniqueId() {
                return this.uuid;
            }

            /**
             * Retrieves the name that this {@link Ref} refers to.
             *
             * @return The name
             */
            public String getName() {
                return this.name;
            }

            /**
             * Retrieves the type that this {@link Ref} refers to, if it exists.
             *
             * @return The type, or {@link Optional#absent()}
             */
            public Optional<Class<? extends Entity>> getType() {
                return this.type;
            }

            @Override
            public boolean equals(Object obj) {
                if (super.equals(obj)) {
                    return true;
                }

                if (!(obj instanceof Ref)) {
                    return false;
                }

                Ref that = (Ref) obj;
                return  this.uuid.equals(that.uuid)
                        && this.name.equals(that.name)
                        && this.type.equals(that.type);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(this.uuid, this.name, this.type);
            }


            @Override
            public String toString() {
                return Objects.toStringHelper(this)
                        .add("uuid", this.uuid)
                        .add("name", this.name)
                        .add("type", this.type)
                        .toString();
            }

        }

    }

}
