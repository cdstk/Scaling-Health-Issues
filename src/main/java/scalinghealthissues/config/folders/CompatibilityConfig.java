package scalinghealthissues.config.folders;

import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.compat.ModLoadedUtil;

@MixinConfig(name = ScalingHealthIssues.MODID)
public class CompatibilityConfig {

    @Config.Comment({
            "Death messages involving Champions will display their Rank.",
            "Hovering over the Rank will show all their modifiers."
    })
    @Config.Name("Mixin: Champions in Death Messages (Champions)")
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
            "Fixes First Aid's Poison handling ignoring Player Immunity Frames",
            "Also allows the damage to be modified by other mods (such as Scaling Health damage scaling)"
    })
    @Config.Name("First Aid Poison Damage IFrame Bug Fix (First Aid)")
    public boolean firstAidPoisonFix = true;

    @Config.Comment("If IFrame ignoring poison is desirable, extend this aspect to all mobs instead of only players.")
    @Config.Name("First Aid Poison Damage IFrame Bug UnFix All (First Aid)")
    public boolean firstAidPoisonUnfixAll = false;

    @Config.Comment({
            "Death messages involving Infernal mobs will display their Classification.",
            "Hovering over the Classification will show all their modifiers."
    })
    @Config.Name("Mixin: Infernal Classifications in Death Messages (Infernal Mobs)")
    @MixinConfig.MixinToggle(earlyMixin = "mixins.scalinghealthissues.vanilla.infernalmobs.json", defaultValue = true)
    @MixinConfig.CompatHandling(
            modid = ModLoadedUtil.INFERNAL_MOBS_MODID,
            desired = true,
            reason = "Mod needed for this Mixin to properly work",
            warnIngame = false
    )
    @Config.RequiresMcRestart
    public boolean infernalDeathMessage = true;

    @Config.Comment("Let all players know when an Infernal is killed by a player via chat message.")
    @Config.Name("Infernal Killed by Player Message (Infernal Mobs)")
    public boolean infernalKilledByPlayerMessage = true;

    @Config.Comment({
            "The minimum number of Modifiers the Infernal killed must have.",
            "\tInfernal - 11 or more",
            "\tUltra - 6 to 10",
            "\tRare - 5 or less"
    })
    @Config.Name("Infernal Killed by Player Message Modifier Count")
    public int infernalKilledByPlayerModifiers = 11;

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

    @Config.Comment("The number of Heart Containers that must be used in order to experience Bloodmoons")
    @Config.Name("Bloodmoon Heart Container Requirement")
    public int bloodmoonHeartContainersRequired = 3;

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

    @Config.Comment("The number of Heart Containers that must be used in order to experience Lycanites Mob Events")
    @Config.Name("Lycanites Mob Event Heart Container Requirement")
    public int lycanitesEventHeartContainersRequired = 3;

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

    @Config.Comment("Whether the Scaling Health Auto Regen is applied to Lycanites Soulbinds, who have their own auto regen mechanic.")
    @Config.Name("Lycanites Pets Auto Regen Soulbinds")
    public boolean lycanitesPetRegenSoulbind = true;
}
