package scalinghealthissues.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;

public abstract class ScalingHealthWrapper {

    public static boolean isBlight(Entity entity) {
        if(entity instanceof EntityLivingBase)
            return ScalingHealthAPI.isBlight((EntityLivingBase) entity);

        return false;
    }
}
