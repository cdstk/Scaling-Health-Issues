package scalinghealthissues.compat;

import net.minecraft.potion.Potion;
import rlmixins.handlers.ConfigHandler;

public abstract class RLMixinsUtil {

    public static boolean hasBlightPotionConfig() {
        return ModLoadedUtil.RLMIXINS.isLoaded() && ModLoadedUtil.versionInRange(ModLoadedUtil.RLMIXINS, "[1.4.4,)");
    }

    public static boolean isBlightPotion(Potion potion) {
        return ConfigHandler.SCALINGHEALTH_CONFIG.getBlightEffects().containsKey(potion);
    }

    public static int getBlightPotionAmplifier(Potion potion) {
        return ConfigHandler.SCALINGHEALTH_CONFIG.getBlightEffects().getOrDefault(potion, 0);
    }
}
