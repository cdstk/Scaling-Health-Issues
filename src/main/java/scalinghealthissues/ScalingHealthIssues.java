package scalinghealthissues;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scalinghealthissues.command.ScalingHealthIssuesCommand;
import scalinghealthissues.compat.ModLoadedUtil;
import scalinghealthissues.compat.handlers.FirstAidHandler;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.handlers.BetterBlightHandler;
import scalinghealthissues.handlers.BetterDifficultyHandler;
import scalinghealthissues.handlers.DamageScalingOverhaulHandler;
import scalinghealthissues.proxy.CommonProxy;
import scalinghealthissues.util.DamageSources;

@Mod(
        modid = Tags.MODID,
        version = Tags.VERSION,
        name = Tags.NAME,
        dependencies =
                "required-after:fermiumbooter@[1.3.2,);" +
                "required-after:betterconfig@[1.3.0,);" +
                "required-after:scalinghealth;",
        acceptableRemoteVersions = "*"
)
public class ScalingHealthIssues {
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;
	
    @SidedProxy(clientSide = "scalinghealthissues.proxy.ClientProxy", serverSide = "scalinghealthissues.proxy.CommonProxy")
    public static CommonProxy PROXY;

	@Instance(Tags.MODID)
	public static ScalingHealthIssues instance;
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ScalingHealthIssues.PROXY.preInit();

        MinecraftForge.EVENT_BUS.register(BetterBlightHandler.class);
        MinecraftForge.EVENT_BUS.register(BetterDifficultyHandler.class);

        if(ConfigHandler.dmgScale.overhaulDamageScaling)
            MinecraftForge.EVENT_BUS.register(DamageScalingOverhaulHandler.class);

        if(ModLoadedUtil.FIRST_AID.isLoaded()) {
            MinecraftForge.EVENT_BUS.register(FirstAidHandler.class);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ScalingHealthIssues.PROXY.init();

        if(ModLoadedUtil.FIRST_AID.isLoaded()) {
            FirstAidHandler.registerDefaults();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ConfigHandler.initConfig();

        DamageSources.postInitDamageSourceFlags();

        completedLoading = true;
    }

    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new ScalingHealthIssuesCommand());
    }
}