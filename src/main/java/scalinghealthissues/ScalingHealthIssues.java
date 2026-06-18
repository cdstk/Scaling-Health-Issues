package scalinghealthissues;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scalinghealthissues.capability.CapabilityExampleHandler;
import scalinghealthissues.handlers.ModRegistry;
import scalinghealthissues.proxy.CommonProxy;

@Mod(
        modid = ScalingHealthIssues.MODID,
        version = ScalingHealthIssues.VERSION,
        name = ScalingHealthIssues.NAME,
        dependencies =
                "required-after:fermiumbooter;" +
                "required-after:scalinghealth;"
)
public class ScalingHealthIssues {
    public static final String MODID = "scalinghealthissues";
    public static final String VERSION = "0.0.0";
    public static final String NAME = "Scaling Health Issues";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;
	
    @SidedProxy(clientSide = "scalinghealthissues.proxy.ClientProxy", serverSide = "scalinghealthissues.proxy.CommonProxy")
    public static CommonProxy PROXY;

	@Instance(MODID)
	public static ScalingHealthIssues instance;
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModRegistry.init();
        ScalingHealthIssues.PROXY.preInit();

        CapabilityExampleHandler.registerCapability();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        completedLoading = true;
    }
}