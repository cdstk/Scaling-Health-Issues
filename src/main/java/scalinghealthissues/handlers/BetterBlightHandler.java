package scalinghealthissues.handlers;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageMarkBlight;
import scalinghealthissues.compat.RLMixinsUtil;
import scalinghealthissues.config.folders.BlightConfig;
import scalinghealthissues.mixin.scalinghealth.BlightHandler_InvokerMixin;

import javax.annotation.Nullable;

public abstract class BetterBlightHandler {

    public static final String NBT_BLIGHT_PROCESSED = ScalingHealth.MOD_ID_OLD + "Issues.BlightProcessed";

    // ******************
    // * Blight marking *
    // ******************

    // Also fixes Armor on spawn
    public static boolean isFakeBlight(EntityLivingBase entityLivingBase) {
        if(ScalingHealthAPI.isBlight(entityLivingBase)) {
            NBTTagCompound nbt = entityLivingBase.getEntityData();
            return !nbt.hasKey(NBT_BLIGHT_PROCESSED) || !nbt.getBoolean(NBT_BLIGHT_PROCESSED);
        }
        return false;
    }

    public static boolean isBlightPotion(Potion potion) {
        // Respect RLMixins
        if(RLMixinsUtil.hasBlightPotionConfig() && RLMixinsUtil.isBlightPotion(potion)) {
            return true;
        }

        // Respect this mod
        if(BlightConfig.BLIGHT_POTIONS.containsKey(potion))
            return true;

        // Respect Scaling Health
        if (potion.equals(MobEffects.INVISIBILITY)) {
            return Config.Mob.Blight.invisibility;
        }
        if(potion.equals(MobEffects.FIRE_RESISTANCE)) {
            return Config.Mob.Blight.fireResist;
        }
        if(potion.equals(MobEffects.SPEED)) {
            return Config.Mob.Blight.speedAmp > -1;
        }
        if(potion.equals(MobEffects.STRENGTH)) {
            return Config.Mob.Blight.strengthAmp > -1;
        }

        return false;
    }

    public static int getBlightPotionAmplifier(Potion potion) {
        if(RLMixinsUtil.hasBlightPotionConfig() && RLMixinsUtil.isBlightPotion(potion)) {
            return RLMixinsUtil.getBlightPotionAmplifier(potion);
        }

        int amp = BlightConfig.BLIGHT_POTIONS.get(potion).amplifier;
        if(amp != 0)
            return amp;

        if(potion.equals(MobEffects.SPEED)) {
            return Config.Mob.Blight.speedAmp;
        }
        if(potion.equals(MobEffects.STRENGTH)) {
            return Config.Mob.Blight.strengthAmp;
        }

        return 0;
    }

    public static int getBlightPotionDuration(Potion potion) {
        return BlightConfig.BLIGHT_POTIONS.get(potion).duration;
    }

    public static PotionEffect initBlightPotionEffect(Potion potion) {
        return new PotionEffect(
                potion,
                getBlightPotionDuration(potion),
                getBlightPotionAmplifier(potion),
                true, // Ambient
                false // Particles
        );
    }

