package scalinghealthissues.proxy;

import net.minecraft.client.Minecraft;
import scalinghealthissues.network.PacketHandler;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();

        PacketHandler.registerClientMessages();
    }

    @Override
    public boolean isSinglePlayer() { return Minecraft.getMinecraft().isSingleplayer(); }
}