package scalinghealthissues.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotConditionalEdit extends Slot {

    private final boolean canEdit;
    private boolean disabled = false;

    public SlotConditionalEdit(IInventory inventoryIn, int index, int xPosition, int yPosition, boolean canEdit) {
        super(inventoryIn, index, xPosition, yPosition);
        this.canEdit = canEdit;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled() {
        this.disabled = true;
    }

    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        this.onSlotChanged();
        this.playEquipSound(thePlayer, stack);
        return stack;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.canEdit && super.isItemValid(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        boolean canTake = this.canEdit && super.canTakeStack(playerIn);
        if(canTake) {
            this.playEquipSound(playerIn, this.getStack());
        }
        return canTake;
    }

    @SideOnly(Side.CLIENT)
    public boolean isEnabled() {
        return !this.disabled;
    }

    protected void playEquipSound(EntityPlayer thePlayer, ItemStack stack)  {
        if (!stack.isEmpty()) {
            SoundEvent soundevent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
            Item item = stack.getItem();

            if (item instanceof ItemArmor) {
                soundevent = ((ItemArmor)item).getArmorMaterial().getSoundEvent();
            }
            else if (item == Items.ELYTRA) {
                soundevent = SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA;
            }

            thePlayer.playSound(soundevent, 1.0F, 1.0F);
        }
    }
}
