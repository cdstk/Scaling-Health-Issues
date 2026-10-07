package scalinghealthissues.util;

import net.minecraft.util.DamageSource;

public interface IDamageSources_NonLethalMixin {

    static boolean isNonLethal(DamageSource damageSource) {
        if(damageSource.equals(DamageSource.OUT_OF_WORLD)) {
            return false;
        }

        if(damageSource instanceof IDamageSources_NonLethalMixin) {
            return ((IDamageSources_NonLethalMixin) damageSource).scalingHealthIssues$isNonLethal();
        }
        return false;
    }

    boolean scalingHealthIssues$isNonLethal();
    DamageSource scalingHealthIssues$setNonLethal();
}
