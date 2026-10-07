package scalinghealthissues.config.folders;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.data.PotionConfigEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlightConfig {

    public static final Map<Potion, PotionConfigEntry> BLIGHT_POTIONS = new HashMap<>();

    @Config.Name("Potion")
    public PotionConfig potion = new PotionConfig();

    public static class PotionConfig {

        @Config.Comment({
                "The original config transferred to here would look like:",
                "    minecraft:speed,4,6000",
                "    minecraft:strength,1,6000",
                "    minecraft:fire_resistance,0,6000",
                "    minecraft:invisibility,0,0"
        })
        @Config.Name("Blight Constant Potions")
        public ArrayList<PotionConfigEntry> blightPotionEffects = new ArrayList<>();
    }

    public void init() {
        BLIGHT_POTIONS.clear();

        ConfigHandler.mob.blight.potion.blightPotionEffects.forEach(potionEntry -> {
            Potion potion = ForgeRegistries.POTIONS.getValue(potionEntry.id);
            if(potion != null)
                BLIGHT_POTIONS.putIfAbsent(potion, potionEntry);
        });
    }
}
