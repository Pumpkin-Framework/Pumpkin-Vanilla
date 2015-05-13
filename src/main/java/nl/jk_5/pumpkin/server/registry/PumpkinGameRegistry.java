package nl.jk_5.pumpkin.server.registry;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;

import nl.jk_5.pumpkin.api.gamemode.GameMode;
import nl.jk_5.pumpkin.api.gamemode.GameModes;
import nl.jk_5.pumpkin.api.scoreboard.Visibility;
import nl.jk_5.pumpkin.api.scoreboard.criteria.Criterion;
import nl.jk_5.pumpkin.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import nl.jk_5.pumpkin.api.text.chat.ChatType;
import nl.jk_5.pumpkin.api.text.chat.ChatTypes;
import nl.jk_5.pumpkin.api.text.format.TextColor;
import nl.jk_5.pumpkin.api.text.format.TextColors;
import nl.jk_5.pumpkin.api.text.format.TextStyle;
import nl.jk_5.pumpkin.api.text.format.TextStyles;
import nl.jk_5.pumpkin.api.text.selector.SelectorType;
import nl.jk_5.pumpkin.api.text.translation.Translation;
import nl.jk_5.pumpkin.api.util.CatalogType;
import nl.jk_5.pumpkin.api.util.Direction;
import nl.jk_5.pumpkin.api.world.DimensionType;
import nl.jk_5.pumpkin.api.world.DimensionTypes;
import nl.jk_5.pumpkin.api.world.difficulty.Difficulties;
import nl.jk_5.pumpkin.api.world.difficulty.Difficulty;
import nl.jk_5.pumpkin.api.world.weather.Weather;
import nl.jk_5.pumpkin.server.text.chat.PumpkinChatType;
import nl.jk_5.pumpkin.server.text.format.PumpkinTextColor;
import nl.jk_5.pumpkin.server.text.format.PumpkinTextStyle;
import nl.jk_5.pumpkin.server.text.translation.PumpkinTranslation;
import nl.jk_5.pumpkin.server.util.PumpkinGameMode;
import nl.jk_5.pumpkin.server.world.PumpkinDimensionType;

import java.awt.*;
import java.util.Map;

public class PumpkinGameRegistry {

    public static final Map<String, TextColor> textColorMappings = Maps.newHashMap();
    public static final Map<EnumChatFormatting, PumpkinTextColor> enumChatColor = Maps.newEnumMap(EnumChatFormatting.class);

    public static final ImmutableMap<String, TextStyle> textStyleMappings = new ImmutableMap.Builder<String, TextStyle>()
            .put("BOLD", PumpkinTextStyle.of(EnumChatFormatting.BOLD))
            .put("ITALIC", PumpkinTextStyle.of(EnumChatFormatting.ITALIC))
            .put("UNDERLINE", PumpkinTextStyle.of(EnumChatFormatting.UNDERLINE))
            .put("STRIKETHROUGH", PumpkinTextStyle.of(EnumChatFormatting.STRIKETHROUGH))
            .put("OBFUSCATED", PumpkinTextStyle.of(EnumChatFormatting.OBFUSCATED))
            .put("RESET", PumpkinTextStyle.of(EnumChatFormatting.RESET))
            .build();
    private static final ImmutableMap<String, ChatType> chatTypeMappings = new ImmutableMap.Builder<String, ChatType>()
            .put("CHAT", new PumpkinChatType((byte) 0))
            .put("SYSTEM", new PumpkinChatType((byte) 1))
            .put("ACTION_BAR", new PumpkinChatType((byte) 2))
            .build();

