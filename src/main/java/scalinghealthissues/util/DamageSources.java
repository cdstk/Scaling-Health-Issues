package scalinghealthissues.util;

import net.minecraft.util.DamageSource;

public class DamageSources {

    // TODO Mixin based flag for non fatal (Scaling Health and First Aid)

    // Copy of magic, unique instance for handling
    public static final DamageSource POISON = new DamageSource("magic").setDamageBypassesArmor().setMagicDamage();
}
