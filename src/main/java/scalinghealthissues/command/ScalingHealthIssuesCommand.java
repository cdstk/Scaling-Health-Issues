package scalinghealthissues.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.Tags;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.network.GuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScalingHealthIssuesCommand extends CommandBase {

    public static final String VIEW_INVENTORY = "viewinventory";

    @Override
    public String getName() {
        return Tags.MODID;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/scalinghealthissues viewinventory [entityUUID]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1 || !(sender instanceof EntityPlayerMP)) {
            throw new WrongUsageException(this.getUsage(sender));
        }

        if(!ConfigHandler.server.enableEntityEquipmentView) {
            throw new CommandException("scalinghealthissues.gui.entityinventory.disabled");
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        UUID uuid = null;
        Integer selectedEntityID = null;

        if(args.length < 2) {
            selectedEntityID = player.getEntityId();
        }
        else {
            try {
                uuid = UUID.fromString(args[1]);
            }
            catch (IllegalArgumentException ignored) {}
        }

        if(uuid != null) {
            selectedEntityID = GuiHandler.getViewableEntityID(uuid);
        }

        if(selectedEntityID == null) {
            selectedEntityID = getEntity(server, sender, args[1]).getEntityId();
        }

        player.openGui(
                ScalingHealthIssues.instance,
                GuiHandler.VIEW_ENTITY_INVENTORY,
                player.world,
                selectedEntityID,
                0,
                0
        );
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender commandSender) {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(CommandBase.getListOfStringsMatchingLastWord(args, VIEW_INVENTORY));
        }
        else if(args.length == 2) {
            completions.addAll(CommandBase.getListOfStringsMatchingLastWord(args, "@e[c=1,type=!player]", "@e[c=1,type="));
        }
        return completions;
    }
}
