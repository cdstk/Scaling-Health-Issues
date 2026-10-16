package scalinghealthissues.config.folders;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.data.ConfigEnum;
import scalinghealthissues.config.data.EnchantWithLevelsEntry;
import scalinghealthissues.config.data.ItemStackEntry;
import scalinghealthissues.config.data.PotionConfigEntry;
import scalinghealthissues.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                new Pair<>(0D, new EnchantWithLevelsEntry(20, 30, false)),
                new Pair<>(30D, new EnchantWithLevelsEntry(30, 40, true))
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
        public int maxExpectedTier = 4;

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
                new Pair<>(100D, 2),
                new Pair<>(200D, 3),
                new Pair<>(300D, 4)
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
                new Pair<>(100D, 0.25F),
                new Pair<>(200D, 0.5F),
                new Pair<>(300D, 0.75F)
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
                new Pair<>(0D, 0.25F),
                new Pair<>(30D, 0.5F),
                new Pair<>(60D, 0.75F),
                new Pair<>(90D, 1.0F)
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Helmet")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleHelmet = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Collections.singletonList(new ItemStackEntry("leather_helmet")))),
                new Pair<>(1, new ArrayList<>(Collections.singletonList(new ItemStackEntry("golden_helmet")))),
                new Pair<>(2, new ArrayList<>(Collections.singletonList(new ItemStackEntry("chainmail_helmet")))),
                new Pair<>(3, new ArrayList<>(Collections.singletonList(new ItemStackEntry("iron_helmet")))),
                new Pair<>(4, new ArrayList<>(Collections.singletonList(new ItemStackEntry("diamond_helmet"))))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Chestplate")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleChestplate = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Collections.singletonList(new ItemStackEntry("leather_chestplate")))),
                new Pair<>(1, new ArrayList<>(Collections.singletonList(new ItemStackEntry("golden_chestplate")))),
                new Pair<>(2, new ArrayList<>(Collections.singletonList(new ItemStackEntry("chainmail_chestplate")))),
                new Pair<>(3, new ArrayList<>(Collections.singletonList(new ItemStackEntry("iron_chestplate")))),
                new Pair<>(4, new ArrayList<>(Collections.singletonList(new ItemStackEntry("diamond_chestplate"))))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Leggings")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleLeggings = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Collections.singletonList(new ItemStackEntry("leather_leggings")))),
                new Pair<>(1, new ArrayList<>(Collections.singletonList(new ItemStackEntry("golden_leggings")))),
                new Pair<>(2, new ArrayList<>(Collections.singletonList(new ItemStackEntry("chainmail_leggings")))),
                new Pair<>(3, new ArrayList<>(Collections.singletonList(new ItemStackEntry("iron_leggings")))),
                new Pair<>(4, new ArrayList<>(Collections.singletonList(new ItemStackEntry("diamond_leggings"))))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Boots")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleBoots = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(0, new ArrayList<>(Collections.singletonList(new ItemStackEntry("leather_boots")))),
                new Pair<>(1, new ArrayList<>(Collections.singletonList(new ItemStackEntry("golden_boots")))),
                new Pair<>(2, new ArrayList<>(Collections.singletonList(new ItemStackEntry("chainmail_boots")))),
                new Pair<>(3, new ArrayList<>(Collections.singletonList(new ItemStackEntry("iron_boots")))),
                new Pair<>(4, new ArrayList<>(Collections.singletonList(new ItemStackEntry("diamond_boots"))))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found.",
                "    If the mob spawned holding any kind of Bow, it will use the \"Possible Bow\" list instead."
        })
        @Config.Name("Possible Mainhand")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleMainhand = ConfigHandler.linkedMapOf(Arrays.asList(
//                new Pair<>(0, new ArrayList<>(Arrays.asList(new ItemStackEntry("wooden_sword"), new ItemStackEntry("wooden_axe")))),
//                new Pair<>(1, new ArrayList<>(Arrays.asList(new ItemStackEntry("golden_sword"), new ItemStackEntry("golden_axe")))),
//                new Pair<>(2, new ArrayList<>(Arrays.asList(new ItemStackEntry("stone_sword"), new ItemStackEntry("stone_axe")))),
                new Pair<>(3, new ArrayList<>(Arrays.asList(new ItemStackEntry("iron_sword"), new ItemStackEntry("iron_axe")))),
                new Pair<>(4, new ArrayList<>(Arrays.asList(new ItemStackEntry("diamond_sword"), new ItemStackEntry("diamond_axe"))))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found."
        })
        @Config.Name("Possible Offhand")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleOffhand = ConfigHandler.linkedMapOf(Arrays.asList(
                new Pair<>(2, new ArrayList<>(Collections.singletonList(new ItemStackEntry("shield")))),
                new Pair<>(4, new ArrayList<>(Collections.singletonList(new ItemStackEntry("totem_of_undying"))))
        ));

        @Config.Comment({
                "Possible Equipment per Tier",
                "A random tier is chosen on spawn and then a random item is selected.",
                "If no list is set for a tier, lower tiers are checked until one or none are found.",
                "    If the mob spawned holding any kind of Bow, it will use this list."
        })
        @Config.Name("Possible Bow")
        public Map<Integer, ArrayList<ItemStackEntry>> possibleBow = ConfigHandler.linkedMapOf(Collections.singletonList(
                new Pair<>(0, new ArrayList<>(Collections.singletonList(new ItemStackEntry("bow"))))
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
                if(item != null)
                    map.get(tier).add(new ItemStack(item, configEntry.amount, configEntry.meta, configEntry.getForgeCaps()));
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
