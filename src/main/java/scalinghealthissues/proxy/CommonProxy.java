package scalinghealthissues.proxy;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.network.GuiHandler;

public class CommonProxy {

    public void preInit() {

    }

    public void init() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ScalingHealthIssues.instance, new GuiHandler());
    }

    public boolean isSinglePlayer() { return false; }
}