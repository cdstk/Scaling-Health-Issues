package scalinghealthissues.util;

import net.minecraft.util.DamageSource;

public class DamageSources {

    // Copy of magic, unique instance for handling
    public static final DamageSource POISON = new DamageSource("magic").setDamageBypassesArmor().setMagicDamage();

    public static void postInitDamageSourceFlags() {
        if(POISON instanceof IDamageSources_NonLethalMixin)
            ((IDamageSources_NonLethalMixin) POISON).scalingHealthIssues$setNonLethal();
    }
}
