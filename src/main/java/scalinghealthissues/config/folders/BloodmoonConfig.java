package scalinghealthissues.config.folders;

import net.minecraftforge.common.config.Config;

public class BloodmoonConfig {

    @Config.Comment("The number of Heart Containers that must be used in order to experience Bloodmoons")
    @Config.Name("Heart Container Requirement")
    public int heartContainersRequired = 3;
}
