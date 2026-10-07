package scalinghealthissues.mixin.modcompat.firstaid.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ichttt.mods.firstaid.client.ClientEventHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import scalinghealthissues.config.ConfigHandler;

import java.util.List;

@Mixin(ClientEventHandler.class)
public abstract class ClientEventHandler_Mixin {

    @ModifyConstant(
            method = "tooltipItems",
            constant = @Constant(stringValue = "3:30-4:30"),
            remap = false
    )
    private static String scalingHealthIssues_firstAidClientEventHandler_tooltipItemsMorphineDuration(String constant){
        return StringUtils.ticksToElapsedTime(20 * ConfigHandler.compat.firstAid.morphineDurationMin)
                + "-"
                + StringUtils.ticksToElapsedTime(20 * ConfigHandler.compat.firstAid.morphineDurationMax);
    }

    @WrapOperation(
            method = "tooltipItems",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0),
            remap = false
    )
    private static boolean scalingHealthIssues_firstAidClientEventHandler_tooltipItemsMorphineRework(List<String> tooltip, Object tooltipLine, Operation<Boolean> original){
        boolean result = original.call(tooltip, tooltipLine);
        tooltip.add(I18n.format("scalinghealthissues.firstaid.sturdy.description"));
        tooltip.add(I18n.format("scalinghealthissues.firstaid.mitigation.description"));
        return result;
    }
}
