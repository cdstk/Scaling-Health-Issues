package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.scalinghealth.event.BlightHandler;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.handlers.BetterDifficultyHandler;

@Mixin(BlightHandler.class)
public abstract class BlightHandler_BetterMixin {

    @WrapOperation(
            method = "onBlightKilled",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/lib/util/ChatHelper;translate(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;[Ljava/lang/Object;)V"),
            remap = false
    )
    private void scalingHealthIssues_shBlightHandler_onBlightKilledDifficulty(EntityPlayer player, String translationKey, Object[] args, Operation<Void> original, LivingDeathEvent event, @Local(name = "blight") EntityLivingBase blight, @Local(name = "actualKiller") EntityLivingBase actualKiller){
        ITextComponent deathMessage = BetterDifficultyHandler.prependBlightText(event.getSource().getDeathMessage(blight), blight);

        BetterDifficultyHandler.addDifficultyHoverText(deathMessage, blight, actualKiller);

        player.sendMessage(deathMessage);
    }

    @WrapOperation(
            method = "onBlightKilled",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/DamageSource;getDeathMessage(Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_shBlightHandler_onBlightKilledBlightNewMessage(DamageSource instance, EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent deathMessage = original.call(instance, entity);
        deathMessage = BetterDifficultyHandler.prependBlightText(deathMessage, entity);
        for (EntityPlayer p : entity.world.getPlayers(EntityPlayer.class, e -> true))
            ChatHelper.sendMessage(p, deathMessage);
        return deathMessage;
    }

    @WrapWithCondition(
            method = "onBlightKilled",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/lib/util/ChatHelper;sendMessage(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/text/ITextComponent;)V"),
            remap = false
    )
    private boolean scalingHealthIssues_shBlightHandler_onBlightKilledBlightOriginalMessage(EntityPlayer player, ITextComponent component, @Local(argsOnly = true) LivingDeathEvent event, @Local EntityLivingBase blight){
        return false;
    }

    @WrapOperation(
            method = "onBlightUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendToAllAround(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V"),
            remap = false
    )
    private void scalingHealthIssues_shBlightHandler_onBlightUpdatePacketTracking(SimpleNetworkWrapper instance, IMessage message, NetworkRegistry.TargetPoint point, Operation<Void> original, @Local EntityLivingBase entityLiving){
        NetworkHandler.INSTANCE.sendToAllTracking(message, entityLiving);
    }
}
