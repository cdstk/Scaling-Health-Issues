package scalinghealthissues.util;

import net.minecraft.util.DamageSource;

public interface IDamageSources_NonLethalMixin {

    boolean scalingHealthIssues$isNonLethal();
    DamageSource scalingHealthIssues$setNonLethal();
}
