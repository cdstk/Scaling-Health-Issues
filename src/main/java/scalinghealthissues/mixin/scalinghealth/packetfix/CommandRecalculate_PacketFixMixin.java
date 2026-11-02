package scalinghealthissues.mixin.scalinghealth.packetfix;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.silentchaos512.scalinghealth.command.CommandRecalculate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandRecalculate.class)
public abstract class CommandRecalculate_PacketFixMixin {

    @WrapWithCondition(
            method = "recalculateAllEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToAllAround(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V"),
            remap = false
    )
    private static boolean scalingHealthIssues_shCommandRecalculate_recalculateAllEntitiesPacketTracking(SimpleNetworkWrapper instance, IMessage message, NetworkRegistry.TargetPoint point, @Local EntityLivingBase entity){
        instance.sendToAllTracking(message, entity);
        return false;
    }
}
