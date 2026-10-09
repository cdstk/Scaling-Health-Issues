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

public class InventoryEntityPlayerSized implements IInventory {

    /**
     *
     * @param entity
     * @return The existing inventory used by the entity excluding item arrays
     */
    public static IInventory getBaseInventory(Entity entity) {
        IInventory baseInventory = null;
        if(entity instanceof EntityVillager) {
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
    public static IInventory getInventoryWrapper(Entity entity) {
        IInventory playerSizedInventory = null;

        if (entity instanceof EntityPlayer) {
            playerSizedInventory = ((EntityPlayer) entity).inventory;
        }

        if(playerSizedInventory == null) {
            IInventory baseInventory = getBaseInventory(entity);

            playerSizedInventory = new InventoryEntityPlayerSized(entity, baseInventory);

            if(entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                playerSizedInventory.setInventorySlotContents(0, entityLivingBase.getHeldItemMainhand());
                playerSizedInventory.setInventorySlotContents(1, entityLivingBase.getHeldItemOffhand());
                for (int armorSlot = 0; armorSlot < 4; ++armorSlot) {
                    ItemStack armorStack = entityLivingBase.getItemStackFromSlot(ContainerEntity.VALID_EQUIPMENT_SLOTS[armorSlot]);
                    playerSizedInventory.setInventorySlotContents(2 + (3 - armorSlot), armorStack);
                }
            }

            int baseSlot = 0;
            if(baseInventory != null) {
                for (int inventoryRow = 0; inventoryRow < 4; ++inventoryRow) {
                    for (int inventoryCol = 0; inventoryCol < 9; ++inventoryCol) {
                        if(baseSlot < baseInventory.getSizeInventory()) {
                            int index = inventoryCol + (inventoryRow * 9);
                            playerSizedInventory.setInventorySlotContents(6 + index, baseInventory.getStackInSlot(baseSlot));
                            baseSlot++;
                        }
                    }
                }
            }
        }

        return playerSizedInventory;
    }

    public final NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public final NonNullList<ItemStack> armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
    public final NonNullList<ItemStack> mainInventory = NonNullList.withSize(36, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> allInventories;

    public final Entity entity;
    public final IInventory baseInventory;
    public int baseInventorySize;

    public InventoryEntityPlayerSized(@Nonnull Entity entity, @Nullable IInventory baseInventory) {
        this.allInventories = Arrays.asList(this.handInventory, this.armorInventory, this.mainInventory);
        this.entity = entity;
        this.baseInventory = baseInventory;

        this.baseInventorySize = baseInventory == null ? 0 : baseInventory.getSizeInventory();
        if(entity instanceof AbstractHorse_AccessorMixin) {
            this.baseInventorySize = ((AbstractHorse_AccessorMixin) entity).scalingHealthIssues$invokeGetInventorySize();
        }
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

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        // Sync changes
        if(!this.entity.getEntityWorld().isRemote) {
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