    public static final ImmutableBiMap<Direction, EnumFacing> directionMap = ImmutableBiMap.<Direction, EnumFacing>builder()
            .put(Direction.NORTH, EnumFacing.NORTH)
            .put(Direction.EAST, EnumFacing.EAST)
            .put(Direction.SOUTH, EnumFacing.SOUTH)
            .put(Direction.WEST, EnumFacing.WEST)
            .put(Direction.UP, EnumFacing.UP)
            .put(Direction.DOWN, EnumFacing.DOWN)
            .build();
    public static final ImmutableMap<String, GameMode> gameModeMappings = new ImmutableMap.Builder<String, GameMode>()
            .put("SURVIVAL", new PumpkinGameMode("SURVIVAL"))
            .put("CREATIVE", new PumpkinGameMode("CREATIVE"))
            .put("ADVENTURE", new PumpkinGameMode("ADVENTURE"))
            .put("SPECTATOR", new PumpkinGameMode("SPECTATOR"))
            .put("NOT_SET", new PumpkinGameMode("NOT_SET"))
            .build();
    private static final ImmutableMap<String, Difficulty> difficultyMappings = new ImmutableMap.Builder<String, Difficulty>()
            .put("PEACEFUL", (Difficulty) (Object) EnumDifficulty.PEACEFUL)
            .put("EASY", (Difficulty) (Object) EnumDifficulty.EASY)
            .put("NORMAL", (Difficulty) (Object) EnumDifficulty.NORMAL)
            .put("HARD", (Difficulty) (Object) EnumDifficulty.HARD)
            .build();

    private final Map<String, DimensionType> dimensionTypeMappings = Maps.newHashMap();

