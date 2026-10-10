package scalinghealthissues.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.Tags;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.network.GuiHandler;
import scalinghealthissues.network.PacketHandler;

public class CommonProxy {

    public void preInit() {
        PacketHandler.registerMessages(Tags.MODID);
    }

    public void init() {
        if(ConfigHandler.server.enableEntityEquipmentView) {
            MinecraftForge.EVENT_BUS.register(GuiHandler.class);
            NetworkRegistry.INSTANCE.registerGuiHandler(ScalingHealthIssues.instance, new GuiHandler());
        }
    }

    public boolean isSinglePlayer() { return false; }
}