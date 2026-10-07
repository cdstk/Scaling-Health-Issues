package scalinghealthissues.mixin.modcompat.firstaid;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import scalinghealthissues.compat.handlers.FirstAidHandler;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.util.IAbstractDamageablePart_NonLethalMixin;
import scalinghealthissues.util.IDamageSources_NonLethalMixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Collectors;

@Mixin(DamageDistribution.class)
public abstract class DamageDistribution_LethalityMixin {

    @Shadow(remap = false) protected abstract float minHealth(@Nonnull EntityPlayer player, @Nonnull AbstractDamageablePart part);

    @Inject(
            method = "handleDamageTaken",
            at = @At("HEAD"),
            remap = false
    )
    private static void scalingHealthIssues_firstAidDamageDistribution_handleDamageTakenLethalityGet(IDamageDistribution damageDistribution, AbstractPlayerDamageModel damageModel, float damage, EntityPlayer player, DamageSource source, boolean addStat, boolean redistributeIfLeft, CallbackInfoReturnable<Float> cir){
        // Non Lethal Damage Source only affects min health
        if(IDamageSources_NonLethalMixin.isNonLethal(source)) {
            scalingHealthIssues$setPartsLethality(damageModel, true);
        }

        // Affects min health and ignores part -> part remainder handling
        if(FirstAidHandler.morphineBuffActive(player)) {
            if(player.getHealth() >= player.getMaxHealth()) {
                scalingHealthIssues$setPartsLethality(damageModel, true);
            }
            scalingHealthIssues$setPartsIgnoreRemainder(damageModel, true);
        }
    }

    @WrapOperation(
            method = "handleDamageTaken",
            at = @At(value = "NEW", target = "(Lnet/minecraft/entity/player/EntityPlayer;Lichttt/mods/firstaid/api/damagesystem/AbstractPlayerDamageModel;Lichttt/mods/firstaid/api/damagesystem/AbstractPlayerDamageModel;Lnet/minecraft/util/DamageSource;F)Lichttt/mods/firstaid/api/event/FirstAidLivingDamageEvent;"),
            remap = false
    )
    private static FirstAidLivingDamageEvent scalingHealthIssues_firstAidDamageDistribution_handleDamageTakenLethalitySyncEvent(EntityPlayer entity, AbstractPlayerDamageModel afterDamageDone, AbstractPlayerDamageModel beforeDamageDone, DamageSource source, float undistributedDamage, Operation<FirstAidLivingDamageEvent> original){
        // Send updated parts to Event
        afterDamageDone.forEach(part -> {
            if(IAbstractDamageablePart_NonLethalMixin.isNonLethal(part)) {
                IAbstractDamageablePart_NonLethalMixin.setNonLethal(beforeDamageDone.getFromEnum(part.part), true);
            }
            if(IAbstractDamageablePart_NonLethalMixin.shouldIgnoreRemainder(part)) {
                IAbstractDamageablePart_NonLethalMixin.setIgnoreRemainder(beforeDamageDone.getFromEnum(part.part), true);
            }
        });

        FirstAidLivingDamageEvent result = original.call(entity, afterDamageDone, beforeDamageDone, source, undistributedDamage);
        scalingHealthIssues$setPartsLethality(afterDamageDone, false);
        scalingHealthIssues$setPartsIgnoreRemainder(afterDamageDone, false);
        return result;
    }

    @WrapMethod(
            method = "distributeDamageOnParts",
            remap = false
    )
    private float scalingHealthIssues_firstAidDamageDistribution_distributeDamageOnPartsSturdyActivated(float damage, AbstractPlayerDamageModel damageModel, EnumPlayerPart[] enumParts, EntityPlayer player, boolean addStat, Operation<Float> original){
        boolean sendMessage = player.getHealth() >= player.getMaxHealth() && FirstAidHandler.morphineBuffActive(player);
        float result = original.call(damage, damageModel, enumParts, player, addStat);

        if(sendMessage) {
            sendMessage = false;
            for(AbstractDamageablePart part : Arrays.stream(enumParts).map(damageModel::getFromEnum).collect(Collectors.toList())) {
                if(IAbstractDamageablePart_NonLethalMixin.isNonLethal(part)) {
                    if(part.currentHealth == this.minHealth(player, part)) {
                        sendMessage = true;
                    }
                }
            }
        }

        if(sendMessage) {
            ITextComponent activateText = new TextComponentTranslation("scalinghealthissues.firstaid.sturdy.activated");
            TextFormatting format = TextFormatting.getValueByName(ConfigHandler.compat.firstAid.morphineMessageFormat);
            if(format != null) {
                activateText.getStyle().setColor(format);
            }
            player.sendMessage(activateText);

            SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(ConfigHandler.compat.firstAid.morphineSoundEvent);
            if(soundEvent != null) {
                player.world.playSound(null, player.posX, player.posY, player.posZ, soundEvent, SoundCategory.PLAYERS, 0.8F, (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.1F + 0.8F);
            }
        }
        return result;
    }

    @WrapOperation(
            method = "distributeDamageOnParts",
            at = @At(value = "INVOKE", target = "Lichttt/mods/firstaid/api/damagesystem/AbstractDamageablePart;damage(FLnet/minecraft/entity/player/EntityPlayer;ZF)F"),
            remap = false
    )
    private float scalingHealthIssues_firstAidDamageDistribution_distributeDamageOnPartsIgnoreRemainder(AbstractDamageablePart part, float amount, @Nullable EntityPlayer player, boolean applyDebuff, float minHealth, Operation<Float> original){
        boolean canIgnoreRemainder = false;
        float result;
        if(part.currentHealth > minHealth) {
            if(IAbstractDamageablePart_NonLethalMixin.shouldIgnoreRemainder(part)) {
                canIgnoreRemainder = true;
            }
        }

        result = original.call(part, amount, player, applyDebuff, minHealth);

        return canIgnoreRemainder ? 0.0F : result;
    }

    @Unique
    private static void scalingHealthIssues$setPartsLethality(AbstractPlayerDamageModel damageModel, boolean nonLethal){
        damageModel.forEach(part -> IAbstractDamageablePart_NonLethalMixin.setNonLethal(part, nonLethal));
    }

    @Unique
    private static void scalingHealthIssues$setPartsIgnoreRemainder(AbstractPlayerDamageModel damageModel, boolean remainder){
        damageModel.forEach(part -> IAbstractDamageablePart_NonLethalMixin.setIgnoreRemainder(part, remainder));
    }
}
