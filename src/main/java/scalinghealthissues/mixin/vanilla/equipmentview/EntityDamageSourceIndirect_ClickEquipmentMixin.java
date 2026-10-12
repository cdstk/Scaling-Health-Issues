package scalinghealthissues.mixin.vanilla.equipmentview;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import scalinghealthissues.network.GuiHandler;

import javax.annotation.Nullable;

@Mixin(value = EntityDamageSourceIndirect.class, priority = 1001)
public abstract class EntityDamageSourceIndirect_ClickEquipmentMixin extends EntityDamageSource {

    @Shadow @Nullable public abstract Entity getTrueSource();

    public EntityDamageSourceIndirect_ClickEquipmentMixin(String damageTypeIn, @Nullable Entity damageSourceEntityIn) {
        super(damageTypeIn, damageSourceEntityIn);
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageClickEventEquipment(Entity entity, Operation<ITextComponent> original){
        return GuiHandler.addClickToView(original.call(entity), entity);
    }

    @WrapOperation(
            method = "getDeathMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/text/ITextComponent;")
    )
    private ITextComponent scalingHealthIssues_vanillaEntityDamageSourceIndirect_getDeathMessageClickEventEquipment(EntityLivingBase entity, Operation<ITextComponent> original){
        return GuiHandler.addClickToView(original.call(entity), entity);
    }
}
