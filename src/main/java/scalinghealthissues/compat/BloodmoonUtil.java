package scalinghealthissues.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import scalinghealthissues.config.ConfigHandler;

import java.util.Collection;
import java.util.HashSet;

public abstract class BloodmoonUtil {

    private static final Collection<EntityPlayer> CURRENTLY_SAFE = new HashSet<>();

    public static boolean canExperienceBloodmoon(EntityPlayer player) {
        SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
        if (data != null && Config.Items.Heart.increaseHealth) {
            float containerHealth = data.getMaxHealth() - Config.Player.Health.startingHealth;
            return containerHealth >= 2F * ConfigHandler.compat.bloodmoon.heartContainersRequired;
        }
        return true;
    }

    public static boolean isSafeFromActive(EntityPlayer player) {
        return CURRENTLY_SAFE.contains(player);
    }

    public static void addSafePlayer(EntityPlayer entityPlayer) {
        CURRENTLY_SAFE.add(entityPlayer);
    }

    public static void resetSafePlayers() {
        CURRENTLY_SAFE.clear();
    }
}
