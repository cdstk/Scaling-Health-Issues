package scalinghealthissues.config.folders;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import scalinghealthissues.Tags;
import scalinghealthissues.compat.ModLoadedUtil;

@MixinConfig(name = Tags.MODID)
public class CompatibilityConfig {

    @Config.Name("Bloodmoon")
    public BloodmoonConfig bloodmoon = new BloodmoonConfig();

    @Config.Name("First Aid")
    public FirstAidConfig firstAid = new FirstAidConfig();

    @Config.Name("Infernal Mobs")
    public InfernalMobsConfig infernalMobs = new InfernalMobsConfig();

    @Config.Name("Lycanites Mobs")
    public LycanitesMobsConfig lycanitesMobs = new LycanitesMobsConfig();

    @Config.Comment({
            "Players will not experience Bloodmoons until they have used a specified number of Heart Containers.",
            "The server will set a Bloodmoon but only eligible players will experience their spawns.",
            "Singleplayer will be allowed to sleep through Bloodmoon nights that they can not experience."
    })
    @Config.Name("Mixin: Bloodmoon Heart Container Requirement (Bloodmoon)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.bloodmoon.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.BLOODMOON_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean bloodmoonHeartRequirement = true;

    @Config.Comment({
            "Death messages involving Champions will display their Rank.",
            "Hovering over the Rank will show all their modifiers."
    })
    @Config.Name("Mixin: Champions in Death Messages (Champions/Vanilla)")
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.scalinghealthissues.vanilla.champions.json",
            lateMixin = "mixins.scalinghealthissues.champions.json",
            defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.CHAMPIONS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean championDeathMessage = true;

    @Config.Comment({
            "Handles Defiled Lands Bleed damage as Non lethal damage.",
            "Intended to be used when enabling damage scaling for the \"magic\" DamageSource as it is commonly used elsewhere.",
    })
    @Config.Name("Mixin: Defiled Lands Bleed is Non Lethal (Defiled Lands)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.defiledlands.bleednonlethal.json", defaultValue = false)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.DEFILED_LANDS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean defiledLandsBleedNonLethal = false;

    @Config.Comment({
            "Entities that deal Significant Damage dealt to a Player will be cached and the client notified.",
            "   Notifies about Reskillable Undershirt usage",
            "   Notifies about Morphine max health ability usage",
            "   Notifies about Armor breaking"
    })
    @Config.Name("Mixin: Cache Significant Damage (First Aid)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.firstaid.significantdamage.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.FIRST_AID_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean firstAidSignificantDamage = true;

    @Config.Comment({
            "Death messages involving Infernal mobs will display their Classification.",
            "Hovering over the Classification will show all their modifiers.",
            "Additionally lets all players know when an Infernal is killed by a player via chat message with configurable minimum modifier count."
    })
    @Config.Name("Mixin: Infernal Classifications in Death Messages (Vanilla)")
    @MixinConfig.MixinToggle(earlyMixin = "mixins.scalinghealthissues.vanilla.infernalmobs.json", lateMixin = "mixins.scalinghealthissues.infernalmobs.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.INFERNAL_MOBS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean infernalDeathMessage = true;

    @Config.Comment({
            "Infernal Modified Mobs will spawn with equipment (armor, weapon, offhand) from their configured drops.",
            "This will be similar to how Scaling Health Blighted Mobs are equipped with armor and weapons on spawn.",
            "New equipment will not replace existing and drops are treated like Vanilla naturally spawned armor."
    })
    @Config.Name("Mixin: Infernal Mobs Use Loot As Equipment (Infernal Mobs)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.infernalmobs.equipment.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.INFERNAL_MOBS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean infernalWearLoot = true;

    @Config.Comment({
            "Allows the Enchantment Loot Pools for Enchanted Books and Equipment to be modified.",
            "This can be used to fix a inconsistency where book only have Vanilla Enchantments while equipment can have modded.",
            "This can also be used to interact with RLCraft Luckified mod by using a Loot Function pool which considers luck."
    })
    @Config.Name("Mixin: Infernal Mobs Configurable Enchantment Pool (Infernal Mobs)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.infernalmobs.enchantment.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.INFERNAL_MOBS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean infernalEnchantmentPool = true;

    @Config.Comment({
            "Players will not experience Lycanites Mob Events until they have used a specified number of Heart Containers.",
            "The server will set Mob Events but only eligible players will experience their spawns."
    })
    @Config.Name("Mixin: Lycanites Mob Event Heart Container Requirement (Lycanites Mobs)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.lycanitesmobs.mobevent.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.LYCANITES_MOBS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean lycanitesEventHeartRequirement = true;

    @Config.Comment("Applies the Pet Regen mechanic to tamed Lycanites entities.")
    @Config.Name("Mixin: Lycanites Pets Auto Regen (Lycanites Mobs)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.lycanitesmobs.petregen.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.LYCANITES_MOBS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean lycanitesPetRegen = true;

    @Config.Comment({
            "Handles Lycanites Bleed damage as Non lethal damage.",
            "Intended to be used when enabling damage scaling for the \"magic\" DamageSource as it is commonly used elsewhere.",
    })
    @Config.Name("Mixin: Lycanites Bleed is Non Lethal (Lycanites Mobs)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.lycanitesmobs.bleednonlethal.json", defaultValue = false)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.LYCANITES_MOBS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean lycanitesBleedNonLethal = false;

    @Config.Comment({
            "Handles SRP Bleed damage as Non lethal damage.",
            "Intended to be used when enabling damage scaling for the \"magic\" DamageSource as it is commonly used elsewhere.",
    })
    @Config.Name("Mixin: SRP Bleed is Non Lethal (Scape and Run: Parasites)")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.srp.bleednonlethal.json", defaultValue = false)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.SRP_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean srpBleedNonLethal = false;
}
