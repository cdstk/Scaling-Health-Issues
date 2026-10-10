package scalinghealthissues.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scalinghealthissues.network.packet.PacketEntityViewableData;

// Not used ATM
public class PacketHandler {

    public static SimpleNetworkWrapper instance = null;

    public static void registerMessages(String channelName) {
        instance = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }

    public static void registerMessages() {
        instance.registerMessage(PacketEntityViewableData.ServerHandler.class, PacketEntityViewableData.class, 1, Side.SERVER);
//        instance.registerMessage(PacketEntityDifficulty.ServerHandler.class, PacketEntityDifficulty.class, 1, Side.SERVER);
    }

    @SideOnly(Side.CLIENT)
    public static void registerClientMessages() {
        instance.registerMessage(PacketEntityViewableData.ClientHandler.class, PacketEntityViewableData.class, 1, Side.CLIENT);
//        instance.registerMessage(PacketEntityDifficulty.ClientHandler.class, PacketEntityDifficulty.class, 1, Side.CLIENT);
    }
}
