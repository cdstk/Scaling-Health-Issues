package scalinghealthissues.mixin.vanilla.equipmentview;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.network.GuiHandler;


@Mixin(value = DamageSource.class, priority = 1001)
public abstract class DamageSource_ClickEquipmentMixin {

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSource_getDeathMessageClickEventEquipment(EntityLivingBase entity, Operation<ITextComponent> original){
        return GuiHandler.addClickToView(original.call(entity), entity);
    }
}
