package scalinghealthissues.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.folders.BlightConfig;
import scalinghealthissues.config.folders.CompatibilityConfig;

@Config(modid = ScalingHealthIssues.MODID)
public class ForgeConfigHandler {

	@Config.Name("Compatibility")
	public static final CompatibilityConfig compat = new CompatibilityConfig();

	@Config.Name("Mob")
	public static final MobConfig mob = new MobConfig();

//	@Config.Name("Mixin Toggles")
//	public static final MixinToggleConfig mixin = new MixinToggleConfig();

	public static final DebugConfig debug = new DebugConfig();

	public static class DebugConfig {

		@Config.Name("Log Blights")
		public boolean logBlights = false;

		@Config.Name("Log Difficulty")
		public boolean logDifficulty = false;
	}

	public static class MobConfig {

		@Config.Name("Blights")
		public final BlightConfig blight = new BlightConfig();
	}

//	@MixinConfig(name = ScalingHealthIssues.MODID)
//	public static class MixinToggleConfig {
//
//		@Config.Comment({
//				"Death messages will state if a Blight was involved.",
//				"Hovering over the message will show the Scaling Health mod Difficulty of involved entities."
//		})
//		@Config.Name("Blights and Difficulty in Death Messages)")
//		@MixinConfig.MixinToggle(earlyMixin = "mixins.scalinghealthissues.vanilla.json", defaultValue = true)
//		public boolean scalingHealthDeathMessage = true;
//
//		@Config.Comment("Example Late Mixin Toggle Config")
//		@Config.Name("Enable JEI Init Mixin (JEI)")
//		@MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.scalinghealth.json", defaultValue = true)
//		public boolean enableJeiMixin = true;
//	}

	@Mod.EventBusSubscriber(modid = ScalingHealthIssues.MODID)
	private static class EventHandler{

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(ScalingHealthIssues.MODID)) {
				ConfigManager.sync(ScalingHealthIssues.MODID, Config.Type.INSTANCE);

				initConfig();
			}
		}
	}

	public static void initConfig() {
		ForgeConfigHandler.mob.blight.init();
	}
}