package scalinghealthissues.config.data;

import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;


public class EnchantWithLevelsEntry extends ValueRangeEntry {

    @Config.Comment("Can include Treasure Enchantments")
    @Config.Name("Treasure")
    @Order(2)
    public boolean isTreasure = false;

    public EnchantWithLevelsEntry() {}

    public EnchantWithLevelsEntry(int value) {
        this.min = this.max = value;
    }

    public EnchantWithLevelsEntry(int value, boolean isTreasure) {
        this(value);
        this.isTreasure = isTreasure;
    }

    public EnchantWithLevelsEntry(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public EnchantWithLevelsEntry(int min, int max, boolean isTreasure) {
        this(min, max);
        this.isTreasure = isTreasure;
    }
}
