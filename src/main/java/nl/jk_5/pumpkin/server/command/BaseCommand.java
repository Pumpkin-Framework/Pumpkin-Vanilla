package nl.jk_5.pumpkin.server.command;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import nl.jk_5.pumpkin.api.command.CommandSender;
import nl.jk_5.pumpkin.api.command.exception.*;
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
abstract class BaseCommand implements ICommand, PermissionCommand {

    private final String name;
    private final String[] aliases;

    public BaseCommand(String name, String ... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void execute(CommandSender sender, String[] args) throws CommandException;

    protected static MapWorld requireWorld(CommandSender sender) throws CommandException {
        if(sender instanceof MinecraftServer || sender instanceof RConConsoleSource){
            throw new CommandException("You are not in a world");
        }
        World world = ((ICommandSender) sender).getEntityWorld();
        return Pumpkin.instance().getDimensionManager().getWorld(world.provider.getDimensionId());
    }

    protected static Map requireMap(CommandSender sender) throws CommandException {
        MapWorld world = requireWorld(sender);
        Map map = world.getMap();
        if(map == null){
            throw new CommandException("You are not in a map");
        }
        return map;
    }

    protected static Player requirePlayer(CommandSender sender) throws CommandException {
        if(sender instanceof Player){
            return (Player) sender;
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

    protected static Player selectPlayer(CommandSender sender, String query) throws CommandException {
        EntityPlayerMP player = PlayerSelector.matchOnePlayer((ICommandSender) sender, query);
        if(player == null){
            player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(query);
        }
        if(player == null){
            throw new PlayerNotFoundException();
        }
        return Pumpkin.instance().getPlayerManager().getFromEntity(player);
    }

    protected static List<Player> selectPlayers(CommandSender sender, String query) throws CommandException {
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

    protected static <T extends Entity> List<T> select(CommandSender sender, String query, Class<? extends T> target) throws CommandException {
        //TODO: check if sender has permission to use selectors
        @SuppressWarnings("unchecked")
        List<T> out = PlayerSelector.matchEntities((ICommandSender) sender, query, target);
        if(out == null || out.isEmpty()){
            return Collections.emptyList();
        }
        return out;
    }

    protected static double handleRelativeNumber(CommandSender sender, double origin, String arg, int min, int max) throws CommandException {
        boolean isRelative = arg.startsWith("~");
        double value = isRelative ? origin : 0D;
        if(!isRelative || arg.length() > 1){
            boolean isDouble = arg.contains(".");
            if(isRelative){
                arg = arg.substring(1);
            }
            value += parseDouble(arg);
            if(!isDouble && !isRelative){
                value += 0.5D;
            }
        }
        if(min != 0 || max != 0){
            if(value < min){
                throw new InvalidNumberException("commands.generic.double.tooSmall", value, min);
            }
            if(value > max){
                throw new InvalidNumberException("commands.generic.double.tooBig", value, min);
            }
        }
        return value;
    }

    protected static double handleRelativeNumber(CommandSender sender, double origin, String arg) throws CommandException {
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

    protected static int parseInt(String input) throws InvalidNumberException {
        try {
            return Integer.parseInt(input);
        }catch(NumberFormatException e) {
            throw new InvalidNumberException("commands.generic.num.invalid", input);
        }
    }

    protected static int parseInt(String input, int min) throws InvalidNumberException {
        return parseInt(input, min, Integer.MAX_VALUE);
    }

    protected static int parseInt(String input, int min, int max) throws InvalidNumberException {
        int number = parseInt(input);

        if(number < min){
            throw new InvalidNumberException("commands.generic.num.tooSmall", number, min);
        }else if(number > max){
            throw new InvalidNumberException("commands.generic.num.tooBig", number, max);
        }else{
            return number;
        }
    }

    protected static long parseLong(String input) throws InvalidNumberException {
        try {
            return Long.parseLong(input);
        }catch(NumberFormatException numberformatexception) {
            throw new InvalidNumberException("commands.generic.num.invalid", input);
        }
    }

    protected static long parseLong(String input, long min, long max) throws InvalidNumberException {
        long number = parseLong(input);

        if(number < min){
            throw new InvalidNumberException("commands.generic.num.tooSmall", number, min);
        }else if (number > max){
            throw new InvalidNumberException("commands.generic.num.tooBig", number, max);
        }else{
            return number;
        }
    }

    protected static double parseDouble(String input) throws InvalidNumberException {
        try {
            double number = Double.parseDouble(input);

            if(!Doubles.isFinite(number)) {
                throw new InvalidNumberException("commands.generic.num.invalid", input);
            }else{
                return number;
            }
        }catch(NumberFormatException numberformatexception){
            throw new InvalidNumberException("commands.generic.num.invalid", input);
        }
    }

    protected static double parseDouble(String input, double min) throws InvalidNumberException {
        return parseDouble(input, min, Double.MAX_VALUE);
    }

    protected static double parseDouble(String input, double min, double max) throws InvalidNumberException {
        double number = parseDouble(input);

        if(number < min){
            throw new InvalidNumberException("commands.generic.double.tooSmall", number, min);
        }else if (number > max){
            throw new InvalidNumberException("commands.generic.double.tooBig", number, max);
        }else{
            return number;
        }
    }

    protected static boolean parseBoolean(String input) throws CommandException {
        if(input.equals("true") || input.equals("1")){
            return true;
        }else if(input.equals("false") || input.equals("0")){
            return false;
        }else{
            throw new CommandException("commands.generic.boolean.invalid", input);
        }
    }

    @Nullable
    protected List<String> addAutocomplete(CommandSender sender, String[] args){
        return null;
    }

    @Override
    public final String getCommandName() {
        return this.name;
    }

    @Override
    @Nullable
    public final String getCommandUsage(ICommandSender sender) {
        return "/" + this.name;
    }

    @Override
    public final List getCommandAliases() {
        return Arrays.asList(this.aliases);
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] args) throws net.minecraft.command.CommandException {
        try {
            this.execute((CommandSender) sender, args);
        }catch(EntityNotFoundException e){
            throw new net.minecraft.command.EntityNotFoundException(e.getMessage(), e.getFormatArgs());
        }catch(InvalidNumberException e){
            throw new net.minecraft.command.NumberInvalidException(e.getMessage(), e.getFormatArgs());
        }catch(InvalidSyntaxException e){
            throw new net.minecraft.command.SyntaxErrorException(e.getMessage(), e.getFormatArgs());
        }catch(InvalidUsageException e){
            throw new net.minecraft.command.WrongUsageException(e.getMessage(), e.getFormatArgs());
        }catch(PlayerNotFoundException e){
            throw new net.minecraft.command.PlayerNotFoundException(e.getMessage(), e.getFormatArgs());
        }catch(CommandException e){
            throw new net.minecraft.command.CommandException(e.getMessage(), e.getFormatArgs());
        }
    }

    @Override
    public final boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public final List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> completion = this.addAutocomplete((CommandSender) sender, args);
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

    @Override
    public final int compareTo(@Nonnull Object o) {
        return this.compareTo((ICommand) o);
    }

    public final int compareTo(ICommand command) {
        return this.getCommandName().compareTo(command.getCommandName());
    }
}
