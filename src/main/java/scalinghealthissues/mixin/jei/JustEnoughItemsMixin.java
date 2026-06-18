package scalinghealthissues.mixin.jei;

import mezz.jei.JustEnoughItems;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.ScalingHealthIssues;

@Mixin(JustEnoughItems.class)
public abstract class JustEnoughItemsMixin {

    @Inject(
            method = "init",
            at = @At("HEAD"),
            remap = false
    )
    public void scalinghealthissues_jeiJustEnoughItems_init(FMLInitializationEvent event, CallbackInfo ci) {
        ScalingHealthIssues.LOGGER.log(Level.INFO, "JEI Init");
    }
}