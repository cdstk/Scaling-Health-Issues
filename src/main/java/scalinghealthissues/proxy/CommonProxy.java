package scalinghealthissues.proxy;

import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.network.PacketHandler;

public class CommonProxy {

    public void preInit() {
        PacketHandler.registerMessages(ScalingHealthIssues.MODID);
    }

    public boolean isSinglePlayer() { return false; }
}