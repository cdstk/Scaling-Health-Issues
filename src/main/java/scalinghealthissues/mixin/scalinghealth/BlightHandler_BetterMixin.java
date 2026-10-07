package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.silentchaos512.lib.util.ChatHelper;
import net.silentchaos512.scalinghealth.event.BlightHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.config.folders.BlightConfig;

@Mixin(BlightHandler.class)
public abstract class BlightHandler_BetterMixin {

    @Inject(
            method = "applyBlightPotionEffects",
            at = @At("TAIL"),
            remap = false
    )
    private static void scalingHealthIssues_shBlightHandler_applyBlightPotionEffectsConfig(EntityLivingBase entityLiving, CallbackInfo ci){
        BlightConfig.BLIGHT_POTIONS.forEach((potion, config) -> entityLiving.addPotionEffect(new PotionEffect(potion, config.duration, config.amplifier, true, false)));
    }

    @WrapOperation(
            method = "onBlightKilled",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/lib/util/ChatHelper;translate(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;[Ljava/lang/Object;)V"),
            remap = false
    )
    private void scalingHealthIssues_shBlightHandler_onBlightKilledDifficulty(EntityPlayer player, String translationKey, Object[] args, Operation<Void> original, LivingDeathEvent event, @Local(name = "blight") EntityLivingBase blight){
        // Send the vanilla death message with mixin'd format modifications
        ITextComponent deathMessage = blight.getCombatTracker().getDeathMessage();
        player.sendMessage(deathMessage);
    }

    @WrapOperation(
            method = "onBlightKilled",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/DamageSource;getDeathMessage(Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_shBlightHandler_onBlightKilledBlightNewMessage(DamageSource instance, EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent deathMessage = original.call(instance, entity);
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
        return false; // Should not be called because of the TextComponentTranslation instance check, but just in case
    }
}
