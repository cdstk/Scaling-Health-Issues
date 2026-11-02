package scalinghealthissues.mixin.modcompat.bloodmoon;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import lumien.bloodmoon.network.PacketHandler;
import lumien.bloodmoon.network.messages.MessageBloodmoonStatus;
import lumien.bloodmoon.server.BloodmoonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.compat.BloodmoonUtil;
import scalinghealthissues.config.ConfigHandler;

@Mixin(BloodmoonHandler.class)
public abstract class BloodmoonHandler_MaxHeartRequirementMixin {

    @WrapWithCondition(
            method = "playerJoinedWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendTo(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraft/entity/player/EntityPlayerMP;)V"),
            remap = false
    )
    private boolean scalingHealthIssues_bloodmoonBloodmoonHandler_playerJoinedWorldSafe(SimpleNetworkWrapper instance, IMessage message, EntityPlayerMP player){
        if(!BloodmoonUtil.isSafeFromActive(player) && !BloodmoonUtil.canExperienceBloodmoon(player)) {
            BloodmoonUtil.addSafePlayer(player);
        }
        if(BloodmoonUtil.isSafeFromActive(player)) {
            if(player.ticksExisted != 0 && ConfigHandler.compat.bloodmoon.heartContainerMessage) {
                ITextComponent safeText = new TextComponentTranslation("scalinghealthissues.heartcontainer.lockedevent");
                safeText.getStyle().setColor(TextFormatting.GREEN);
                safeText.appendText(" ").appendSibling((new TextComponentTranslation("text.bloodmoon.notify")).setStyle((new Style()).setColor(TextFormatting.RED)));
                player.sendMessage(safeText);
            }
            if(!ScalingHealthIssues.PROXY.isSinglePlayer()) {
                player.sendMessage((new TextComponentTranslation("text.bloodmoon.nosleep")).setStyle((new Style()).setColor(TextFormatting.RED)));
            }
            return false;
        }
        else
            return true;
    }

    @WrapWithCondition(
            method = "endWorldTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V")
    )
    private boolean scalingHealthIssues_bloodmoonBloodmoonHandler_endWorldTickSafe(EntityPlayer player, ITextComponent iTextComponent, @Local World world){
        if(!BloodmoonUtil.isSafeFromActive(player) && !BloodmoonUtil.canExperienceBloodmoon(player)) {
            BloodmoonUtil.addSafePlayer(player);

            if(ConfigHandler.compat.bloodmoon.heartContainerMessage) {
                ITextComponent safeText = new TextComponentTranslation("scalinghealthissues.heartcontainer.lockedevent");
                safeText.getStyle().setColor(TextFormatting.GREEN);
                safeText.appendText(" ").appendSibling((new TextComponentTranslation("text.bloodmoon.notify")).setStyle((new Style()).setColor(TextFormatting.RED)));
                player.sendMessage(safeText);
            }
            if(!ScalingHealthIssues.PROXY.isSinglePlayer()) {
                player.sendMessage((new TextComponentTranslation("text.bloodmoon.nosleep")).setStyle((new Style()).setColor(TextFormatting.RED)));
            }
            if (player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new MessageBloodmoonStatus(false), (EntityPlayerMP) player);
            }
            return false;
        }
        else
            return true;
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
