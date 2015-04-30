package nl.jk_5.pumpkin.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.mappack.Map;
import nl.jk_5.pumpkin.server.mappack.MapWorld;
import nl.jk_5.pumpkin.server.permissions.PermissionCommand;
import nl.jk_5.pumpkin.server.player.Player;
import nl.jk_5.pumpkin.server.util.annotation.NonnullByDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
@NonnullByDefault
abstract class BaseCommand extends ComparedCommand implements PermissionCommand {

    private final String name;
    private final String[] aliases;

    public BaseCommand(String name, String ... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void execute(ICommandSender sender, String[] args) throws CommandException;

    protected static MapWorld requireWorld(ICommandSender sender) throws CommandException {
        if(sender instanceof MinecraftServer || sender instanceof RConConsoleSource){
            throw new CommandException("You are not in a world");
        }
        World world = sender.getEntityWorld();
        return Pumpkin.instance().getDimensionManager().getWorld(world.provider.getDimensionId());
    }

    protected static Map requireMap(ICommandSender sender) throws CommandException {
        MapWorld world = requireWorld(sender);
        Map map = world.getMap();
        if(map == null){
            throw new CommandException("You are not in a map");
        }
        return map;
    }

    protected static Player requirePlayer(ICommandSender sender) throws CommandException {
        if(sender instanceof EntityPlayerMP){
            Player player = Pumpkin.instance().getPlayerManager().getFromEntity((EntityPlayerMP) sender);
            if(player == null){
                throw new CommandException("You are not a player");
            }
            return player;
        }else{
            throw new CommandException("You are not a player");
        }
    }

    protected static List<String> getOptions(String[] args, String ... options){
        String last = args[args.length - 1];
        List<String> ret = new ArrayList<String>();
        for(String option : options){
            if(option.regionMatches(true, 0, last, 0, last.length())){
                ret.add(option);
            }
        }
        return ret;
    }

    protected static List<String> getOptions(String[] args, Iterable<String> options){
        String last = args[args.length - 1];
        List<String> ret = new ArrayList<String>();
        for(String option : options){
            if(option.regionMatches(true, 0, last, 0, last.length())){
                ret.add(option);
            }
        }
        return ret;
    }

    protected static List<String> getUsernameOptions(String[] args){
        return getOptions(args, MinecraftServer.getServer().getAllUsernames());
    }

    protected static List<String> getUsernameOptions(String[] args, Map map){
        throw new UnsupportedOperationException("Not yet implemented");
        //return getOptions(args, );
    }

    protected static List<String> getUsernameOptions(String[] args, MapWorld world){
        throw new UnsupportedOperationException("Not yet implemented");
        //return getOptions(args, );
    }

    protected static Player selectPlayer(ICommandSender sender, String query) throws CommandException {
        EntityPlayerMP player = PlayerSelector.matchOnePlayer(sender, query);
        if(player == null){
            player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(query);
        }
        if(player == null){
            throw new PlayerNotFoundException();
        }
        return Pumpkin.instance().getPlayerManager().getFromEntity(player);
    }

    protected static List<Player> selectPlayers(ICommandSender sender, String query) throws CommandException {
        List<EntityPlayerMP> players = select(sender, query, EntityPlayerMP.class);
        if(players.isEmpty()){
            EntityPlayerMP p = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(query);
            if(p == null){
                throw new PlayerNotFoundException();
            }
            Player player = Pumpkin.instance().getPlayerManager().getFromEntity(p);
            return ImmutableList.of(player);
        }else{
            ImmutableList.Builder<Player> builder = ImmutableList.builder();
            for (EntityPlayerMP player : players) {
                builder.add(Pumpkin.instance().getPlayerManager().getFromEntity(player));
            }
            return builder.build();
        }
    }

    protected static <T extends Entity> List<T> select(ICommandSender sender, String query, Class<? extends T> target) throws CommandException {
        //TODO: check if sender has permission to use selectors
        @SuppressWarnings("unchecked")
        List<T> out = PlayerSelector.matchEntities(sender, query, target);
        if(out == null || out.isEmpty()){
            return Collections.emptyList();
        }
        return out;
    }

    protected static double handleRelativeNumber(ICommandSender sender, double origin, String arg, int min, int max) throws CommandException {
        boolean isRelative = arg.startsWith("~");
        double value = isRelative ? origin : 0D;
        if(!isRelative || arg.length() > 1){
            boolean isDouble = arg.contains(".");
            if(isRelative){
                arg = arg.substring(1);
            }
            value += CommandBase.parseDouble(arg);
            if(!isDouble && !isRelative){
                value += 0.5D;
            }
        }
        if(min != 0 || max != 0){
            if(value < min){
                throw new NumberInvalidException("commands.generic.double.tooSmall", value, min);
            }
            if(value > max){
                throw new NumberInvalidException("commands.generic.double.tooBig", value, min);
            }
        }
        return value;
    }

    protected static double handleRelativeNumber(ICommandSender sender, double origin, String arg) throws CommandException {
        return handleRelativeNumber(sender, origin, arg, -30000000, 30000000);
    }

    protected static <T> T getFutureResult(ListenableFuture<T> future) {
        try{
            return future.get();
        }catch(Exception e){
            //noinspection ConstantConditions
            return null;
        }
    }

    @Nullable
    protected List<String> addAutocomplete(ICommandSender sender, String[] args){
        return null;
    }

    @Override
    public final String getCommandName() {
        return this.name;
    }

    @Override
    @Nullable
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.name;
    }

    @Override
    public final List getCommandAliases() {
        return Arrays.asList(this.aliases);
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] args) throws CommandException {
        this.execute(sender, args);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public final List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> completion = this.addAutocomplete(sender, args);
        if(completion == null){
            return Collections.emptyList();
        }
        return completion;
    }

    @Override
    public final boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Nonnull
    @Override
    public String getPermission() {
        return "pumpkin.command." + this.name;
    }

    @Override
    public boolean defaultPermission() {
        return false;
    }

    public final String getName() {
        return name;
    }
}
