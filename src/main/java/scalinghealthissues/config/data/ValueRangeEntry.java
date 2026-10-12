package scalinghealthissues.config.data;

import meldexun.betterconfig.api.Order;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Config;

import java.util.Random;

public class ValueRangeEntry {

    @Config.Name("Minimum")
    @Config.RangeInt(min = 0)
    @Order(0)
    public int min = 0;

    @Config.Name("Maximum")
    @Config.RangeInt(min = 0)
    @Order(1)
    public int max = 0;

    public ValueRangeEntry() {}

    public ValueRangeEntry(int value) {
        this.min = this.max = value;
    }

    public ValueRangeEntry(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getRandomValue(Random rand) {
        return MathHelper.getInt(rand, this.min, this.max);
    }
}
