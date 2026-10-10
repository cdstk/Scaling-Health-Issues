package scalinghealthissues.mixin.vanilla.equipmentview;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CombatTracker.class)
public abstract class CombatTracker_ClickEquipmentMixin {

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSource_getDeathMessageClickEventEquipment(EntityLivingBase entity, Operation<ITextComponent> original){
        ITextComponent entityName = original.call(entity);

        Style style = entityName.getStyle();
        if(style.getClickEvent() == null) {
            String command = "/scalinghealthissues viewinventory " + entity.getCachedUniqueIdString();

            style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            style.setColor(TextFormatting.UNDERLINE);
        }

        return entityName;
    }
}
