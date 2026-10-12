package scalinghealthissues.mixin.modcompat.infernalmobs;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.data.ConfigEnum;
import scalinghealthissues.enchantment.EnchantmentPools;
import scalinghealthissues.entity.DummyMerchant;

import java.util.ArrayList;
import java.util.Random;

@Mixin(InfernalMobsCore.class)
public abstract class InfernalMobsCore_EnchantmentMixin {

    @Shadow(remap = false) private ArrayList<Enchantment> enchantmentList;

    @WrapOperation(
            method = "dropRandomEnchantedItems",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemEnchantedBook;getEnchantedItemStack(Lnet/minecraft/enchantment/EnchantmentData;)Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack scalingHealthIssues_imInfernalMobsCore_dropRandomEnchantedItemsBookEnchantments(EnchantmentData enchantmentData, Operation<ItemStack> original, EntityLivingBase mob){
        switch (ConfigHandler.compat.infernalMobs.bookEnchantmentList) {
            case ENCHANT_RANDOMLY:
                return EnchantmentPools.applyRandomlyLootFunc(
                    new ItemStack(Items.BOOK),
                    mob.getRNG(),
                    EnchantmentPools.generateLootContext(
                            mob,
                            mob.getLastDamageSource()
                    ),
                    null
            );
            case LIBRARIAN_VILLAGER:
                return EnchantmentPools.getLibrarianTradeResult(mob.getRNG(), new DummyMerchant(mob));
            case VANILLA_REGISTRY:
            default:
        }
        return original.call(enchantmentData);
    }

    @WrapOperation(
            method = "dropRandomEnchantedItems",
            at = @At(value = "INVOKE", target = "Latomicstryker/infernalmobs/common/InfernalMobsCore;enchantRandomly(Ljava/util/Random;Lnet/minecraft/item/ItemStack;II)V"),
            remap = false
    )
    private void scalingHealthIssues_imInfernalMobsCore_dropRandomEnchantedItemsGearEnchantments(InfernalMobsCore instance, Random rand, ItemStack itemStack, int level, int modStr, Operation<Void> original, EntityLivingBase mob){
        switch (ConfigHandler.compat.infernalMobs.equipmentEnchantmentList) {
            case ENCHANT_WITH_LEVELS:
                EnchantmentPools.applyWithLevelsLootFunc(
                    itemStack,
                    rand,
                    EnchantmentPools.generateLootContext(
                            mob,
                            mob.getLastDamageSource()
                    ),
                    new RandomValueRange(level),
                    true
            );
            break;
            case ENCHANTMENT_HELPER:
                EnchantmentHelper.addRandomEnchantment(
                    rand,
                    itemStack,
                    level,
                    true
            );
            break;
            case FORGE_REGISTRY:
            default:
                original.call(instance, rand, itemStack, level, modStr);
        }
    }

    @Inject(
            method = "getRandomEnchantment",
            at = @At("HEAD"),
            remap = false
    )
    private void scalingHealthIssues_imInfernalMobsCore_getRandomEnchantmentBookEnchantmentsFallback(Random rand, CallbackInfoReturnable<EnchantmentData> cir){
        if(this.enchantmentList == null) {
            if (ConfigHandler.compat.infernalMobs.bookEnchantmentList == ConfigEnum.SingleEnchantmentList.FORGE_REGISTRY) {
                this.enchantmentList = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValuesCollection());
            }
        }
    }
}
