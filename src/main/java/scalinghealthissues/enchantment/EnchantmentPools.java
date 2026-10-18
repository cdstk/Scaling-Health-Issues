package scalinghealthissues.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.EnchantRandomly;
import net.minecraft.world.storage.loot.functions.EnchantWithLevels;
import scalinghealthissues.mixin.vanilla.EntityLivingBase_Accessor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class EnchantmentPools {

    // TODO Add Socketed Sockets once official 1.0.0 is released, base+addons are all indev and not on CF

    private static final LootCondition[] EMPTY_CONDITION = new LootCondition[] {};

    /**
     * @return An Enchanted Book generated from the Librarian Trade list
     */
    public static ItemStack getLibrarianTradeResult(Random random, IMerchant dummy) {
        EntityVillager.ListEnchantedBookForEmeralds librarianTrade = new EntityVillager.ListEnchantedBookForEmeralds();
        MerchantRecipeList result = new MerchantRecipeList();
        librarianTrade.addMerchantRecipe(dummy, result, random);
        return result.get(0).getItemToSell();
    }

    /**
     * @return An ItemStack with the minecraft:enchant_randomly loot function applied
     */
    public static ItemStack applyRandomlyLootFunc(ItemStack itemStack, Random random, LootContext context, @Nullable List<Enchantment> enchantmentsIn) {
        EnchantRandomly lootFunc = new EnchantRandomly(EMPTY_CONDITION, enchantmentsIn);
        return lootFunc.apply(itemStack, random, context);
    }

    /**
     * @return An ItemStack with the minecraft:enchant_with_levels loot function applied
     */
    public static ItemStack applyWithLevelsLootFunc(ItemStack itemStack, Random random, LootContext context, RandomValueRange randomRange, boolean isTreasureIn) {
        EnchantWithLevels lootFunc = new EnchantWithLevels(EMPTY_CONDITION, randomRange, isTreasureIn);
        return lootFunc.apply(itemStack, random, context);
    }


    /**
     * @return A Loot Context based on EntityLiving.dropLoot
     */
    public static LootContext generateLootContext(EntityLivingBase victim, @Nullable DamageSource source) {
        LootContext.Builder contextBuilder = new LootContext.Builder((WorldServer)victim.world).withLootedEntity(victim);
        if(source != null) contextBuilder.withDamageSource(source);

        if(victim instanceof EntityLivingBase_Accessor) {
            EntityPlayer attackingPlayer = ((EntityLivingBase_Accessor) victim).scalingHealthIssues$getAttackingPlayer();
            boolean recentlyHit = ((EntityLivingBase_Accessor) victim).scalingHealthIssues$getRecentlyHit() > 0;
            if (recentlyHit && attackingPlayer != null) {
                contextBuilder = contextBuilder.withPlayer(attackingPlayer).withLuck(attackingPlayer.getLuck());
            }
        }
        return contextBuilder.build();
    }

    public static LootContext generateLootContextWithPlayer(EntityLivingBase victim, EntityPlayer player) {
        LootContext.Builder contextBuilder = new LootContext.Builder((WorldServer)victim.world).withLootedEntity(victim);

        if(player != null) {
            contextBuilder = contextBuilder.withPlayer(player).withLuck(player.getLuck());
        }

        return contextBuilder.build();
    }
}
