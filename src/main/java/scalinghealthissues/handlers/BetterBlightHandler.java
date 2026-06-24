package scalinghealthissues.handlers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageMarkBlight;
import scalinghealthissues.compat.RLMixinsUtil;
import scalinghealthissues.config.folders.BlightConfig;
import scalinghealthissues.mixin.scalinghealth.BlightHandler_InvokerMixin;

public abstract class BetterBlightHandler {

    public static final String NBT_BLIGHT_PROCESSED = ScalingHealth.MOD_ID_OLD + "Issues.BlightProcessed";

    // ******************
    // * Blight marking *
    // ******************

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
        if(BlightConfig.isBlightPotion(potion))
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

        int amp = BlightConfig.getBlightPotionAmplifier(potion);
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
        return BlightConfig.getBlightPotionDuration(potion);
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

        NetworkHandler.INSTANCE.sendToAllTracking(new MessageMarkBlight(entity, true), entity);

        if(BlightHandler_InvokerMixin.scalingHealthIssues$invokeGetBlightFire(entity) == null)
            BlightHandler_InvokerMixin.scalingHealthIssues$invokeSpawnBlightFire(entity);

        BlightHandler_InvokerMixin.scalingHealthIssues$invokeApplyBlightPotionEffects(entity);
    }

    // Instantly recover potion
    private static boolean handlingRemove = false;
    @SubscribeEvent
    public static void onPotionRemove(PotionEvent.PotionRemoveEvent event) {
        if(handlingRemove) return;
        PotionEffect effect = event.getPotionEffect();
        if(effect == null) return;
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if(!ScalingHealthAPI.isBlight(entity)) return;

        // Refresh effect, less complicated than canceling the event
        if(isBlightPotion(event.getPotion())) {
            handlingRemove = true;
            entity.addPotionEffect(initBlightPotionEffect(effect.getPotion()));
            handlingRemove = false;
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

    // Instantly recover potion
    private static boolean handlingExpire = false;
    @SubscribeEvent
    public static void onPotionExpiry(PotionEvent.PotionExpiryEvent event) {
        if(handlingExpire) return;
        if(event.getPotionEffect() == null) return;
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if(!ScalingHealthAPI.isBlight(entity)) return;
        Potion potion = event.getPotionEffect().getPotion();

        // Refresh effect
        if(isBlightPotion(potion)) {
            handlingExpire = true;
            entity.addPotionEffect(initBlightPotionEffect(potion));
            handlingExpire = false;
        }
    }
}
