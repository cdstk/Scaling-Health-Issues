package scalinghealthissues.config.data;

import meldexun.betterconfig.api.Order;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import scalinghealthissues.ScalingHealthIssues;

import javax.annotation.Nullable;

public class ItemStackEntry {

    @Config.Name("Item")
    @Order(0)
    public ResourceLocation id;

    @Config.Name("Amount")
    @Order(1)
    public int amount = 1;

    @Config.Name("Meta Data")
    @Order(2)
    public int meta = 0;

    @Config.Name("Default Forge Capabilities")
    @Order(3)
    public String forgeCaps = "";

    public ItemStackEntry() {
        this.id = new ResourceLocation("iron_sword");
    }

    public ItemStackEntry(String id) {
        this.id = new ResourceLocation(id);
    }

    public ItemStackEntry(String id, int amount, int meta) {
        this(id);
        this.amount = amount;
        this.meta = meta;
    }

    public ItemStackEntry(ResourceLocation id) {
        this.id = id;
    }

    public ItemStackEntry(ResourceLocation id, int amount, int meta) {
        this(id);
        this.amount = amount;
        this.meta = meta;
    }

    @Nullable
    public NBTTagCompound getForgeCaps() {
        NBTTagCompound nbt = null;

        if(!this.forgeCaps.isEmpty()) {
            try {
                nbt = JsonToNBT.getTagFromJson(this.forgeCaps);
            }
            catch (NBTException e) {
                ScalingHealthIssues.LOGGER.error("Failed to create Forge Capabilities Tag for config entry, skipping: {}", e.toString());
            }
        }

        return nbt;
    }
}
