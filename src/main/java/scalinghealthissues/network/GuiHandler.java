package scalinghealthissues.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import scalinghealthissues.client.gui.inventory.GuiScreenEntityInventory;
import scalinghealthissues.inventory.ContainerEntity;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    public static final int VIEW_ENTITY_INVENTORY = 1;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == VIEW_ENTITY_INVENTORY) {
            Entity targetEntity = world.getEntityByID(x);
            if(targetEntity instanceof EntityPlayer) {
                player.sendMessage(new TextComponentTranslation("scalinghealthissues.gui.entityinventory.playererror").setStyle(new Style().setColor(TextFormatting.RED)));
            }
            else if(targetEntity != null && targetEntity.isAddedToWorld()) {
                return new ContainerEntity(targetEntity, player);
            }
            else {
                player.sendMessage(new TextComponentTranslation("scalinghealthissues.gui.entityinventory.nullerror").setStyle(new Style().setColor(TextFormatting.RED)));
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == VIEW_ENTITY_INVENTORY) {
            Entity targetEntity = world.getEntityByID(x);
            if(targetEntity instanceof EntityPlayer) {
                player.sendMessage(new TextComponentTranslation("scalinghealthissues.gui.entityinventory.playererror").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
            }
            else if(targetEntity != null && targetEntity.isAddedToWorld()) {
                return new GuiScreenEntityInventory(new ContainerEntity(targetEntity, player));
            }
            else {
                player.sendMessage(new TextComponentTranslation("scalinghealthissues.gui.entityinventory.nullerror").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
            }
        }
        return null;
    }
}
