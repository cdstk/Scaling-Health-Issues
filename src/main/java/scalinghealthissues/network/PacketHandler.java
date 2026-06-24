package scalinghealthissues.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketHandler {

    public static SimpleNetworkWrapper instance = null;

    public static void registerMessages(String channelName) {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }

    public static void registerMessages() {
        instance.registerMessage(PacketEntityDifficulty.ServerHandler.class, PacketEntityDifficulty.class, 1, Side.SERVER);
    }

    @SideOnly(Side.CLIENT)
    public static void registerClientMessages() {
        instance.registerMessage(PacketEntityDifficulty.ClientHandler.class, PacketEntityDifficulty.class, 1, Side.CLIENT);
    }
}
