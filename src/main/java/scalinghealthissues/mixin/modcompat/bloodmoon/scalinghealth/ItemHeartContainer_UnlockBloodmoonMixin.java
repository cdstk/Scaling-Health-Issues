package scalinghealthissues.mixin.modcompat.bloodmoon.scalinghealth;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.silentchaos512.scalinghealth.item.ItemHeartContainer;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.compat.BloodmoonUtil;

@Mixin(ItemHeartContainer.class)
public abstract class ItemHeartContainer_UnlockBloodmoonMixin {

    @WrapOperation(
            method = "useForHealthIncrease",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/scalinghealth/utils/SHPlayerDataHandler$PlayerData;incrementMaxHealth(F)V"),
            remap = false
    )
    private void scalingHealthIssues_scalingHealthItemHeartContainer_useForHealthIncreaseUnlockBloodmoon(SHPlayerDataHandler.PlayerData instance, float newHealth, Operation<Void> original, @Local(argsOnly = true) EntityPlayer player){
        boolean previous = BloodmoonUtil.canExperienceBloodmoon(player);

        original.call(instance, newHealth);

        if(previous != BloodmoonUtil.canExperienceBloodmoon(player)) {
            ITextComponent unlockText = new TextComponentTranslation("scalinghealthissues.heartcontainer.unlockedevent");
            unlockText.appendText(" ").appendSibling((new TextComponentTranslation("text.bloodmoon.notify")).setStyle((new Style()).setColor(TextFormatting.RED)));
            player.sendMessage(unlockText);
        }
    }
}
