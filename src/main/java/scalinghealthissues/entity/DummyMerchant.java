package scalinghealthissues.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

// Used to prevent param is null errors when used in places where the instance isn't even used at all
public class DummyMerchant implements IMerchant {

    private final Entity entity;

    public DummyMerchant(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setCustomer(@Nullable EntityPlayer player) {}

    @Nullable
    @Override
    public EntityPlayer getCustomer() { return null; }

    @Nullable
    @Override
    public MerchantRecipeList getRecipes(EntityPlayer player) {return null; }

    @Override
    public void setRecipes(@Nullable MerchantRecipeList recipeList) {}

    @Override
    public void useRecipe(MerchantRecipe recipe) {}

    @Override
    public void verifySellingItem(ItemStack stack) {}

    @Override
    public ITextComponent getDisplayName() {
        return this.entity.getDisplayName();
    }

    @Override
    public World getWorld() { return this.entity.world; }

    @Override
    public BlockPos getPos() { return this.entity.getPosition(); }
}
