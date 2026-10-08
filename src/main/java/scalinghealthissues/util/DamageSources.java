package scalinghealthissues.util;

import net.minecraft.util.DamageSource;
import scalinghealthissues.mixininterface.IDamageSources_NonLethalMixin;

public class DamageSources {

    // Copy of magic, unique instance for handling
    // This is the only source with a First Aid explicitly mapped DamageDistribution
    public static final DamageSource POISON = new DamageSource("magic").setDamageBypassesArmor().setMagicDamage();

    // Mod Compat References
    // These keep original First Aid DamageDistribution but can be modified to use Mixin'd Non-Lethal
    public static DamageSource LYCANITES_BLEED = null;
    public static DamageSource DEFILEDLANDS_BLEED = null;
    public static DamageSource SRP_BLEED = null;

    public static void postInitDamageSourceFlags() {
        if(POISON instanceof IDamageSources_NonLethalMixin)
            ((IDamageSources_NonLethalMixin) POISON).scalingHealthIssues$setNonLethal();
    }
}
