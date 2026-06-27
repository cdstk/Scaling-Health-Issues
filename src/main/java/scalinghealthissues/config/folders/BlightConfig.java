package scalinghealthissues.config.folders;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ScalingHealthIssuesConfigHandler;
import scalinghealthissues.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BlightConfig {

//    private static final Map<Potion, Triple<Integer, Integer, Integer>> blightPotions = new HashMap<>();
    private static final Map<Potion, Pair<Integer, Integer>> blightPotions = new HashMap<>();

    @Config.Name("Equipment")
    public EquipmentConfig equipment = new EquipmentConfig();

    @Config.Name("Equipment Enchants")
    public EnchantConfig enchant = new EnchantConfig();

    @Config.Name("Potion")
    public PotionConfig potion = new PotionConfig();

    public static class EquipmentConfig {

        @Config.Name("Enable Spawn Enchantments")
        public boolean enableEnchantments = true;

        @Config.Name("Enchanted Armor Piece Chance")
        public double enchantArmorChance = 0.5D;

        @Config.Name("Enchanted Hand Chance")
        public double enchantHandChance = 0.5D;

        @Config.Name("Minimum Enchantment Tier")
        public int minimumEnchantmentTier = 1;

        @Config.Name("Enchantment Tier Up Chance")
        public double enchantTierUpChance = 0.95D;
    }

    public static class EnchantConfig {

        @Config.Name("Enable Spawn Enchantments")
        public boolean enableEnchantments = true;

        @Config.Name("Enchanted Armor Piece Chance")
        public double enchantArmorChance = 0.5D;

        @Config.Name("Enchanted Hand Chance")
        public double enchantHandChance = 0.5D;

        @Config.Name("Minimum Enchantment Tier")
        public int minimumEnchantmentTier = 1;

        @Config.Name("Enchantment Tier Up Chance")
        public double enchantTierUpChance = 0.95D;
    }

    public static class PotionConfig {

        @Config.Comment({
                "The original config transferred to here would look like:",
                "\tminecraft:speed,4,6000",
                "\tminecraft:strength,1,6000",
                "\tminecraft:fire_resistance,0,6000",
                "\tminecraft:invisibility,0,0"
        })
        @Config.Name("Blight Constant Potions")
        public String[] blightPotions = {

        };
    }

    public static boolean isBlightPotion(Potion potion) {
        return blightPotions.containsKey(potion);
    }

    public static int getBlightPotionAmplifier(Potion potion) {
        if(isBlightPotion(potion)) {
            Pair<Integer, Integer> entry = blightPotions.get(potion);
            return entry.left;
        }

        return 0;
    }

    public static int getBlightPotionDuration(Potion potion) {
        int duration = net.silentchaos512.scalinghealth.config.Config.Mob.Blight.potionDuration;
        if (duration < 0)
            duration = Integer.MAX_VALUE;

        if(isBlightPotion(potion)) {
            Pair<Integer, Integer> entry = blightPotions.get(potion);
            duration = entry.right;
        }

        return duration;
    }

    public void init() {
        blightPotions.clear();

        Arrays.stream(ScalingHealthIssuesConfigHandler.mob.blight.potion.blightPotions).forEach(line -> {
            String[] split = line.split(",");
            int amp = 0;
            int duration = 0;

            try {
                if(split.length >= 3) duration = Integer.parseInt(split[2].trim());
                if(split.length >= 2) amp = Integer.parseInt(split[1].trim());
            } catch (NumberFormatException e) {
                ScalingHealthIssues.LOGGER.log(Level.WARN, "Could not parse Blight Potion, skipping: {}", line);
                return;
            }
            Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(split[0]));
            if(potion != null)
                blightPotions.put(potion, new Pair<>(amp, duration));
        });
    }
}
