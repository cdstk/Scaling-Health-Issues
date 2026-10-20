package scalinghealthissues.config.folders;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.data.ConfigEnum;
import scalinghealthissues.config.data.EnchantWithLevelsEntry;
import scalinghealthissues.config.data.ItemStackEntry;
import scalinghealthissues.config.data.PotionConfigEntry;
import scalinghealthissues.util.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage") // For RangeMap, went out of Beta in Guava 32.0.0, but 1.12 Forge uses  21.0
public class BlightConfig {

    // Post init stage ResourceLocation -> NonNull Object
    public static final Map<Potion, PotionConfigEntry> BLIGHT_POTIONS = new HashMap<>();

    public static final Map<Integer, List<ItemStack>> HELMET_BY_TIER = new HashMap<>();
    public static final Map<Integer, List<ItemStack>> CHESTPLATE_BY_TIER = new HashMap<>();
    public static final Map<Integer, List<ItemStack>> LEGGINGS_BY_TIER = new HashMap<>();
    public static final Map<Integer, List<ItemStack>> BOOTS_BY_TIER = new HashMap<>();
    public static final Map<Integer, List<ItemStack>> MAINHAND_BY_TIER = new HashMap<>();
    public static final Map<Integer, List<ItemStack>> OFFHAND_BY_TIER = new HashMap<>();
    public static final Map<Integer, List<ItemStack>> BOW_BY_TIER = new HashMap<>();

    public static final RangeMap<Double, EnchantWithLevelsEntry> RANDOM_ENCHANT_BY_DIFFICULTY = TreeRangeMap.create();
    public static final RangeMap<Double, Integer> COMMON_TIER_BY_DIFFICULTY = TreeRangeMap.create();
    public static final RangeMap<Double, Float> TIER_UP_CHANCE_BY_DIFFICULTY = TreeRangeMap.create();
    public static final RangeMap<Double, Float> ARMOR_CHANCE_BY_DIFFICULTY = TreeRangeMap.create();
    public static final RangeMap<Double, Float> HAND_CHANCE_BY_DIFFICULTY = TreeRangeMap.create();

    // Helper method to supress beta warning
    @Nullable
    public static <V> V getRangeMapValue(RangeMap<Double, V> configMap, Double key) {
        return configMap.get(key);
    }

    @Config.Name("Enchantments")
    public EnchantmentConfig enchantments = new EnchantmentConfig();

    @Config.Name("Equipment")
    public EquipmentConfig equipment = new EquipmentConfig();

    @Config.Name("Potion")
    public PotionConfig potion = new PotionConfig();

    public static class EnchantmentConfig {

