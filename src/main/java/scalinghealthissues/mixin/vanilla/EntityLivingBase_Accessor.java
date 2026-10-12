package scalinghealthissues.mixin.vanilla;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLivingBase.class)
public interface EntityLivingBase_Accessor {

    @Accessor("attackingPlayer")
    EntityPlayer scalingHealthIssues$getAttackingPlayer();

    @Accessor("recentlyHit")
    int scalingHealthIssues$getRecentlyHit();
}
