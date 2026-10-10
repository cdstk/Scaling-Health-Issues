package scalinghealthissues.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import scalinghealthissues.mixin.vanilla.AbstractHorse_AccessorMixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class InventoryEntityWrapper implements IInventory {

    /**
     *
     * @param entity
     * @return The existing inventory used by the entity excluding item arrays
     */
    public static IInventory getBaseInventory(Entity entity) {
        IInventory baseInventory = null;
        if(entity instanceof EntityPlayer) {
            baseInventory = ((EntityPlayer) entity).inventory;
        }
        else if(entity instanceof EntityVillager) {
            baseInventory = ((EntityVillager) entity).getVillagerInventory();
        }
        else if(entity instanceof AbstractHorse_AccessorMixin) {
            baseInventory = ((AbstractHorse_AccessorMixin) entity).scalingHealthIssues$getHorseChest();
        }
        else if (entity instanceof IInventory) {
            baseInventory = (IInventory) entity;
        }
        return baseInventory;
    }

    /**
     *
     * @param entity To get an inventory from. Parses instances of IInventory,
     *               various optional inventories, and EntityLivingBase item arrays
     * @return An inventory that can read and modify the items stored in the entity
     */
    public static InventoryEntityWrapper createInventoryWrapper(Entity entity) {
        IInventory baseInventory = getBaseInventory(entity);
        InventoryEntityWrapper inventoryWrapper = new InventoryEntityWrapper(entity, baseInventory);

        // 0 Mainhand
        // 1 Offhand
        // 2 Feet
        // 3 Legs
        // 4 Chest
        // 5 Helmet
        // [6, 42) 9x4 Inventory

        // Fixed size of 6
        if(entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            inventoryWrapper.setInventorySlotContentsNoUpdate(0, entityLivingBase.getHeldItemMainhand());
            inventoryWrapper.setInventorySlotContentsNoUpdate(1, entityLivingBase.getHeldItemOffhand());
            for (int armorSlot = 0; armorSlot < 4; ++armorSlot) {
                ItemStack armorStack = entityLivingBase.getItemStackFromSlot(ContainerEntity.VALID_EQUIPMENT_SLOTS[armorSlot]);
                inventoryWrapper.setInventorySlotContentsNoUpdate(2 + (3 - armorSlot), armorStack);
            }
        }

        // Size is flexible
        if(baseInventory != null) {
            int slotCount = 0;
            for (int inventoryRow = 0; inventoryRow < 4; ++inventoryRow) {
                for (int inventoryCol = 0; inventoryCol < 9; ++inventoryCol) {
                    if (slotCount < baseInventory.getSizeInventory()) {
                        int index = inventoryCol + (inventoryRow * 9);
                        inventoryWrapper.setInventorySlotContentsNoUpdate(6 + index, baseInventory.getStackInSlot(index));
                        slotCount++;
                    }
                }
            }
        }

        return inventoryWrapper;
    }

    public final NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public final NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
    public final NonNullList<ItemStack> mainInventory;
    private final List<NonNullList<ItemStack>> allInventories;

    public final Entity entity;
    public final IInventory baseInventory;

    public final boolean nonPlayer;
    public int baseInventorySize;

    public InventoryEntityWrapper(@Nonnull Entity entity, @Nullable IInventory baseInventory) {
        this.baseInventorySize = baseInventory == null ? 0 : baseInventory.getSizeInventory();
        if(entity instanceof AbstractHorse_AccessorMixin) {
            this.baseInventorySize = ((AbstractHorse_AccessorMixin) entity).scalingHealthIssues$invokeGetInventorySize();
        }

        this.mainInventory = NonNullList.withSize(this.baseInventorySize, ItemStack.EMPTY);

        this.allInventories = Arrays.asList(this.handInventory, this.armorInventory, this.mainInventory);
        this.entity = entity;
        this.baseInventory = baseInventory;
        this.nonPlayer = !(entity instanceof EntityPlayer);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        List<ItemStack> validList = null;

        for (NonNullList<ItemStack> storedList : this.allInventories) {
            if (index < storedList.size()) {
                validList = storedList;
                break;
            }

            index -= storedList.size();
        }

        return validList != null && !validList.get(index).isEmpty() ? ItemStackHelper.getAndSplit(validList, index, count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        NonNullList<ItemStack> validList = null;

        for (NonNullList<ItemStack> storedList : this.allInventories) {
            if (index < storedList.size()) {
                validList = storedList;
                break;
            }

            index -= storedList.size();
        }

        if (validList != null && !validList.get(index).isEmpty()) {
            ItemStack itemstack = validList.get(index);
            validList.set(index, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public void syncAllContents(EntityPlayer playerIn) {
        for(int index = 0; index < this.getSizeInventory(); index++) {
            this.setInventorySlotContents(index, this.getStackInSlot(index));
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        // Sync changes
        if(!this.entity.getEntityWorld().isRemote && this.nonPlayer) {
            if(index == 0) {
                this.entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
            }
            else if(index == 1) {
                this.entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack);
            }
            else if(index < 6) {
                this.entity.setItemStackToSlot(ContainerEntity.VALID_EQUIPMENT_SLOTS[2 + (3 - index)], stack);
            }
            else if(index < this.getSizeInventory()) {
                if(this.baseInventory != null) {
                    int relativeIndex = index - 6;
                    if(relativeIndex < this.baseInventory.getSizeInventory()) {
                        this.baseInventory.setInventorySlotContents(relativeIndex, stack);
                    }
                }
            }
        }

        this.setInventorySlotContentsNoUpdate(index, stack);
    }

    public void setInventorySlotContentsNoUpdate(int index, ItemStack stack) {
        NonNullList<ItemStack> validList = null;

        for (NonNullList<ItemStack> storedList : this.allInventories) {
            if (index < storedList.size()) {
                validList = storedList;
                break;
            }

            index -= storedList.size();
        }

        if (validList != null) {
            validList.set(index, stack);
        }
    }

    @Override
    public int getSizeInventory() {
        return this.mainInventory.size() + this.armorInventory.size() + this.handInventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.mainInventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemstack1 : this.armorInventory) {
            if (!itemstack1.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemstack2 : this.handInventory) {
            if (!itemstack2.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        List<ItemStack> list = null;

        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            if (index < nonnulllist.size()) {
                list = nonnulllist;
                break;
            }

            index -= nonnulllist.size();
        }

        return list == null ? ItemStack.EMPTY : list.get(index);
    }

    @Override
    public String getName() {
        return "container.inventory";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.entity != null && this.entity.isAddedToWorld();
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() {
        for (List<ItemStack> list : this.allInventories) {
            list.clear();
        }
    }
}