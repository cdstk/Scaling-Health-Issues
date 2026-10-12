package scalinghealthissues.mixin.significantdamagetracker.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.network.GuiHandler;

import java.util.HashMap;
import java.util.Map;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayer_DamageTrackerMixin extends EntityLivingBase {

    @Shadow public abstract Iterable<ItemStack> getArmorInventoryList();

    @Unique
    private boolean scalingHealthIssues$undershirtActive = false;

    @Unique
    private final Map<EntityEquipmentSlot, String> scalingHealthIssues$brokenArmor = new HashMap<>();

    public EntityPlayer_DamageTrackerMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(
            method = "damageEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;onLivingHurt(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)F", remap = false)
    )
    private void scalingHealthIssues_vanillaEntityPlayer_damageEntityUndershirtPre(DamageSource damageSrc, float damageAmount, CallbackInfo ci){
        this.scalingHealthIssues$undershirtActive = this.getEntityData().getInteger("skillable:UndershirtCD") <= 0;
    }

    @WrapOperation(
            method = "damageEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ISpecialArmor$ArmorProperties;applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", remap = false)
    )
    private float scalingHealthIssues_vanillaEntityPlayer_damageEntitySignificantDamagePre(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, Operation<Float> original){
        this.scalingHealthIssues$brokenArmor.clear();
        this.getArmorInventoryList().forEach(armor -> {
            if(!armor.isEmpty()) {
                this.scalingHealthIssues$brokenArmor.put(EntityLiving.getSlotForItemStack(armor), armor.getDisplayName());
            }
        });

        float result = original.call(entity, inventory, source, damage);

        this.getArmorInventoryList().forEach(armor -> {
            if(!armor.isEmpty()) {
                this.scalingHealthIssues$brokenArmor.remove(EntityLiving.getSlotForItemStack(armor));
            }
        });

        return result;
    }

    @Inject(
            method = "damageEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addExhaustion(F)V")
    )
    private void scalingHealthIssues_vanillaEntityPlayer_damageEntitySignificantDamagePost(DamageSource damageSrc, float damageAmount, CallbackInfo ci){
        boolean undershirtUsed = this.scalingHealthIssues$undershirtActive && this.getEntityData().getInteger("skillable:UndershirtCD") > 0;

        ITextComponent head = null;
        ITextComponent current = null;

        if(undershirtUsed) {
            head = new TextComponentTranslation("scalinghealthissues.reskillable.undershirt.activated");
            head.getStyle().setColor(ConfigHandler.compat.firstAid.reskillableUndershirtMessageFormat);
        }

        if(!this.scalingHealthIssues$brokenArmor.isEmpty()) {
            current = new TextComponentTranslation("scalinghealthissues.significantdamage.armorlost", this.scalingHealthIssues$brokenArmor.values().toString());
            current.getStyle().setColor(ConfigHandler.compat.firstAid.armorBreakMessageFormat);

            if(head == null) {
                head = current;
            }
            else {
                head.appendText(" ").appendSibling(current);
            }
        }


        if(head != null) {
            Entity entity = damageSrc.getImmediateSource();
            if(entity instanceof EntityLivingBase) {
                GuiHandler.cacheAndNotifySpecificPlayer((EntityLivingBase) entity, (EntityPlayer) (Object) this, head);
            }

            if(entity != damageSrc.getTrueSource()) {
                entity = damageSrc.getTrueSource();
                if(entity instanceof EntityLivingBase) {
                    GuiHandler.cacheAndNotifySpecificPlayer((EntityLivingBase) entity, (EntityPlayer) (Object) this, head);
                }
            }
        }
    }
}
