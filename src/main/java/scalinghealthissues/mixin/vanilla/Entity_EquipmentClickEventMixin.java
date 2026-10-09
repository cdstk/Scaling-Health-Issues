package scalinghealthissues.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class Entity_EquipmentClickEventMixin {

    @Shadow public abstract boolean isEntityAlive();
    @Shadow public abstract String getCachedUniqueIdString();

    @ModifyReturnValue(
            method = "getDisplayName",
            at = @At("RETURN")
    )
    private ITextComponent scalingHealthIssues_vanillaEntity_getDisplayNameClickEventEquipment(ITextComponent original){
        if(this.isEntityAlive()) {
            Style style = original.getStyle();
            if(style.getClickEvent() == null) {
                String command = "/scalinghealthissues viewinventory " + this.getCachedUniqueIdString();

                style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
                style.setColor(TextFormatting.UNDERLINE);
            }
        }
        return original;
    }
}
