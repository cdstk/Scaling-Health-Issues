package scalinghealthissues.mixin.scalinghealth;

import net.minecraft.entity.EntityLivingBase;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DifficultyHandler.class)
public interface DifficultyHandler_InvokerMixin {

    @Invoker(value = "process", remap = false)
    boolean scalingHealthIssues$invokeProcess(EntityLivingBase entity);
}
