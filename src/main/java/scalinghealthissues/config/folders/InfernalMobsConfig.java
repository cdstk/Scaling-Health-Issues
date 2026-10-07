package scalinghealthissues.config.folders;

import net.minecraftforge.common.config.Config;

public class InfernalMobsConfig {

    @Config.Comment("Let all players know when an Infernal is killed by a player via chat message.")
    @Config.Name("Infernal Killed by Player Message")
    public boolean killedByPlayerMessage = true;

    @Config.Comment({
            "The minimum number of Modifiers the Infernal killed must have.",
            "\tInfernal - 11 or more",
            "\tUltra - 6 to 10",
            "\tRare - 5 or less"
    })
    @Config.Name("Infernal Killed by Player Message Modifier Count")
    public int killedByPlayerModifiers = 11;
}
