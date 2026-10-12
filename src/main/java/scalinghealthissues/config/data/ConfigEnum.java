package scalinghealthissues.config.data;

public class ConfigEnum {

    public enum ListType {
        BLACKLIST,
        WHITELIST
    }

    public enum SingleEnchantmentList {
        ENCHANT_RANDOMLY,
        FORGE_REGISTRY,
        LIBRARIAN_VILLAGER,
        VANILLA_REGISTRY
    }

    public enum MultiEnchantmentList {
        ENCHANTMENT_HELPER,
        ENCHANT_WITH_LEVELS,
        FORGE_REGISTRY
    }

    public enum InfernalTier {
        RARE,
        ULTRA,
        INFERNAL
    }
}
