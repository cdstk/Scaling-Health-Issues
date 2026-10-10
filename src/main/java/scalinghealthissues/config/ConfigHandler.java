package scalinghealthissues.config;

import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.BetterConfigManager;
import meldexun.betterconfig.api.Order;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.Tags;
import scalinghealthissues.config.folders.BlightConfig;
import scalinghealthissues.config.folders.CompatibilityConfig;
import scalinghealthissues.config.folders.DamageScalingConfig;

import java.util.ArrayList;

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

	@Config.Name("Server")
	public static ServerConfig server = new ServerConfig();

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

	public static class ServerConfig {

		@Config.Comment({
				"Toggles the necessary server side handling for viewing the Equipment of entities in a GUI.",
				"The system relies on the \"/scalinghealthissues viewinventory [entityselector]\" command.",
				"    The Entity Selector is a vanilla permission based command utility, basic clients can only specify with UUIDs.",
				"    Creative mode and OP status is required in order to allow inventory management",
				"Entities that killed a player will be temporarily cached.",
				"Players can query for a loaded entity to bring up a GUI showing its equipment.",
				"Intended to work with the Mixin: \"Death Message Clickable Equipment\" so players can see what they died to."
		})
		@Config.Name("Entity Equipment View GUI")
		@Config.RequiresMcRestart
		@Order(0)
		public boolean enableEntityEquipmentView = true;

		@Config.Comment({
				"In addition to entities that killed players, cache entities that are killed if:",
				"    They are Blighted and death announcement is enabled",
				"    They are Infernal (Infernal Mobs) and announcement enabled",
				"    They are a Champion (Champions) and announcement enabled",
		})
		@Config.Name("Entity Equipment - Cache Killed")
		@Order(1)
		public boolean entityViewCacheKilled = true;

		@Config.Comment({
				"If Cache Killed is enabled, any mobs specified will also be cached.",
				"Only useful to add if the mob is involved in a public death message."
		})
		@Config.Name("Entity Equipment - Cache Additional List")
		@Order(2)
		public ArrayList<ResourceLocation> entityViewCacheAdditional = new ArrayList<>();

		@Config.Comment({
				"Allow players to try viewing the equipment of another player.",
				"The system does not try to cache players nor makes player names clickable.",
				"Intended to be used as a local sharing feature."
		})
		@Order(3)
		@Config.Name("Entity Equipment - Players")
		public boolean entityViewPlayers = true;
	}

	@MixinConfig(name = Tags.MODID)
	public static class MixinToggleConfig {

		@Config.Comment({
				"Fixes the Blight configs \"Armor Piece Chance\" and \"Hand Piece Chance\" being inverted.",
				"ex. A config value of 1.0, or 100% was incorrectly treated as 0% chance."
		})
		@Config.Name("Fix Inverted Config For Equipment Piece Chances")
		@MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.fixconfigblight.json", defaultValue = false)
		@Config.RequiresMcRestart
		public boolean fixConfigEquipmentChance = false;

		@Config.Comment({
				"The Server will add a click event and underline the entity's TextComponent used to display it's name.",
				"The click event runs a command to view the inventory of the entity.",
				"Intended to be used to view the equipment of what kill you with \"Entity Equipment View GUI\" enabled"
		})
		@Config.Name("Death Message Clickable Equipment (Vanilla)")
		@MixinConfig.MixinToggle(earlyMixin = "mixins.scalinghealthissues.vanilla.deathmsgequipment.json", defaultValue = true)
		@Config.RequiresMcRestart
		public boolean vanillaDeathMessageEquipment = true;
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