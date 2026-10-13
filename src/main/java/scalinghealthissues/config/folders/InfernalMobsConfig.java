package scalinghealthissues.config.folders;

import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.data.ConfigEnum;
import scalinghealthissues.config.data.ValueRangeEntry;
import scalinghealthissues.util.Pair;

import java.util.Arrays;
import java.util.Map;

public class InfernalMobsConfig {

    @Config.Comment({
            "The minimum number of Modifiers the Infernal killed must have.",
            "\tInfernal - 11 or more",
            "\tUltra - 6 to 10",
            "\tRare - 5 or less"
    })
    @Config.Name("Infernal Killed by Player Message Modifier Count")
    @Order(0)
    public int killedByPlayerModifiers = 11;

    @Config.Comment({
            "Allows an item from the Infernal drops config to replace an existing item set on natural spawn.",
            "    Mobs like Skeletons and Vindicators will be able to have a different mainhand",
            "    Although an item randomly provided by another mod can be replaced"
    })
    @Config.Name("Replace Existing Equipment On Spawn")
    @Order(1)
    public boolean replaceExistingEquipment = true;

    @Config.Comment({
            "Defines the value range for Equipment Enchantability value.",
            "    These values are used for worn Equipment and loot dropped.",
            "An undefined entry uses Infernal Mob's default of using the Item's Material base value",
            "    which is often lower than Vanilla naturally spawned enchanted equipped mobs of [5, 22]"
    })
    @Config.Name("Infernal Equipment Enchantability")
    @Order(2)
    public Map<ConfigEnum.InfernalTier, ValueRangeEntry> infernalEnchantability = ConfigHandler.linkedMapOf(Arrays.asList(
            new Pair<>(ConfigEnum.InfernalTier.RARE, new ValueRangeEntry(10, 20)),
            new Pair<>(ConfigEnum.InfernalTier.ULTRA, new ValueRangeEntry(20, 30)),
            new Pair<>(ConfigEnum.InfernalTier.INFERNAL, new ValueRangeEntry(30, 50))
    ));

    @Config.Comment({
            "Determines where the Enchanted Book Enchantsments Pool comes from.",
            "Intended to be used to choose if it should interact with RLCraft Luckified or the So Many Enchantments blacklists.",
            "    ENCHANT_RANDOMLY_LOOT_FUNC - The minecraft:enchant_randomly loot function",
            "    FORGE_REGISTRY - All loaded enchantments",
            "    LIBRARIAN_VILLAGER - Enchanted Books sold by Librarians",
            "    VANILLA_REGISTRY - Only Vanilla enchantments",
            "Infernal Mobs originally used VANILLA_REGISTRY"
    })
    @Config.Name("Enchanted Book Loot Enchantment Pool")
    @Config.RequiresMcRestart
    @Order(3)
    public ConfigEnum.SingleEnchantmentList bookEnchantmentList = ConfigEnum.SingleEnchantmentList.ENCHANT_RANDOMLY;

    @Config.Comment({
            "Determines where the Enchanted Equipment Enchantsments Pool comes from.",
            "Intended to be used to choose if it should interact with RLCraft Luckified or the So Many Enchantments blacklists.",
            "    ENCHANTMENT_HELPER - Directly use EnchantmentHelper.addRandomEnchantment",
            "    ENCHANT_WITH_LEVELS - The minecraft:enchant_with_levels loot function",
            "    FORGE_REGISTRY - Chooses up to 3 from the top after using EnchantmentHelper.addRandomEnchantment",
            "Infernal Mobs originally used FORGE_REGISTRY "
    })
    @Config.Name("Enchanted Equipment Loot Enchantment Pool")
    @Order(4)
    public ConfigEnum.MultiEnchantmentList equipmentEnchantmentList = ConfigEnum.MultiEnchantmentList.ENCHANT_WITH_LEVELS;
}
