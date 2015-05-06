package nl.jk_5.pumpkin.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import nl.jk_5.pumpkin.launch.console.VanillaConsole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ServerTweaker implements ITweaker {

    private static final Logger logger = LogManager.getLogger();

    public static boolean isObfuscated = false;

    private static boolean isObfuscated() {
        try {
            return Launch.classLoader.getClassBytes("net.minecraft.world.World") == null;
        } catch (IOException ignored) {
            return true;
        }
    }

    @Override
    public void acceptOptions(List<String> list, File file, File file1, String s) {
        VanillaConsole.start();
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader loader) {
        logger.info("Initializing Pumpkin...");

        loader.addClassLoaderExclusion("io.netty.");
        loader.addClassLoaderExclusion("gnu.trove.");
        loader.addClassLoaderExclusion("joptsimple.");
        loader.addClassLoaderExclusion("com.mojang.util.QueueLogAppender");
        loader.addClassLoaderExclusion("org.spongepowered.tools.");
        loader.addClassLoaderExclusion("nl.jk_5.pumpkin.server.mixin.");
        loader.addClassLoaderExclusion("nl.jk_5.pumpkin.launch.");
        loader.addClassLoaderExclusion("jline.");
        loader.addClassLoaderExclusion("org.fusesource.");
        loader.addClassLoaderExclusion("nl.jk_5.pumpkin.launch.console.");

        logger.info("Applying runtime deobfuscation...");
        isObfuscated = isObfuscated();
        if (isObfuscated) {
            Launch.blackboard.put("pumpkin.deobf-srg", Paths.get("bin", "deobf.srg.gz"));
            loader.registerTransformer("nl.jk_5.pumpkin.launch.transformer.DeobfuscationTransformer");
            logger.info("Runtime deobfuscation is applied.");
        } else {
            logger.info("Runtime deobfuscation was not applied. Pumpkin is being loaded in a deobfuscated environment.");
        }

        logger.info("Applying access transformer...");
        Launch.blackboard.put("pumpkin.at", "pumpkin_at.cfg");
        loader.registerTransformer("nl.jk_5.pumpkin.launch.transformer.AccessTransformer");

        logger.info("Initializing Mixin environment...");
        MixinBootstrap.init();
        MixinEnvironment env = MixinEnvironment.getDefaultEnvironment();
        env.setSide(MixinEnvironment.Side.SERVER);
        env.addConfiguration("mixins.pumpkin.core.json");

        logger.info("Initialization finished. Starting Minecraft server...");
    }

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.server.MinecraftServer";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
