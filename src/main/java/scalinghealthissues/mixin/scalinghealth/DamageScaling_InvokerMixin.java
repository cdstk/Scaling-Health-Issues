package scalinghealthissues.mixin.scalinghealth;

import net.minecraft.entity.EntityLivingBase;
import net.silentchaos512.scalinghealth.event.DamageScaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(DamageScaling.class)
public interface DamageScaling_InvokerMixin {

    @Accessor(value = "genericScale", remap = false)
    float scalingHealthIssues$accessGenericScale();

    @Accessor(value = "affectHostileMobs", remap = false)
    boolean scalingHealthIssues$accessAffectHostileMobs();

    @Accessor(value = "affectPassiveMobs", remap = false)
    boolean scalingHealthIssues$accessAffectPassiveMobss();

    @Accessor(value = "scalingMap", remap = false)
    Map<String, Float> scalingHealthIssues$accessScalingMap();

    @Invoker(value = "getEffectScale", remap = false)
    float scalingHealthIssues$invokeGetEffectScale(EntityLivingBase entity);

    @Invoker(value = "makeSane", remap = false)
    float scalingHealthIssues$invokeMakeSane(float scaledAmount);
}