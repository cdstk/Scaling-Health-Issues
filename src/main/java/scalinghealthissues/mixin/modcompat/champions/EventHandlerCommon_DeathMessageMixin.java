package scalinghealthissues.mixin.modcompat.champions;

import c4.champions.common.EventHandlerCommon;
import c4.champions.common.capability.IChampionship;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.compat.ChampionsUtil;

@Mixin(EventHandlerCommon.class)
public abstract class EventHandlerCommon_DeathMessageMixin {

    @WrapOperation(
            method = "livingDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/PlayerList;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V")
    )
    private void scalingHealthIssues_championsEventHandlerCommon_livingDeathHover(PlayerList instance, ITextComponent deathMessage, Operation<Void> original, @Local EntityLivingBase entityLivingBase, @Local IChampionship chp){
        // Color doesn't display correctly, only affects first word
//        ITextComponent messageWrapper = new TextComponentString("");
//        ITextComponent champName = new TextComponentString(chp.getName());
//        champName.getStyle().setColor(HexToColorMap.nearestColor(chp.getRank().getColor()));
//
//        messageWrapper.appendSibling(champName);
//        deathMessage.getSiblings().forEach(messageWrapper::appendSibling);

        if(entityLivingBase instanceof EntityLiving)
            ChampionsUtil.addChampionHoverText(deathMessage, (EntityLiving) entityLivingBase);

        original.call(instance, deathMessage);
    }
}
