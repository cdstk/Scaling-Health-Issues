package scalinghealthissues.mixin.modcompat.lycanitesmobs.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.lycanitesmobs.core.mobevent.MobEvent;
import com.lycanitesmobs.core.mobevent.MobEventPlayerClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.compat.LycanitesMobsUtil;

@Mixin(MobEventPlayerClient.class)
public abstract class MobEventPlayerClient_MaxHeartRequirementMixin {

    @Shadow(remap = false) public MobEvent mobEvent;
    @Shadow(remap = false) public int ticks;

    @WrapMethod(
            method = "onStart",
            remap = false
    )
    private void scalingHealthIssues_lycanitesMobsMobEventPlayerClient_onStartPlayerHearts(EntityPlayer player, Operation<Void> original){
        boolean clientCanExperience = this.mobEvent.channel.equals("boss") || LycanitesMobsUtil.canExperienceMobEvent(player);

        // Don't show are play sound
        if(ScalingHealthIssues.PROXY.isSinglePlayer() && !clientCanExperience) {
            this.ticks = 241;
        }
        else {
            original.call(player);
            // Show for 3 seconds and play sound
            if(!clientCanExperience) {
                this.ticks = 60;
            }
        }

        // Safe Message
        if(!clientCanExperience) {
            ITextComponent safeText = new TextComponentString(I18n.format("scalinghealthissues.heartcontainer.lockedevent"));
            safeText.getStyle().setColor(TextFormatting.GREEN);
            safeText.appendText(" ").appendSibling((new TextComponentString(this.mobEvent.getTitle())).setStyle((new Style()).setColor(TextFormatting.RESET)));
            player.sendMessage(safeText);
        }
    }
}
