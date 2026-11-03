package scalinghealthissues.mixininterface;

import net.minecraft.util.DamageSource;
import scalinghealthissues.config.ConfigHandler;

public interface IDamageSources_NonLethalMixin {

    static boolean isNonLethal(DamageSource damageSource) {
        if(damageSource.equals(DamageSource.OUT_OF_WORLD)) {
            return false;
        }

        if(ConfigHandler.dmgScale.nonLethalDamageSources.contains(damageSource.getDamageType())) {
            return true;
        }

        if(damageSource instanceof IDamageSources_NonLethalMixin) {
            return ((IDamageSources_NonLethalMixin) damageSource).scalingHealthIssues$isNonLethal();
        }
        return false;
    }

    boolean scalingHealthIssues$isNonLethal();
    DamageSource scalingHealthIssues$setNonLethal();
}