    // Tries to filter [Weapons, Bows, Shields] -> MAINHAND, Stackable -> NULL
    @Nullable
    public static EntityEquipmentSlot getReasonableSlotForItemStack(ItemStack itemStack) {
        EntityEquipmentSlot equipmentSlot = EntityLiving.getSlotForItemStack(itemStack);
        switch (itemStack.getItemUseAction()) {
            case BLOCK:
            case BOW:
                return equipmentSlot;
        }
        if(equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            if(itemStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).isEmpty()) {
                return null;
            }
        }
        return equipmentSlot;
    }

    // Based on EntityLiving.updateEquipmentIfNeeded()
    public static boolean isBetterThanCurrentEquipment(EntityLivingBase entityLivingBase, ItemStack newItemStack, @Nullable EntityEquipmentSlot entityEquipmentSlot) {
        boolean isBetter = true;
        if(entityEquipmentSlot == null)
            entityEquipmentSlot = EntityLiving.getSlotForItemStack(newItemStack);
        ItemStack currentItemStack = entityLivingBase.getItemStackFromSlot(entityEquipmentSlot);

        if (!currentItemStack.isEmpty()) {
            if (entityEquipmentSlot.getSlotType() == EntityEquipmentSlot.Type.HAND) {
                if (newItemStack.getItem() instanceof ItemSword && !(currentItemStack.getItem() instanceof ItemSword)) {
                    isBetter = true;
                } else if (newItemStack.getItem() instanceof ItemSword && currentItemStack.getItem() instanceof ItemSword) {
                    ItemSword newWeapon = (ItemSword) newItemStack.getItem();
                    ItemSword currenWeapon = (ItemSword) currentItemStack.getItem();

                    if (newWeapon.getAttackDamage() == currenWeapon.getAttackDamage()) {
                        isBetter = newItemStack.getMetadata() > currentItemStack.getMetadata() || newItemStack.hasTagCompound() && !currentItemStack.hasTagCompound();
                    } else {
                        isBetter = newWeapon.getAttackDamage() > currenWeapon.getAttackDamage();
                    }
                } else if (newItemStack.getItem() instanceof ItemBow && currentItemStack.getItem() instanceof ItemBow) {
                    isBetter = newItemStack.hasTagCompound() && !currentItemStack.hasTagCompound();
                } else {
                    isBetter = false;
                }
            } else if (newItemStack.getItem() instanceof ItemArmor && !(currentItemStack.getItem() instanceof ItemArmor)) {
                isBetter = true;
            } else if (newItemStack.getItem() instanceof ItemArmor && currentItemStack.getItem() instanceof ItemArmor && !EnchantmentHelper.hasBindingCurse(currentItemStack)) {
                ItemArmor newArmor = (ItemArmor) newItemStack.getItem();
                ItemArmor currentArmor = (ItemArmor) currentItemStack.getItem();

                if (newArmor.damageReduceAmount == currentArmor.damageReduceAmount) {
                    isBetter = newItemStack.getMetadata() > currentItemStack.getMetadata() || newItemStack.hasTagCompound() && !currentItemStack.hasTagCompound();
                } else {
                    isBetter = newArmor.damageReduceAmount > currentArmor.damageReduceAmount;
                }
            } else {
                isBetter = false;
            }
        }

        return isBetter;
    }

    // Update blight upon spawn instead of waiting for batch update (the ticking updates are still kept)
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getWorld().isRemote) return;
        if(!(event.getEntity() instanceof EntityLiving)) return;
        EntityLiving entity = (EntityLiving) event.getEntity();
        if(!ScalingHealthAPI.isBlight(entity)) return;

        if(isFakeBlight(entity)) {
            DifficultyHandler.INSTANCE.recalculate(entity);
        }

        // Client isn't tracking yet, so have to a do a point, SH's builtin update cycle will sync anything else
        NetworkHandler.INSTANCE.sendToAllAround(new MessageMarkBlight(entity, true),
                new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 64));

        if(BlightHandler_InvokerMixin.scalingHealthIssues$invokeGetBlightFire(entity) == null)
            BlightHandler_InvokerMixin.scalingHealthIssues$invokeSpawnBlightFire(entity);

        BlightHandler_InvokerMixin.scalingHealthIssues$invokeApplyBlightPotionEffects(entity);
    }

    // Instantly recover potion
    @SubscribeEvent
    public static void onPotionRemove(PotionEvent.PotionRemoveEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if(effect == null) return;
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if(event.isCanceled()) return;
        if(!ScalingHealthAPI.isBlight(entity)) return;

        if(isBlightPotion(event.getPotion())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if(!ScalingHealthAPI.isBlight(entity)) return;

        if(isBlightPotion(event.getPotionEffect().getPotion()))
            event.setResult(Event.Result.ALLOW);
    }
}
