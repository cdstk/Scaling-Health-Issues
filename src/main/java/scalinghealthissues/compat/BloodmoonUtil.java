package scalinghealthissues.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import scalinghealthissues.config.ForgeConfigHandler;

import java.util.Collection;
import java.util.HashSet;

public abstract class BloodmoonUtil {

    private static final Collection<EntityPlayer> CURRENTLY_SAFE = new HashSet<>();

    public static boolean canExperienceBloodmoon(EntityPlayer player) {
        if(CURRENTLY_SAFE.contains(player)) return false;

        SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
        if (data != null) {
            return Config.Items.Heart.increaseHealth && data.getHealth() >= 2F * ForgeConfigHandler.compat.bloodmoonHeartContainersRequired;
        }
        return true;
    }

    public static void addSafePlayer(EntityPlayer entityPlayer) {
        CURRENTLY_SAFE.add(entityPlayer);
    }

    public static void resetSafePlayers() {
        CURRENTLY_SAFE.clear();
    }
}
