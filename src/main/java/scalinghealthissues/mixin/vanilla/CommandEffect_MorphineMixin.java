package scalinghealthissues.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.command.CommandEffect;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import scalinghealthissues.compat.handlers.FirstAidHandler;

@Mixin(CommandEffect.class)
public abstract class CommandEffect_MorphineMixin {

    @WrapMethod(
            method = "execute"
    )
    private void scalingHealthIssues_vanillaCommandEffect_executeMorphineInternal(MinecraftServer server, ICommandSender sender, String[] args, Operation<Void> original){
        FirstAidHandler.morphineAppliedInternally = true;
        FirstAidHandler.morphineRemovedInternally = true;
        original.call(server, sender, args);
        FirstAidHandler.morphineAppliedInternally = false;
        FirstAidHandler.morphineRemovedInternally = false;
    }
}
