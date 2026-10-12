package scalinghealthissues.mixin.significantdamagetracker.firstaid;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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
import org.spongepowered.asm.mixin.Mixin;
import scalinghealthissues.compat.handlers.FirstAidHandler;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.network.GuiHandler;

@Mixin(DamageDistribution.class)
public abstract class DamageDistribution_DamageTrackerMixin {

    @WrapMethod(
            method = "handleDamageTaken",
            remap = false
    )
    private static float scalingHealthIssues_firstAidDamageDistribution_handleDamageTakenCacheSignificantDamage(IDamageDistribution damageDistribution, AbstractPlayerDamageModel damageModel, float damage, EntityPlayer player, DamageSource source, boolean addStat, boolean redistributeIfLeft, Operation<Float> original){
        /**
         * Pre
         */
        FirstAidHandler.sturdyActivated = false;
        FirstAidHandler.undershirtActivated = player.getEntityData().getInteger("skillable:UndershirtCD") <= 0;
        FirstAidHandler.brokenArmor.clear();
        player.getArmorInventoryList().forEach(armor -> {
            if(!armor.isEmpty()) {
                FirstAidHandler.brokenArmor.put(EntityLiving.getSlotForItemStack(armor), armor.getDisplayName());
            }
        });

        float result = original.call(damageDistribution, damageModel, damage, player, source, addStat, redistributeIfLeft);

        /**
         * Post
         */
        boolean undershirtUsed = FirstAidHandler.undershirtActivated && player.getEntityData().getInteger("skillable:UndershirtCD") > 0;
        player.getArmorInventoryList().forEach(armor -> {
            if(!armor.isEmpty()) {
                FirstAidHandler.brokenArmor.remove(EntityLiving.getSlotForItemStack(armor));
            }
        });

        ITextComponent head = null;
        ITextComponent current = null;

        if(undershirtUsed) {
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
