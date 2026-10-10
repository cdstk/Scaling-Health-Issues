package scalinghealthissues.inventory;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
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

    public final boolean canEditInventory;
    public final Entity entity;
    public final InventoryEntityWrapper inventoryWrapper; // Wrapped base + item arrays
    public final InventoryLayout inventoryLayout;

    public enum InventoryLayout {
        PLAYER,                 // Looking at player, can not edit ever
        PLAYER_INV_OTHER_HOT,   // Hotbar is entity inventory
        PLAYER_HOT_OTHER_INV,   // 9x3 is entity inventory
        OTHER_BOTH,             // Entity inventory is entire screen
        OTHER_HUGE              // Multiple Screens
    }
    // TODO Implementation huge inventory such as Astikor Cart Cargo

    // 0 Mainhand
    // 1 Offhand
    // 2 Feet
    // 3 Legs
    // 4 Chest
    // 5 Helmet
    // [6, 14] Players Hotbar OR Small Inventory
    //      ^
    //  THESE CAN SWAP to [6, 32] [33, 42) inventory stores 9x4 in [6, 42)
    //      v
    // [15, 42) 9x3 Inventory OR Player Inventory

    public ContainerEntity(Entity entity, EntityPlayer viewingPlayer) {
        InventoryEntityWrapper wrapper = InventoryEntityWrapper.createInventoryWrapper(entity);
        // Check if the viewer has OP permission level 3 or higher
        this.canEditInventory = viewingPlayer.capabilities.isCreativeMode
                && viewingPlayer.canUseCommand(3, "")
                && wrapper.nonPlayer;
        this.entity = entity;
        
        if(wrapper.baseInventory instanceof InventoryPlayer) {
            this.inventoryLayout = InventoryLayout.OTHER_BOTH;
        }
        else if(wrapper.baseInventorySize > 27) {
            this.inventoryLayout = InventoryLayout.OTHER_HUGE;
        }
        else if(wrapper.baseInventorySize > 9) {
            this.inventoryLayout = InventoryLayout.PLAYER_HOT_OTHER_INV;
        }
        else if(wrapper.baseInventorySize > 0) {
            this.inventoryLayout = InventoryLayout.PLAYER_INV_OTHER_HOT;
        }
        else {
            this.inventoryLayout = InventoryLayout.PLAYER;
        }

        boolean hasEquipment = this.entity instanceof EntityLivingBase;
        // Mainhand + Offhand + Armor [0, 5]
        // Mainhand Slot 0, Offhand Slot 1
        SlotConditionalEdit mainhandSlot = new SlotConditionalEdit(wrapper, 0, 77, 62 - 18, this.canEditInventory && hasEquipment);
        SlotConditionalEdit offhandSlot = new SlotConditionalEdit(wrapper, 1, 77, 62, this.canEditInventory && hasEquipment) {
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
            SlotConditionalEdit slot = new SlotConditionalEdit(wrapper, armorIndex, 8, 8 + armorSlot * 18, this.canEditInventory && hasEquipment) {
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

        // Wrapper SHOULD ALWAYS start at index 6
        // Viewer inventory needs to shift to map back to their inventory
        // 0 is where Player's Hotbar starts
        // 9 is where Player's Inventory starts

        // Hotbar [6, 15)
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            SlotConditionalEdit slot;
            // Hotbar is Other Player
            if(this.inventoryLayout == InventoryLayout.OTHER_BOTH) {
                slot = new SlotConditionalEdit(viewingPlayer.inventory, hotbarSlot, 8 + hotbarSlot * 18, 142, false);
            }
            // Hotbar is Other Inventory
            else if(this.inventoryLayout == InventoryLayout.PLAYER_INV_OTHER_HOT) {
                if(hotbarSlot < wrapper.baseInventorySize) {
                    slot = new SlotConditionalEdit(wrapper, hotbarSlot + 6, 8 + hotbarSlot * 18, 142, this.canEditInventory);
                }
                else {
                    slot = new SlotConditionalEdit(wrapper, hotbarSlot + 6, 8 + hotbarSlot * 18, 142, false);
                    slot.setDisabled();
                }
            }
            else {
                slot = new SlotConditionalEdit(viewingPlayer.inventory, hotbarSlot, 8 + hotbarSlot * 18, 142, true);
                if(!this.canEditInventory)
                    slot.setDisabled();
            }
            this.addSlotToContainer(slot);
        }

        // Main Inventory [15, 42)
        for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
            for (int inventoryCol = 0; inventoryCol < 9; ++inventoryCol) {
                SlotConditionalEdit slot;
                int index = inventoryCol + (inventoryRow * 9);
                // Inventory is Other Player
                if(this.inventoryLayout == InventoryLayout.OTHER_BOTH) {
                    slot = new SlotConditionalEdit(viewingPlayer.inventory, index + 9, 8 + inventoryCol * 18, 84 + inventoryRow * 18, false);
                }
                // Inventory is Other
                else if(inventoryLayout == InventoryLayout.PLAYER_HOT_OTHER_INV) {
                    if(index < wrapper.baseInventorySize) {
                        slot = new SlotConditionalEdit(wrapper, index + 6, 8 + inventoryCol * 18, 84 + inventoryRow * 18, this.canEditInventory);
                    }
                    else {
                        slot = new SlotConditionalEdit(wrapper, index + 6, 8 + inventoryCol * 18, 84 + inventoryRow * 18, false);
                        slot.setDisabled();
                    }
                }
                else {
                    slot = new SlotConditionalEdit(viewingPlayer.inventory, index + 9, 8 + inventoryCol * 18, 84 + inventoryRow * 18, true);
                    if(!this.canEditInventory)
                        slot.setDisabled();
                }
                this.addSlotToContainer(slot);
            }
        }

        this.inventoryWrapper = wrapper;
    }

    // Sync inventory changes, also handles mapping indexes back to proper arrays/base inventories
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!playerIn.world.isRemote) {
            if(this.canEditInventory) {
                this.inventoryWrapper.syncAllContents(playerIn);
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
