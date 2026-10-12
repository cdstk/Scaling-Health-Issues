package scalinghealthissues.mixin.significantdamagetracker.firstaid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import scalinghealthissues.compat.handlers.FirstAidHandler;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.network.GuiHandler;

import javax.annotation.Nonnull;

@Mixin(DamageDistribution.class)
public abstract class DamageDistribution_DamageTrackerMixin {

    @Inject(
            method = "handleDamageTaken",
            at = @At("HEAD"),
            remap = false
    )
    private static void scalingHealthIssues_firstAidDamageDistribution_handleDamageTakenSignificantDamagePre(IDamageDistribution damageDistribution, AbstractPlayerDamageModel damageModel, float damage, EntityPlayer player, DamageSource source, boolean addStat, boolean redistributeIfLeft, CallbackInfoReturnable<Float> cir){
        FirstAidHandler.sturdyActivated = false;
        FirstAidHandler.brokenArmor.clear();
        player.getArmorInventoryList().forEach(armor -> {
            if(!armor.isEmpty()) {
                FirstAidHandler.brokenArmor.put(EntityLiving.getSlotForItemStack(armor), armor.getDisplayName());
            }
        });
    }

    @WrapOperation(
            method = "handleDamageTaken",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z"),
            remap = false
    )
    private static boolean scalingHealthIssues_firstAidDamageDistribution_handleDamageTakenSignificantDamage(EventBus instance, Event event, Operation<Boolean> original, @Local(argsOnly = true) @Nonnull EntityPlayer player, @Local(argsOnly = true) @Nonnull DamageSource source){
        /**
         * Pre
         */
        FirstAidHandler.undershirtActivated = player.getEntityData().getInteger("skillable:UndershirtCD") <= 0;
        boolean result = original.call(instance, event);
        /**
         * Post
         */
        FirstAidHandler.undershirtActivated = FirstAidHandler.undershirtActivated && player.getEntityData().getInteger("skillable:UndershirtCD") > 0;
        player.getArmorInventoryList().forEach(armor -> {
            if(!armor.isEmpty()) {
                FirstAidHandler.brokenArmor.remove(EntityLiving.getSlotForItemStack(armor));
            }
        });

        ITextComponent head = null;
        ITextComponent current = null;

        if(FirstAidHandler.undershirtActivated) {
            head = new TextComponentTranslation("scalinghealthissues.reskillable.undershirt.activated");
            head.getStyle().setColor(ConfigHandler.compat.firstAid.reskillableUndershirtMessageFormat);
        }

        if(FirstAidHandler.sturdyActivated) {
            current = new TextComponentTranslation("scalinghealthissues.firstaid.sturdy.activated");
            current.getStyle().setColor(ConfigHandler.compat.firstAid.morphineMessageFormat);

            if(head == null) {
                head = current;
            }
            else {
                head.appendText(" ").appendSibling(current);
            }
        }

        if(!FirstAidHandler.brokenArmor.isEmpty()) {
            current = new TextComponentTranslation("scalinghealthissues.significantdamage.armorlost", FirstAidHandler.brokenArmor.values().toString());
            current.getStyle().setColor(ConfigHandler.compat.firstAid.armorBreakMessageFormat);

            if(head == null) {
                head = current;
            }
            else {
                head.appendText(" ").appendSibling(current);
            }
        }


        if(head != null) {
            Entity entity = source.getImmediateSource();
            if(entity instanceof EntityLivingBase) {
                GuiHandler.cacheAndNotifySpecificPlayer((EntityLivingBase) entity, player, head);
            }

            if(entity != source.getTrueSource()) {
                entity = source.getTrueSource();
                if(entity instanceof EntityLivingBase) {
                    GuiHandler.cacheAndNotifySpecificPlayer((EntityLivingBase) entity, player, head);
                }
            }
        }
        return result;
    }
}
