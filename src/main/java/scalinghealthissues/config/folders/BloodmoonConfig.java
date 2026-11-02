package scalinghealthissues.config.folders;

import net.minecraftforge.common.config.Config;

public class BloodmoonConfig {

    @Config.Comment("Whether players will be notified if they unlock or can't experience Bloodmoons")
    @Config.Name("Mob Event Heart Container Messages")
    public boolean heartContainerMessage = true;

    @Config.Comment("The number of Heart Containers that must be used in order to experience Bloodmoons")
    @Config.Name("Heart Container Requirement")
    public int heartContainersRequired = 3;
}
