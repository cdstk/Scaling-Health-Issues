package scalinghealthissues;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scalinghealthissues.compat.ModLoadedUtil;
import scalinghealthissues.compat.handlers.InfernalMobsHandler;
import scalinghealthissues.config.ForgeConfigHandler;
import scalinghealthissues.handlers.BetterBlightHandler;
import scalinghealthissues.handlers.BetterDifficultyHandler;
import scalinghealthissues.handlers.DamageScalingOverhaulHandler;
import scalinghealthissues.proxy.CommonProxy;

@Mod(
        modid = ScalingHealthIssues.MODID,
        version = ScalingHealthIssues.VERSION,
        name = ScalingHealthIssues.NAME,
        dependencies =
                "required-after:fermiumbooter;" +
                "required-after:scalinghealth;",
        acceptableRemoteVersions = "*" // TODO CHECK IF SIDED BEFORE RELEASE
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
        ScalingHealthIssues.PROXY.preInit();

        MinecraftForge.EVENT_BUS.register(BetterBlightHandler.class);
        MinecraftForge.EVENT_BUS.register(BetterDifficultyHandler.class);

        if(ForgeConfigHandler.dmgScale.overhaulDamageScaling)
            MinecraftForge.EVENT_BUS.register(DamageScalingOverhaulHandler.class);

        if(ModLoadedUtil.INFERNAL_MOBS.isLoaded()) {
            MinecraftForge.EVENT_BUS.register(InfernalMobsHandler.class);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ForgeConfigHandler.initConfig();

        completedLoading = true;
    }
}