package scalinghealthissues.mixin.scalinghealth.packetfix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DifficultyHandler.class)
public abstract class DifficultyHandler_PacketFixMixin {

    @WrapOperation(
            method = "makeEntityBlight",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToAllAround(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V"),
            remap = false
    )
    private void scalingHealthIssues_shDifficultyHandler_makeEntityBlightUpdateTracking(SimpleNetworkWrapper instance, IMessage message, NetworkRegistry.TargetPoint point, Operation<Void> original, EntityLiving entityLiving){
        NetworkHandler.INSTANCE.sendToAllTracking(message, entityLiving);
    }
}
