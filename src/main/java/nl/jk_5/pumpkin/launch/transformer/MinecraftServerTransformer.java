package nl.jk_5.pumpkin.launch.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import nl.jk_5.pumpkin.launch.ServerTweaker;
import nl.jk_5.pumpkin.launch.transformer.util.ASMHelper;
import nl.jk_5.pumpkin.launch.transformer.util.Mapping;

public class MinecraftServerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(transformedName.equals("net.minecraft.server.MinecraftServer")){
            if(ServerTweaker.isObfuscated){
                return transformMinecraftServer(bytes, "h");
            }else{
                return transformMinecraftServer(bytes, "createNewCommandManager");
            }
        }else{
            return bytes;
        }
    }

    public byte[] transformMinecraftServer(byte[] bytes, String createName) {
        ClassNode cnode = ASMHelper.createClassNode(bytes, 0);

        //Find the constructor
        MethodNode mnode = ASMHelper.findMethod(new Mapping("net/minecraft/server/MinecraftServer", "<init>", "(Ljava/io/File;Ljava/net/Proxy;Ljava/io/File;)V"), cnode);

        //Find "this.commandManager = new ServerCommandManager();"
        int offset = 0;
        while(mnode.instructions.get(offset).getOpcode() != Opcodes.INVOKEVIRTUAL || !((MethodInsnNode) mnode.instructions.get(offset)).name.equals(createName)) offset++;

        //Replace vanilla's ServerCommandManager with our own PumpkinCommandManager
        MethodInsnNode newNode = (MethodInsnNode) mnode.instructions.get(offset);
        newNode.setOpcode(Opcodes.INVOKESTATIC);
        newNode.desc = "()Lnl/jk_5/pumpkin/server/command/PumpkinCommandManager;";
        newNode.name = "create";
        newNode.owner = "nl/jk_5/pumpkin/server/command/PumpkinCommandManager";
        mnode.instructions.remove(newNode.getPrevious()); //Remove ALOAD_0 before the invoke line

        return ASMHelper.createBytes(cnode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    }
}
