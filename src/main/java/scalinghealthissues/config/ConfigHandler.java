package scalinghealthissues.config;

import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.BetterConfigManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.Tags;
import scalinghealthissues.config.folders.BlightConfig;
import scalinghealthissues.config.folders.CompatibilityConfig;
import scalinghealthissues.config.folders.DamageScalingConfig;

@BetterConfig(
		modid = Tags.MODID,
		version = Tags.CFG_VERSION
)
public class ConfigHandler {

	@Config.Comment("Mod Compat Mixin Toggles and other Settings")
	@Config.Name("Compatibility")
	public static CompatibilityConfig compat = new CompatibilityConfig();

	@Config.Name("Mob")
	public static MobConfig mob = new MobConfig();

	@Config.Name("Damage Scaling")
	public static DamageScalingConfig dmgScale = new DamageScalingConfig();

	@Config.Name("Mixin Toggles")
	public static MixinToggleConfig mixin = new MixinToggleConfig();

	public static DebugConfig debug = new DebugConfig();

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
		public BlightConfig blight = new BlightConfig();
	}

	@MixinConfig(name = Tags.MODID)
	public static class MixinToggleConfig {

		@Config.Comment({
				"Fixes the Blight configs \"Armor Piece Chance\" and \"Hand Piece Chance\" being inverted.",
				"ex. A config value of 1.0, or 100% was incorrectly treated as 0% chance."
		})
		@Config.Name("Fix Inverted Config For Equipment Piece Chances")
		@MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.fixconfigblight.json", defaultValue = false)
		public boolean fixConfigEquipmentChance = false;
	}

	@Mod.EventBusSubscriber(modid = Tags.MODID)
	private static class EventHandler{

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(Tags.MODID)) {
				BetterConfigManager.sync(Tags.MODID);

				initConfig();
			}
		}
	}

	public static void initConfig() {
		ConfigHandler.mob.blight.init();
		ConfigHandler.dmgScale.init();
	}
}