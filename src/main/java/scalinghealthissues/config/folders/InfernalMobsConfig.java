package scalinghealthissues.config.folders;

import net.minecraftforge.common.config.Config;

public class InfernalMobsConfig {

    @Config.Comment({
            "The minimum number of Modifiers the Infernal killed must have.",
            "\tInfernal - 11 or more",
            "\tUltra - 6 to 10",
            "\tRare - 5 or less"
    })
    @Config.Name("Infernal Killed by Player Message Modifier Count")
    public int killedByPlayerModifiers = 11;
}
