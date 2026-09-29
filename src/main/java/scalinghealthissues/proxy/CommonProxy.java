package scalinghealthissues.proxy;

import scalinghealthissues.Tags;
import scalinghealthissues.network.PacketHandler;

public class CommonProxy {

    public void preInit() {
        PacketHandler.registerMessages(Tags.MODID);
    }

    public boolean isSinglePlayer() { return false; }
}