    protected Map<Class<? extends CatalogType>, Map<String, ? extends CatalogType>> catalogTypeMap =
            ImmutableMap.<Class<? extends CatalogType>, Map<String, ? extends CatalogType>>builder()
                    //.put(Achievement.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(Art.class, this.artMappings)
                    //.put(Attribute.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(BiomeType.class, this.biomeTypeMappings)
                    //.put(BlockType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(Career.class, this.careerMappings)
                    //.put(ChatType.class, chatTypeMappings)
                    //.put(BannerPatternShape.class, this.bannerPatternShapeMappings)
                    //.put(CookedFish.class, this.cookedFishMappings)
                    //.put(DyeColor.class, this.dyeColorMappings)
                    //.put(EntityInteractionType.class, entityInteractionTypeMappings)
                    //.put(CoalType.class, this.coaltypeMappings)
                    //.put(NotePitch.class, this.notePitchMappings)
                    //.put(Comparison.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    .put(Criterion.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    .put(Difficulty.class, difficultyMappings)
                    .put(DimensionType.class, this.dimensionTypeMappings)
                    //.put(DirtType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(DisguisedBlockType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(Enchantment.class, this.enchantmentMappings)
                    //.put(EquipmentType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(FireworkShape.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(Fish.class, this.fishMappings)
                    .put(GameMode.class, gameModeMappings)
                    //.put(GoldenApple.class, this.goldenAppleMappings)
                    //.put(EntityType.class, this.entityTypeMappings)
                    //.put(Hinge.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(HorseColor.class, SpongeEntityConstants.HORSE_COLORS)
                    //.put(HorseStyle.class, SpongeEntityConstants.HORSE_STYLES)
                    //.put(HorseVariant.class, SpongeEntityConstants.HORSE_VARIANTS)
                    //.put(ItemType.class, ImmutableMap.<String, CatalogType>of()) // TODO handle special case of items
                    .put(ObjectiveDisplayMode.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(OcelotType.class, SpongeEntityConstants.OCELOT_TYPES)
                    //.put(Operation.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(ParticleType.class, this.particleByName)
                    //.put(PlantType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(PotionEffectType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(PortionType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(PrismarineType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(Profession.class, this.professionMappings)
                    //.put(QuartzType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(RabbitType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(RailDirection.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(Rotation.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(SandstoneType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    .put(SelectorType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(SkeletonType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(SkullType.class, this.skullTypeMappings)
                    //.put(SlabType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(SoundType.class, this.soundNames)
                    //.put(StairShape.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(Statistic.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(StatisticFormat.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(StatisticGroup.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(StoneType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    .put(TextColor.class, textColorMappings)
                    //.put(TileEntityType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(TreeType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    .put(Visibility.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(WallType.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    .put(Weather.class, ImmutableMap.<String, CatalogType>of()) // TODO
                    //.put(WorldGeneratorModifier.class, this.worldGeneratorRegistry.viewModifiersMap())
                    .build();

    private static void addTextColor(EnumChatFormatting handle, Color color) {
        PumpkinTextColor pumpkinColor = new PumpkinTextColor(handle, color);
        textColorMappings.put(handle.name(), pumpkinColor);
        enumChatColor.put(handle, pumpkinColor);
    }

    private void setTextColors() {
        addTextColor(EnumChatFormatting.BLACK, Color.BLACK);
        addTextColor(EnumChatFormatting.DARK_BLUE, new Color(0x0000AA));
        addTextColor(EnumChatFormatting.DARK_GREEN, new Color(0x00AA00));
        addTextColor(EnumChatFormatting.DARK_AQUA, new Color(0x00AAAA));
        addTextColor(EnumChatFormatting.DARK_RED, new Color(0xAA0000));
        addTextColor(EnumChatFormatting.DARK_PURPLE, new Color(0xAA00AA));
        addTextColor(EnumChatFormatting.GOLD, new Color(0xFFAA00));
        addTextColor(EnumChatFormatting.GRAY, new Color(0xAAAAAA));
        addTextColor(EnumChatFormatting.DARK_GRAY, new Color(0x555555));
        addTextColor(EnumChatFormatting.BLUE, new Color(0x5555FF));
        addTextColor(EnumChatFormatting.GREEN, new Color(0x55FF55));
        addTextColor(EnumChatFormatting.AQUA, new Color(0x00FFFF));
        addTextColor(EnumChatFormatting.RED, new Color(0xFF5555));
        addTextColor(EnumChatFormatting.LIGHT_PURPLE, new Color(0xFF55FF));
        addTextColor(EnumChatFormatting.YELLOW, new Color(0xFFFF55));
        addTextColor(EnumChatFormatting.WHITE, Color.WHITE);
        addTextColor(EnumChatFormatting.RESET, Color.WHITE);

        RegistryHelper.mapFields(TextColors.class, textColorMappings);
        RegistryHelper.mapFields(ChatTypes.class, chatTypeMappings);
        RegistryHelper.mapFields(TextStyles.class, textStyleMappings);
    }

    private void setDimensionTypes() {
        try {
            DimensionTypes.class.getDeclaredField("NETHER").set(null, new PumpkinDimensionType("NETHER", -1));
            DimensionTypes.class.getDeclaredField("OVERWORLD").set(null, new PumpkinDimensionType("OVERWORLD", 0));
            DimensionTypes.class.getDeclaredField("END").set(null, new PumpkinDimensionType("END", 1));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setGameModes() {
        RegistryHelper.mapFields(GameModes.class, gameModeMappings);
    }

    private void setDifficulties() {
        RegistryHelper.mapFields(Difficulties.class, difficultyMappings);
    }

    public com.google.common.base.Optional<Translation> getTranslationById(String id) {
        return com.google.common.base.Optional.<Translation>of(new PumpkinTranslation(id));
    }

    public WorldSettings.GameType getGameType(GameMode mode) {
        return WorldSettings.GameType.getByName(mode.getTranslation().getId());
    }

    public <T extends CatalogType> com.google.common.base.Optional<T> getType(Class<T> typeClass, String id) {
        Map<String, ? extends CatalogType> tempMap = this.catalogTypeMap.get(checkNotNull(typeClass, "null type class"));
        if (tempMap == null) {
            return com.google.common.base.Optional.absent();
        } else {
            T type = (T) tempMap.get(id);
            if (type == null) {
                return com.google.common.base.Optional.absent();
            } else {
                return com.google.common.base.Optional.of(type);
            }
        }
    }

    public void preInit() {

    }

    public void init() {
        setDimensionTypes();
        setTextColors();
        setDifficulties();
        setGameModes();
    }
}
