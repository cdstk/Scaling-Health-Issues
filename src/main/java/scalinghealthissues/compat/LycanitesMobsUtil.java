package scalinghealthissues.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import scalinghealthissues.config.ForgeConfigHandler;

public class LycanitesMobsUtil {

    public static boolean canExperienceMobEvent(EntityPlayer player) {
        SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
        if (data != null) {
            return Config.Items.Heart.increaseHealth && data.getHealth() >= 2F * ForgeConfigHandler.compat.lycanitesEventHeartContainersRequired;
        }
        return true;
    }
}
