package scalinghealthissues.config.folders;

import net.minecraftforge.common.config.Config;

public class LycanitesMobsConfig {

    @Config.Comment("The number of Heart Containers that must be used in order to experience Lycanites Mob Events")
    @Config.Name("Mob Event Heart Container Requirement")
    public int heartContainersRequired = 3;

    @Config.Comment("Whether the Scaling Health Auto Regen is applied to Lycanites Soulbinds, who have their own auto regen mechanic.")
    @Config.Name("Auto Regen Soulbinds")
    public boolean petRegenSoulbind = true;
}
