package scalinghealthissues.config.folders;

import meldexun.betterconfig.api.Sync;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

@Sync
public class FirstAidConfig {

    @Config.Comment({
            "Fixes First Aid's Poison handling ignoring Player Immunity Frames",
            "Also allows the damage to be modified by other mods (such as Scaling Health damage scaling)"
    })
    @Config.Name("Poison Damage IFrame Bug Fix")
    public boolean poisonIFramesFix = true;

    @Config.Comment("If IFrame ignoring poison is desirable, extend this aspect to all mobs instead of only players.")
    @Config.Name("Poison Damage IFrame Bug UnFix All")
    public boolean poisonIFramesUnfix = false;

    @Config.Comment({
            "Buffs Morphine Potion Effect/Item to include the following:",
            "    Being at max health can prevent one-shot deaths, leaving you at half a heart on vitals",
            "    Damage will be not overflow/transfer to another body part if the part being targeted has any health",
            "The buff will not apply to certain damage sources such as Explosions, specifically any usages of:",
            "    DirectDamageDistribution",
            "    EqualDamageDistribution"
    })
    @Config.Name("Morphine Rework")
    public boolean morphineRework = true;

    @Config.Comment("Text Formatting Value-By-Name for the chat message when preventing a one-shot")
    @Config.Name("Morphine Rework - Message Formatting")
    public String morphineMessageFormat = "YELLOW";

    @Config.Comment("Sound Event Resource Location for the sound effect that will play when preventing a one-shot")
    @Config.Name("Morphine Rework - Sound Event")
    public ResourceLocation morphineSoundEvent = new ResourceLocation("entity.witch.drink");

    @Config.Comment({
            "Tweak to obtaining and losing the Morphine Potion Effect:",
            "    + Can not be removed by general effect cleanse/purge/removal, only timeout allowed",
            "    - Duration can not be refreshed/extended outside of using the Morphine Item"
    })
    @Config.Name("Morphine Rework - Applicable Rules")
    public boolean morphineApplyRules = true;

    @Config.Comment("Minimum duration in seconds that the Morphine Item provides")
    @Config.Name("Morphine Rework - Minimum Duration")
    @Config.RangeInt(min = 0)
    public int morphineDurationMin = 210;

    @Config.Comment("Maximum duration in seconds that the Morphine Item provides")
    @Config.Name("Morphine Rework - Maximum Duration")
    @Config.RangeInt(min = 0)
    public int morphineDurationMax = 270;

    @Config.Comment({
            "Seconds between each unique duration.",
            "First Aid's default values is 15 second between which creates 4 total unique durations."
    })
    @Config.Name("Morphine Rework - Bound Increment")
    @Config.RangeInt(min = 0)
    public int morphineDurationStep = 15;
}
