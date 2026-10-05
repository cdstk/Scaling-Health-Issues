package scalinghealthissues.proxy;

import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public boolean isSinglePlayer() { return Minecraft.getMinecraft().isSingleplayer(); }
}