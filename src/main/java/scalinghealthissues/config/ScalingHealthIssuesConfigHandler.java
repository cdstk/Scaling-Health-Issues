package scalinghealthissues.config;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.folders.BlightConfig;
import scalinghealthissues.config.folders.CompatibilityConfig;
import scalinghealthissues.config.folders.DamageScalingConfig;

@Config(modid = ScalingHealthIssues.MODID)
public class ScalingHealthIssuesConfigHandler {

	@Config.Name("Compatibility")
	public static final CompatibilityConfig compat = new CompatibilityConfig();

	@Config.Name("Mob")
	public static final MobConfig mob = new MobConfig();

	@Config.Name("Damage Scaling")
	public static final DamageScalingConfig dmgScale = new DamageScalingConfig();

	@Config.Name("Mixin Toggles")
	public static final MixinToggleConfig mixin = new MixinToggleConfig();

	public static final DebugConfig debug = new DebugConfig();

	public static class DebugConfig {

		@Config.Name("Log Mod Config")
		public boolean logConfig = false;

		@Config.Name("Log Blights")
		public boolean logBlights = false;

		@Config.Name("Log Damage Scaling Overhaul")
		public boolean logDamageScale = false;

		@Config.Name("Log Difficulty")
		public boolean logDifficulty = false;
	}

	public static class MobConfig {

		@Config.Name("Blights")
		public final BlightConfig blight = new BlightConfig();
	}

	@MixinConfig(name = ScalingHealthIssues.MODID)
	public static class MixinToggleConfig {

		@Config.Comment({
				"Fixes the Blight configs \"Armor Piece Chance\" and \"Hand Piece Chance\" being inverted.",
				"ex. A config value of 1.0, or 100% was incorrectly treated as 0% chance."
		})
		@Config.Name("Fix Inverted Config For Equipment Piece Chances")
		@MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.fixconfigblight.json", defaultValue = false)
		public boolean fixConfigEquipmentChance = false;
	}

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
		ScalingHealthIssuesConfigHandler.mob.blight.init();
		ScalingHealthIssuesConfigHandler.dmgScale.init();
	}
}