        @Config.Comment({
                "Random Enchantment Settings depending on a Mob's Difficulty",
                "The config specifies an inclusive starting bound up that links to the next entry",
                "    Minimum, Maximum, and Treasure follow the properties of the minecraft:enchant_with_levels loot function"
        })
        @Config.Name("Random Enchantments Per Difficulty")
        public Map<Double, EnchantWithLevelsEntry> possibleRandomEnchantments = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0D, new EnchantWithLevelsEntry(29, 30, false)),
                // 20 is Max Difficulty in RLC
                new Pair<>(30D, new EnchantWithLevelsEntry(30, 60, true)),
                new Pair<>(60D, new EnchantWithLevelsEntry(60, 90, true))
        ));

        @Config.Comment({
                "Determines where the Enchanted Equipment Enchantsments Pool comes from.",
                "Intended to be used to choose if it should interact with RLCraft Luckified or the So Many Enchantments blacklists.",
                "    ENCHANTMENT_HELPER - Directly use EnchantmentHelper.addRandomEnchantment",
                "    ENCHANT_WITH_LEVELS - The minecraft:enchant_with_levels loot function",
                "    FORGE_REGISTRY - Original behavior of always level 30 without treasure",
                "Infernal Mobs originally used FORGE_REGISTRY "
        })
        @Config.Name("Enchanted Equipment Enchantment Pool")
        public ConfigEnum.MultiEnchantmentList equipmentEnchantmentList = ConfigEnum.MultiEnchantmentList.ENCHANT_WITH_LEVELS;
    }

    public static class EquipmentConfig {

        @Config.Comment({
                "Overrides the default expected max tier of 4",
                "This value is used by Scaling Health and its API",
                "This value should be equal to the highest tier set in any Equipment Item List"
        })
        @Config.Name("Maximum Expected Tier")
        @Config.RequiresMcRestart
        public int maxExpectedTier = 5;

        @Config.Comment({
                "Allows a config item to replace an existing item set on natural spawn.",
                "    Mobs like Skeletons and Vindicators will be able to have a different mainhand",
                "    Although an item randomly provided by another mod can be replaced"
        })
        @Config.Name("Replace Existing Equipment On Spawn")
        public boolean replaceExistingEquipment = true;

        @Config.Comment({
                "Just like the original config, however set by Difficulty",
                "The config specifies an inclusive starting bound up that links to the next entry",
                "    If no valid entry exists, uses Scaling Health's original config value",
                "The highest commonly-occuring equipment tier for blights. This goes from 0 to 4",
                " inclusive. For armor, the defaults (tiers 0 to 4) are leather, gold, chainmail,",
                " iron, and diamond."
        })
        @Config.Name("Highest Common Tier Per Difficulty")
        public Map<Double, Integer> highestCommonTiers = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0D, 1),
                // 20 is Max Difficulty in RLC
                new Pair<>(100D, 2),
                new Pair<>(200D, 3),
                new Pair<>(300D, 4),
                new Pair<>(400D, 5)
        ));

        @Config.Comment({
                "Just like the original config, however set by Difficulty",
                "The config specifies an inclusive starting bound up that links to the next entry",
                "    If no valid entry exists, uses Scaling Health's original config value",
                "The chance that a higher tier will be selected for a blight. A common tier is chosen",
                " first, then it has a few chances to increase."
        })
        @Config.Name("Tier Up Chance Per Difficulty")
        public Map<Double, Float> tierUpChances = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0D, 0.095F),
                // 20 is Max Difficulty in RLC
                new Pair<>(100D, 0.2F),
                new Pair<>(200D, 0.3F),
                new Pair<>(300D, 0.4F),
                new Pair<>(400D, 0.5F)
        ));

        @Config.Comment({
                "Just like the original config, however set by Difficulty",
                "The config specifies an inclusive starting bound up that links to the next entry",
                "    If no valid entry exists, uses Scaling Health's original config value",
                "The chance of an additional armor piece being given. Every blight receives a helmet,",
                " then has this probability of receiving a chestplate. If it gets a chestplate, it has",
                " this probability of receiving leggings, etc."
        })
        @Config.Name("Armor Piece Chance Per Difficulty")
        public Map<Double, Float> armorChances = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0D, 0.5F),
                // 20 is Max Difficulty in RLC
                new Pair<>(30D, 0.75F),
                new Pair<>(60D, 1.0F)
        ));

        @Config.Comment({
                "Just like the original config, however set by Difficulty",
                "The config specifies an inclusive starting bound up that links to the next entry",
                "    If no valid entry exists, uses Scaling Health's original config value",
                "The chance that a blight will receive equipment in their hands (swords, etc.) They only",
                " get a chance at an offhand item if a main hand item is selected. Depending on the",
                " mods you have installed, there may not be any hand equipment to chose from."
        })
        @Config.Name("Hand Piece Chance Per Difficulty")
        public Map<Double, Float> handChances = ConfigHandler.linkedMapOf(Arrays.asList(
                // Generally 0% chance in default since SH didn't populate hands itself
                new Pair<>(0D, 0.25F),
                // 20 is Max Difficulty in RLC
                new Pair<>(30D, 0.75F),
                new Pair<>(60D, 1.0F)
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Helmet")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleHelmet = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("leather_helmet"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "sheep_helmet"))
                ))),
                new Pair<>(1, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("golden_helmet"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_silver_metal_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_copper_metal_helmet"))
                ))),
                new Pair<>(2, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("chainmail_helmet"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "scale_helmet")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "swinemask"))
                ))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("iron_helmet"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "umbrium_helmet")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "chitinarmor_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_yellow_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_white_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_red_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_desert_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_jungle_helmet"))
                ))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("diamond_helmet"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "forest_troll_leather_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "frost_troll_leather_helmet")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "mountain_troll_leather_helmet"))
                )))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Chestplate")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleChestplate = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("leather_chestplate"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "sheep_chestplate"))
                ))),
                new Pair<>(1, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("golden_chestplate"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_silver_metal_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_copper_metal_chestplate"))
                ))),
                new Pair<>(2, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("chainmail_chestplate"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "scale_chestplate")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "swinearmor_chestplate"))
                ))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("iron_chestplate"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "umbrium_chestplate")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "chitinarmor_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_yellow_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_white_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_red_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_desert_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_jungle_chestplate"))
                ))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("diamond_chestplate"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "forest_troll_leather_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "frost_troll_leather_chestplate")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "mountain_troll_leather_chestplate"))
                )))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Leggings")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleLeggings = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("leather_leggings"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "sheep_leggings"))
                ))),
                new Pair<>(1, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("golden_leggings"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_silver_metal_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_copper_metal_leggings"))
                ))),
                new Pair<>(2, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("chainmail_leggings"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "scale_leggings")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "swinearmor_leggings"))
                ))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("iron_leggings"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "umbrium_leggings")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "chitinarmor_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_yellow_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_white_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_red_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_desert_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_jungle_leggings"))
                ))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("diamond_leggings"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "forest_troll_leather_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "frost_troll_leather_leggings")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "mountain_troll_leather_leggings"))
                )))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Boots")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleBoots = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("leather_boots"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "sheep_boots"))
                ))),
                new Pair<>(1, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("golden_boots"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_silver_metal_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "armor_copper_metal_boots"))
                ))),
                new Pair<>(2, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("chainmail_boots"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "scale_boots")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "swinearmor_boots"))
                ))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("iron_boots"),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "umbrium_boots")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "chitinarmor_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_yellow_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_white_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "deathworm_red_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_desert_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_jungle_boots"))
                ))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("diamond_boots"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "forest_troll_leather_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "frost_troll_leather_boots")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "mountain_troll_leather_boots"))
                )))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found.",
                "    If the mob spawned holding any kind of Bow, it will use the \"Possible Bow\" list instead."
        })
        @Config.Name("Possible Mainhand")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleMainhand = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("wooden_sword"),
                        new ItemStackEntry("wooden_axe"),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemwoodhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemwooddagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemwoodbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemwoodnunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_wood")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_wood")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_wood")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_wood")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_wood")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_wood")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_wood")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "dagger_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "mace_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "parrying_dagger_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "battleaxe_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longsword_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "katana_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "saber_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "rapier_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "greatsword_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "hammer_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "warhammer_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "spear_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "halberd_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "pike_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "lance_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "glaive_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "staff")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "scythe_wood"))
                ))),
                new Pair<>(1, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("golden_sword"),
                        new ItemStackEntry("golden_axe"),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemgoldhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemgolddagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemgoldbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemgoldnunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_gold")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_gold")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_gold")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_gold")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_gold")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_gold")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_gold")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "dagger_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "mace_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "parrying_dagger_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "battleaxe_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longsword_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "katana_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "saber_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "rapier_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "greatsword_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "hammer_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "warhammer_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "spear_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "halberd_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "pike_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "lance_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "glaive_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "staff_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "scythe_gold")),
                        // Silver
                        new ItemStackEntry(new ResourceLocation("iceandfire", "silver_sword")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "silver_axe")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemsilverhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemsilverdagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemsilverbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemsilvernunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_silver")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_silver")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_silver")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_silver")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_silver")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_silver")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_silver")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "dagger_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "mace_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "parrying_dagger_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "battleaxe_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longsword_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "katana_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "saber_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "rapier_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "greatsword_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "hammer_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "warhammer_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "spear_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "halberd_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "pike_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "lance_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "glaive_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "staff_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "scythe_silver"))
                ))),
                new Pair<>(2, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("stone_sword"),
                        new ItemStackEntry("stone_axe"),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemstonehammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemstonedagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemstonebattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemstonenunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_stone")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_stone")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_stone")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_stone")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_stone")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_stone")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_stone")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "dagger_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "mace_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "parrying_dagger_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "battleaxe_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longsword_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "katana_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "saber_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "rapier_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "greatsword_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "hammer_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "warhammer_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "spear_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "halberd_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "pike_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "lance_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "glaive_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "staff_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "scythe_stone")),
                        // Copper
                        new ItemStackEntry(new ResourceLocation("iceandfire", "copper_sword")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "copper_sword")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemcopperhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemcopperdagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemcopperbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemcoppernunchaku")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "dagger_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "mace_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "parrying_dagger_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "battleaxe_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longsword_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "katana_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "saber_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "rapier_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "greatsword_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "hammer_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "warhammer_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "spear_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "halberd_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "pike_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "lance_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "glaive_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "staff_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "scythe_copper"))
                ))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("iron_sword"),
                        new ItemStackEntry("iron_axe"),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "tooth_dagger")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemironhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemirondagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemironbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemironnunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_iron")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_iron")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_iron")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_iron")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_iron")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_iron")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_iron")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "dagger_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "mace_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "parrying_dagger_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "battleaxe_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longsword_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "katana_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "saber_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "rapier_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "greatsword_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "hammer_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "warhammer_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "spear_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "halberd_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "pike_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "lance_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "glaive_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "staff_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "scythe_iron")),
                        // Umbrium
                        new ItemStackEntry(new ResourceLocation("defiledlands", "umbrium_sword")),
                        new ItemStackEntry(new ResourceLocation("defiledlands", "umbrium_axe")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_umbrium")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_umbrium")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_umbrium")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_umbrium")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_umbrium")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_umbrium")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_umbrium")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "dagger_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "mace_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "parrying_dagger_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "battleaxe_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "longsword_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "katana_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "saber_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "rapier_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "greatsword_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "hammer_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "warhammer_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "spear_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "halberd_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "pike_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "lance_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "glaive_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "staff_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "scythe_umbrium")),
                        // Desert Mex
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_desert_sword")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_desert_axe")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertchitinhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertchitindagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertchitinbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertchitinnunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_desert_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_desert_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_desert_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_desert_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_desert_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_desert_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_desert_myrmex")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanfire", "dagger_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "mace_desert")),
//                        new ItemStackEntry(new ResourceLocation("spartanfire", "parrying_dagger_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "battleaxe_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longsword_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "katana_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "saber_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "rapier_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "greatsword_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "hammer_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "warhammer_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "spear_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "halberd_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "pike_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "lance_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "glaive_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "staff_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "scythe_desert")),
                        // Desert Venom
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_desert_sword_venom")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertstingerhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertstingerdagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertstingerbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdesertstingernunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_desert_venom")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanfire", "dagger_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "mace_desert_venom")),
//                        new ItemStackEntry(new ResourceLocation("spartanfire", "parrying_dagger_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "battleaxe_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longsword_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "katana_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "saber_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "rapier_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "greatsword_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "hammer_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "warhammer_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "spear_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "halberd_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "pike_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "lance_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "glaive_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "staff_desert_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "scythe_desert_venom")),
                        // Jungle Mex
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_jungle_sword")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_jungle_axe")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglechitinhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglechitindagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglechitinbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglechitinnunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_jungle_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_jungle_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_jungle_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_jungle_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_jungle_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_jungle_myrmex")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_jungle_myrmex")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanfire", "dagger_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "mace_jungle")),
//                        new ItemStackEntry(new ResourceLocation("spartanfire", "parrying_dagger_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "battleaxe_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longsword_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "katana_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "saber_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "rapier_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "greatsword_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "hammer_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "warhammer_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "spear_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "halberd_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "pike_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "lance_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "glaive_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "staff_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "scythe_jungle")),
                        // Jungle Venom
                        new ItemStackEntry(new ResourceLocation("iceandfire", "myrmex_jungle_sword_venom")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglestingerhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglestingerdagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglestingerbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemjunglestingernunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_jungle_venom")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanfire", "dagger_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "mace_jungle_venom")),
//                        new ItemStackEntry(new ResourceLocation("spartanfire", "parrying_dagger_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "battleaxe_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longsword_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "katana_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "saber_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "rapier_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "greatsword_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "hammer_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "warhammer_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "spear_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "halberd_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "pike_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "lance_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "glaive_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "staff_jungle_venom")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "scythe_jungle_venom"))
                ))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("diamond_sword"),
                        new ItemStackEntry("diamond_axe"),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "troll_weapon.column")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "troll_weapon.axe")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "troll_weapon.column_frost")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "troll_weapon.column_forest")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "troll_weapon.trunk")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "troll_weapon.hammer")),
                        new ItemStackEntry(new ResourceLocation("iceandfire", "troll_weapon.trunk_frost")),
                        new ItemStackEntry(new ResourceLocation("mod_lavacow", "bonesword")),
                        // Better Survival
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdiamondhammer")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdiamonddagger")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdiamondbattleaxe")),
                        new ItemStackEntry(new ResourceLocation("mujmajnkraftsbettersurvival", "itemdiamondnunchaku")),
                        // Nanos Arsenal
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "morning_star_diamond")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "scimitar_diamond")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "claws_diamond")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "flail_diamond")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "battering_ram_diamond")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "ball_and_chain_diamond")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "double_bladed_scimitar_diamond")),
                        // Spartan Weaponry
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "dagger_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "mace_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "parrying_dagger_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "battleaxe_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longsword_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "katana_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "saber_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "rapier_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "greatsword_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "hammer_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "warhammer_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "spear_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "halberd_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "pike_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "lance_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "glaive_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "staff_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "scythe_diamond"))
                )))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Offhand")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleOffhand = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_basic_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_tower_wood"))
                ))),
                new Pair<>(1, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_basic_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_tower_gold")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_basic_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_tower_silver"))
                ))),
                new Pair<>(2, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("shield"),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_basic_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_tower_stone")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_basic_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_tower_copper"))
                ))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_basic_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_tower_iron"))
                ))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("totem_of_undying"),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_basic_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanshields", "shield_tower_diamond")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "tartsy_shield"))
                ))),
                new Pair<>(5, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry("totem_of_undying", 4, 0),
                        new ItemStackEntry(new ResourceLocation("bountifulbaubles", "shieldcobalt")),
                        new ItemStackEntry(new ResourceLocation("nanonaitors_arsenal", "sun_war_bulwark"))
                )))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found.",
                "    If the mob spawned holding any kind of Bow, it will use this list."
        })
        @Config.Name("Possible Bow")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleBow = ConfigHandler.linkedMapOf(Arrays.asList(
//                new Pair<>(0, new ArrayList<>(Arrays.asList(
//                        new ItemStackEntry("bow")
//                ))),
                new Pair<>(1, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longbow_wood")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "crossbow_wood"))
                ))),
                new Pair<>(2, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longbow_leather")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "crossbow_leather")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longbow_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "crossbow_silver")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longbow_copper")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "crossbow_copper"))
                ))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longbow_iron")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "crossbow_iron")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "longbow_umbrium")),
                        new ItemStackEntry(new ResourceLocation("spartandefiled", "crossbow_umbrium"))
                ))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "longbow_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanweaponry", "crossbow_diamond")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longbow_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "crossbow_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longbow_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "crossbow_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longbow_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "crossbow_desert")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "longbow_jungle")),
                        new ItemStackEntry(new ResourceLocation("spartanfire", "crossbow_jungle"))
                )))
        ));
    }

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

    public static Map<Integer, List<ItemStack>> getTieredItemsForSlot(EntityEquipmentSlot slot) {
        switch (slot) {
            case HEAD: return  HELMET_BY_TIER;
            case CHEST: return CHESTPLATE_BY_TIER;
            case LEGS: return LEGGINGS_BY_TIER;
            case FEET: return BOOTS_BY_TIER;
            case OFFHAND: return OFFHAND_BY_TIER;
            case MAINHAND: return MAINHAND_BY_TIER;
        }
        return Collections.emptyMap();
    }

    public void init() {
        BLIGHT_POTIONS.clear();

        ConfigHandler.mob.blight.potion.blightPotionEffects.forEach(potionEntry -> {
            Potion potion = ForgeRegistries.POTIONS.getValue(potionEntry.id);
            if(potion != null)
                BLIGHT_POTIONS.putIfAbsent(potion, potionEntry);
        });

        initTieredItemMap(HELMET_BY_TIER, ConfigHandler.mob.blight.equipment.possibleHelmet);
        initTieredItemMap(CHESTPLATE_BY_TIER, ConfigHandler.mob.blight.equipment.possibleChestplate);
        initTieredItemMap(LEGGINGS_BY_TIER, ConfigHandler.mob.blight.equipment.possibleLeggings);
        initTieredItemMap(BOOTS_BY_TIER, ConfigHandler.mob.blight.equipment.possibleBoots);
        initTieredItemMap(MAINHAND_BY_TIER, ConfigHandler.mob.blight.equipment.possibleMainhand);
        initTieredItemMap(OFFHAND_BY_TIER, ConfigHandler.mob.blight.equipment.possibleOffhand);
        initTieredItemMap(BOW_BY_TIER, ConfigHandler.mob.blight.equipment.possibleBow);

        initRangedResultMap(RANDOM_ENCHANT_BY_DIFFICULTY, ConfigHandler.mob.blight.enchantments.possibleRandomEnchantments);
        initRangedResultMap(COMMON_TIER_BY_DIFFICULTY, ConfigHandler.mob.blight.equipment.highestCommonTiers);
        initRangedResultMap(TIER_UP_CHANCE_BY_DIFFICULTY, ConfigHandler.mob.blight.equipment.tierUpChances);
        initRangedResultMap(ARMOR_CHANCE_BY_DIFFICULTY, ConfigHandler.mob.blight.equipment.armorChances);
        initRangedResultMap(HAND_CHANCE_BY_DIFFICULTY, ConfigHandler.mob.blight.equipment.handChances);
    }

    private static void initTieredItemMap(Map<Integer, List<ItemStack>> map, Map<Integer, ArrayList<ItemStackEntry>> config) {
        map.clear();
        config.forEach((tier, itemIDs) -> {
            map.putIfAbsent(tier, new ArrayList<>());
            itemIDs.forEach(configEntry -> {
                Item item = ForgeRegistries.ITEMS.getValue(configEntry.id);
                if(item != null) {
                    map.get(tier).add(new ItemStack(item, configEntry.amount, configEntry.meta, configEntry.getForgeCaps()));
                }
                else if(ConfigHandler.debug.logConfig)
                    ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Tiered Item Map unable to find in Registry: {}", configEntry.id);
            });
        });
    }

    public static <V> void initRangedResultMap(RangeMap<Double, V> map, Map<Double, V> config) {
        map.clear();
        config.forEach(((startRange, value) -> {
            map.put(Range.atLeast(startRange), value);
        }));
    }
}
