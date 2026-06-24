package scalinghealthissues.mixin.scalinghealth;

import net.minecraft.entity.EntityLivingBase;
import net.silentchaos512.scalinghealth.entity.EntityBlightFire;
import net.silentchaos512.scalinghealth.event.BlightHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlightHandler.class)
public interface BlightHandler_InvokerMixin {

    @Invoker(value = "spawnBlightFire", remap = false)
    static void scalingHealthIssues$invokeSpawnBlightFire(EntityLivingBase blight) {
        throw new AssertionError();
    }

    @Invoker(value = "getBlightFire", remap = false)
    static EntityBlightFire scalingHealthIssues$invokeGetBlightFire(EntityLivingBase blight) {
        throw new AssertionError();
    }

    @Invoker(value = "applyBlightPotionEffects", remap = false)
    static void scalingHealthIssues$invokeApplyBlightPotionEffects(EntityLivingBase entityLiving) {
        throw new AssertionError();
    }
}
