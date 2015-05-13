package nl.jk_5.pumpkin.launch;

import net.minecraft.launchwrapper.Launch;

import nl.jk_5.pumpkin.launch.console.VanillaConsole;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public final class Main {

    private static final String MINECRAFT_SERVER_LOCAL = "minecraft_server.1.8.jar";
    private static final String MINECRAFT_SERVER_REMOTE = "https://s3.amazonaws.com/Minecraft.Download/versions/1.8/minecraft_server.1.8.jar";
    private static final String LAUNCHWRAPPER_LOCAL = "launchwrapper-1.11.jar";
    private static final String LAUNCHWRAPPER_REMOTE = "https://libraries.minecraft.net/net/minecraft/launchwrapper/1.11/launchwrapper-1.11.jar";
    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        if (!checkMinecraft()) {
            return;
        }

        VanillaConsole.start();
        Launch.main(join(args,
                "--tweakClass", "nl.jk_5.pumpkin.launch.ServerTweaker"
        ));
    }

    private static String[] join(String[] args, String... prefix) {
        String[] result = new String[prefix.length + args.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(args, 0, result, prefix.length, args.length);
        return result;
    }

    private static boolean checkMinecraft() throws Exception {
        File lib = new File("lib");
        if(!lib.isDirectory() && !lib.mkdirs()){
            throw new IOException("Failed to create folder at " + lib);
        }

        File file = new File(MINECRAFT_SERVER_LOCAL);

        if(!file.isFile() && !downloadVerified(MINECRAFT_SERVER_REMOTE, file)){
            return false;
        }

        file = new File(lib, LAUNCHWRAPPER_LOCAL);
        return file.isFile() || downloadVerified(LAUNCHWRAPPER_REMOTE, file);
    }

    private static boolean downloadVerified(String remote, File file) throws Exception {
        String name = file.getName();
        URL url = new URL(remote);

        System.out.println("Downloading " + name + "... This can take a while.");
        System.out.println(url);
        URLConnection con = url.openConnection();
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        InputStream in = null;
        FileOutputStream out = null;
        ReadableByteChannel source = null;
        FileChannel target = null;
        try{
            in = con.getInputStream();
            out = new FileOutputStream(file);
            source = Channels.newChannel(new DigestInputStream(in, md5));
            target = out.getChannel();

            target.transferFrom(source, 0, Long.MAX_VALUE);
        }finally{
            close(in, out, source, target);
        }

        String expected = getETag(con);
        if(!expected.isEmpty()){
            String hash = toHexString(md5.digest());
            if(hash.equals(expected)){
                System.out.println("Successfully downloaded " + name + " and verified checksum!");
            }else{
                if(!file.delete()){
                    throw new IOException("Failed to delete " + file);
                }

                System.err.println("Failed to download " + name + " (failed checksum verification).");
                System.err.println("Please try again later.");
                return false;
            }
        }

        return true;
    }

    private static String getETag(URLConnection con) {
        String hash = con.getHeaderField("ETag");
        if(hash == null || hash.isEmpty()){
            return "";
        }

        if(hash.startsWith("\"") && hash.endsWith("\"")){
            hash = hash.substring(1, hash.length() - 1);
        }

        return hash;
    }

    private static void close(Closeable... closeables) {
        for(Closeable closeable : closeables){
            if (closeable != null) {
                try{
                    closeable.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
