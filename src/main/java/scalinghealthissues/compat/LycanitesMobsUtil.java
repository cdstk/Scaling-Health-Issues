package scalinghealthissues.compat;

import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import scalinghealthissues.config.ScalingHealthIssuesConfigHandler;

public class LycanitesMobsUtil {

    public static boolean canExperienceMobEvent(EntityPlayer player) {
        SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
        if (data != null && Config.Items.Heart.increaseHealth) {
            float containerHealth = data.getMaxHealth() - Config.Player.Health.startingHealth;
            return containerHealth >= 2F * ScalingHealthIssuesConfigHandler.compat.bloodmoonHeartContainersRequired;
        }
        return true;
    }
}
