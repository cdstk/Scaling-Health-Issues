package scalinghealthissues.mixin.bloodmoon;

import com.llamalad7.mixinextras.sugar.Local;
import lumien.bloodmoon.server.BloodmoonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.compat.BloodmoonUtil;

@Mixin(BloodmoonHandler.class)
public abstract class BloodmoonHandler_MaxHeartRequirementMixin {

    @Inject(
            method = "playerJoinedWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendTo(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraft/entity/player/EntityPlayerMP;)V"),
            remap = false
    )
    private void scalingHealthIssues_bloodmoonBloodmoonHandler_playerJoinedWorldSafe(EntityJoinWorldEvent event, CallbackInfo ci){
        EntityPlayer player = (EntityPlayer) event.getEntity();
        if(!BloodmoonUtil.canExperienceBloodmoon(player)) {
            BloodmoonUtil.addSafePlayer(player);

            if(!ScalingHealthIssues.PROXY.isSinglePlayer())
                event.getEntity().sendMessage((new TextComponentTranslation("text.bloodmoon.nosleep")).setStyle((new Style()).setColor(TextFormatting.RED)));
        }
    }

    @Inject(
            method = "endWorldTick",
            at = @At(value = "INVOKE", target = "Llumien/bloodmoon/server/BloodmoonHandler;setBloodmoon(Z)V", ordinal = 1),
            remap = false
    )
    private void scalingHealthIssues_bloodmoonBloodmoonHandler_endWorldTickSafe(TickEvent.WorldTickEvent event, CallbackInfo ci, @Local World world){
        world.playerEntities.forEach(player -> {
            if(!BloodmoonUtil.canExperienceBloodmoon(player))
                BloodmoonUtil.addSafePlayer(player);
        });
    }

    @Inject(
            method = "setBloodmoon",
            at = @At("HEAD"),
            remap = false
    )
    private void scalingHealthIssues_bloodmoonBloodmoonHandler_setBloodmoonSafe(boolean bloodMoon, CallbackInfo ci){
        if(!bloodMoon) BloodmoonUtil.resetSafePlayers();
    }
}
