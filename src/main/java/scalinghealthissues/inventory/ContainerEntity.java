package scalinghealthissues.inventory;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ContainerEntity extends Container {

    public static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{
            EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
    };

    public final Entity entity;
    public final IInventory entityInventory; // Wrapped base + item arrays
    public final IInventory baseInventory; // Optional

    public enum InventoryLayout {
        PLAYER_INV_OTHER_HOT,
        PLAYER_HOT_OTHER_INV,
        PLAYER_INV
    }

    public final InventoryLayout inventoryLayout;

    // 0 Mainhand
    // 1 Offhand
    // 2 Feet
    // 3 Legs
    // 4 Chest
    // 5 Helmet
    // [6, 14] Players Hotbar OR Small Inventory
    // [15, 42) 9x3 Inventory OR Player Inventory

    public ContainerEntity(Entity entity, EntityPlayer viewingPlayer) {
        IInventory playerSizedInventory = InventoryEntityPlayerSized.getInventoryWrapper(entity);
        this.entity = entity;
        this.baseInventory = InventoryEntityPlayerSized.getBaseInventory(entity);

        // Check if the viewer has OP permission level 3 or higher
        boolean canEdit = viewingPlayer.capabilities.isCreativeMode && viewingPlayer.canUseCommand(3, "");
        boolean hasEquipment = this.entity instanceof EntityLivingBase;

        int baseInventorySize = 0;

        if(playerSizedInventory instanceof InventoryEntityPlayerSized) {
            InventoryEntityPlayerSized customInventory = (InventoryEntityPlayerSized) playerSizedInventory;
            baseInventorySize = customInventory.baseInventorySize;
        }

        if(baseInventorySize > 9) {
            this.inventoryLayout = InventoryLayout.PLAYER_HOT_OTHER_INV;
        }
        else if(baseInventorySize > 0) {
            this.inventoryLayout = InventoryLayout.PLAYER_INV_OTHER_HOT;
        }
        else {
            this.inventoryLayout = InventoryLayout.PLAYER_INV;
        }

        // Mainhand + Offhand + Armor [0, 5]
        // Mainhand Slot 0, Offhand Slot 1
        SlotConditionalEdit mainhandSlot = new SlotConditionalEdit(playerSizedInventory, 0, 77, 62 - 18, canEdit && hasEquipment);
        SlotConditionalEdit offhandSlot = new SlotConditionalEdit(playerSizedInventory, 1, 77, 62, canEdit && hasEquipment) {
            @Nullable
            @SideOnly(Side.CLIENT)
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        };

        if(!hasEquipment) {
            mainhandSlot.setDisabled();
            offhandSlot.setDisabled();;
        }
        this.addSlotToContainer(mainhandSlot);
        this.addSlotToContainer(offhandSlot);

        // Armor slots 2, 3, 4, 5
        for (int armorSlot = 0; armorSlot < 4; ++armorSlot) {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[armorSlot];
            int armorIndex = 2 + (3 - armorSlot);
            SlotConditionalEdit slot = new SlotConditionalEdit(playerSizedInventory, armorIndex, 8, 8 + armorSlot * 18, canEdit && hasEquipment) {
                public int getSlotStackLimit() { return 1; }

                public boolean isItemValid(ItemStack stack) { return stack.getItem().isValidArmor(stack, entityequipmentslot, ContainerEntity.this.entity); }

                public boolean canTakeStack(EntityPlayer playerIn) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }

                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture() { return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()]; }
            };
            if(!hasEquipment) {
                slot.setDisabled();
            }
            this.addSlotToContainer(slot);
        }

        // Hotbar [6, 15)
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            SlotConditionalEdit slot;
            if(inventoryLayout == InventoryLayout.PLAYER_INV_OTHER_HOT) {
                if(hotbarSlot < baseInventorySize) {
                    slot = new SlotConditionalEdit(playerSizedInventory, hotbarSlot + 6, 8 + hotbarSlot * 18, 142, canEdit);
                }
                else {
                    slot = new SlotConditionalEdit(playerSizedInventory, hotbarSlot + 6, 8 + hotbarSlot * 18, 142, false);
                    slot.setDisabled();
                }
            }
            else {
                slot = new SlotConditionalEdit(viewingPlayer.inventory, hotbarSlot + 6, 8 + hotbarSlot * 18, 142, true);
                if(!canEdit)
                    slot.setDisabled();
            }
            this.addSlotToContainer(slot);
        }

        // Main Inventory [15, 42)
        for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
            for (int inventoryCol = 0; inventoryCol < 9; ++inventoryCol) {
                SlotConditionalEdit slot;
                int index = inventoryCol + (inventoryRow * 9);
                if(inventoryLayout == InventoryLayout.PLAYER_HOT_OTHER_INV) {
                    if(index < baseInventorySize) {
                        slot = new SlotConditionalEdit(playerSizedInventory, index + 15, 8 + inventoryCol * 18, 84 + inventoryRow * 18, canEdit);
                    }
                    else {
                        slot = new SlotConditionalEdit(playerSizedInventory, index + 15, 8 + inventoryCol * 18, 84 + inventoryRow * 18, false);
                        slot.setDisabled();
                    }
                }
                else {
                    slot = new SlotConditionalEdit(viewingPlayer.inventory, index + 15, 8 + inventoryCol * 18, 84 + inventoryRow * 18, true);
                    if(!canEdit)
                        slot.setDisabled();
                }
                this.addSlotToContainer(slot);
            }
        }

        this.entityInventory = playerSizedInventory;
    }

    // Sync inventory changes, also handles mapping indexes back to proper arrays/base inventories
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!playerIn.world.isRemote) {
            boolean canEdit = playerIn.capabilities.isCreativeMode && playerIn.canUseCommand(3, "");
            if(canEdit) {
                for(int index = 0; index < this.entityInventory.getSizeInventory(); index++) {
                    this.entityInventory.setInventorySlotContents(index, this.entityInventory.getStackInSlot(index));
                }
            }
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    // Shift Click Behavior
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stackOriginalInSlot = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackRemainingInSlot = slot.getStack();
            stackOriginalInSlot = stackRemainingInSlot.copy();
            EntityEquipmentSlot entityEquipmentSlot = EntityLiving.getSlotForItemStack(stackOriginalInSlot);

            // 0 Mainhand
            // 1 Offhand
            // 2 Feet
            // 3 Legs
            // 4 Chest
            // 5 Helmet
            // [6, 15) Players Hotbar OR Small Inventory
            // [15, 42) 9x3 Inventory OR Player Inventory

            if (entityEquipmentSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !this.inventorySlots.get(5 - entityEquipmentSlot.getIndex()).getHasStack()) {
                int i = 5 - entityEquipmentSlot.getIndex();

                if (!this.mergeItemStack(stackRemainingInSlot, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (entityEquipmentSlot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get(1).getHasStack()) {
                if (!this.mergeItemStack(stackRemainingInSlot, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if(index < 6) {
                if (!this.mergeItemStack(stackRemainingInSlot, 6, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index < 15) {
                if (!this.mergeItemStack(stackRemainingInSlot, 15, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (index < this.inventorySlots.size()) {
                if (!this.mergeItemStack(stackRemainingInSlot, 6, 15, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackRemainingInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }


            if (stackRemainingInSlot.getCount() == stackOriginalInSlot.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stackRemainingInSlot);
        }

        return stackOriginalInSlot;
    }
}
