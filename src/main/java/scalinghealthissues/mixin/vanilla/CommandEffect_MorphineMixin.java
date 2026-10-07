package scalinghealthissues.mixin.vanilla;

import net.minecraft.command.CommandEffect;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.Wrapper.ScalingHealthWrapper;

@Mixin(CommandEffect.class)
public abstract class CommandEffect_MorphineMixin {

    @Inject(
            method = "execute",
            at = @At("HEAD")
    )
    private void scalingHealthIssues_vanillaCommandEffect_executeMorphineInternal(MinecraftServer server, ICommandSender sender, String[] args, CallbackInfo ci){
        ScalingHealthWrapper.setMorphineInternal();
    }
}
