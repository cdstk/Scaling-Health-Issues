package scalinghealthissues.mixin.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBase_TotemRefreshMechanicsMixin extends Entity {

    @Shadow public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    @Unique
    private static final UUID BROKENHEART_HEALTH_UUID = UUID.fromString("554f3929-4193-4ae5-a4da-4b528a89ca32");

    public EntityLivingBase_TotemRefreshMechanicsMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(
            method = "checkTotemDeathProtection",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;setHealth(F)V")
    )
    private void scalingHealthIssues_vanillaEntityLivingBase_checkTotemDeathProtectionRefreshModdedMechanics(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if(this.getEntityData().hasKey("skillable:UndershirtCD"))
            this.getEntityData().setInteger("skillable:UndershirtCD", 0);

        IAttributeInstance maxHealth = this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if(maxHealth != null) {
            AttributeModifier modifier = maxHealth.getModifier(BROKENHEART_HEALTH_UUID);
            if (modifier != null) {
                maxHealth.removeModifier(modifier);
            }
        }
    }
}
