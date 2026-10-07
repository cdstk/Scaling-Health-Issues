package scalinghealthissues.config.data;

import meldexun.betterconfig.api.Order;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

public class PotionConfigEntry {

    @Config.Name("Potion Effect")
    @Order(0)
    public ResourceLocation id;

    @Config.Name("Amplifier")
    @Order(1)
    public int amplifier = 0;

    @Config.Comment("Duration in Ticks")
    @Config.Name("Duration")
    @Config.RangeInt(min = 1)
    @Order(2)
    public int duration = 0;

    public PotionConfigEntry() {
        this.id = new ResourceLocation("speed");
        this.duration = 5 * 20 * 60;
    }

    public PotionConfigEntry(ResourceLocation id, int amplifier, int duration) {
        this.id = id;
        this.amplifier = amplifier;
        this.duration = duration;
    }
}
