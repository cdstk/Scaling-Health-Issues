package scalinghealthissues.mixin.modcompat.infernalmobs;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.RandomValueRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.data.ConfigEnum;
import scalinghealthissues.config.data.ValueRangeEntry;
import scalinghealthissues.enchantment.EnchantmentPools;
import scalinghealthissues.handlers.BetterBlightHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(InfernalMobsCore.class)
public abstract class InfernalMobsCore_EquipmentMixin {

    @Shadow(remap = false) private ArrayList<ItemStack> dropIdListElite;
    @Shadow(remap = false) private ArrayList<ItemStack> dropIdListUltra;
    @Shadow(remap = false) private ArrayList<ItemStack> dropIdListInfernal;

    @Shadow(remap = false) protected abstract EnchantmentData getRandomEnchantment(Random rand);
    @Shadow(remap = false) protected abstract void enchantRandomly(Random rand, ItemStack itemStack, int itemEnchantability, int modStr);

    @Unique
    private static final Map<EntityEquipmentSlot, ArrayList<ItemStack>> scalingHealthIssues$ELITE_EQUIPMENT_LOOT = new HashMap<>();
    @Unique
    private static final Map<EntityEquipmentSlot, ArrayList<ItemStack>> scalingHealthIssues$ULTRA_EQUIPMENT_LOOT = new HashMap<>();
    @Unique
    private static final Map<EntityEquipmentSlot, ArrayList<ItemStack>> scalingHealthIssues$INFERNAL_EQUIPMENT_LOOT = new HashMap<>();

    @Inject(
            method = "loadConfig",
            at = @At("TAIL"),
            remap = false
    )
    private void scalingHealthIssues_imInfernalMobsCore_loadConfigReference(CallbackInfo ci){
        scalingHealthIssues$ELITE_EQUIPMENT_LOOT.clear();
        scalingHealthIssues$ULTRA_EQUIPMENT_LOOT.clear();
        scalingHealthIssues$INFERNAL_EQUIPMENT_LOOT.clear();

        this.dropIdListElite.forEach(itemStack -> {
            EntityEquipmentSlot equipmentSlot = BetterBlightHandler.getReasonableSlotForItemStack(itemStack);
            scalingHealthIssues$ULTRA_EQUIPMENT_LOOT.putIfAbsent(equipmentSlot, new ArrayList<>());
            scalingHealthIssues$ULTRA_EQUIPMENT_LOOT.get(equipmentSlot).add(itemStack);
        });
        this.dropIdListUltra.forEach(itemStack -> {
            EntityEquipmentSlot equipmentSlot = BetterBlightHandler.getReasonableSlotForItemStack(itemStack);
            scalingHealthIssues$ELITE_EQUIPMENT_LOOT.putIfAbsent(equipmentSlot, new ArrayList<>());
            scalingHealthIssues$ELITE_EQUIPMENT_LOOT.get(equipmentSlot).add(itemStack);
        });
        this.dropIdListInfernal.forEach(itemStack -> {
            EntityEquipmentSlot equipmentSlot = BetterBlightHandler.getReasonableSlotForItemStack(itemStack);
            scalingHealthIssues$INFERNAL_EQUIPMENT_LOOT.putIfAbsent(equipmentSlot, new ArrayList<>());
            scalingHealthIssues$INFERNAL_EQUIPMENT_LOOT.get(equipmentSlot).add(itemStack);
        });
    }

    @Inject(
            method = "processEntitySpawn",
            at = @At(value = "INVOKE", target = "Latomicstryker/infernalmobs/common/MobModifier;onSpawningComplete(Lnet/minecraft/entity/EntityLivingBase;)V"),
            remap = false
    )
    private void scalingHealthIssues_imInfernalMobsCore_processEntitySpawnEquipmentLoot(EntityLivingBase entity, CallbackInfo ci, @Local MobModifier mod){
        int modifierCount = mod.getModSize();
        /* 0 for elite, 1 for ultra, 2 for infernal */
        int prefix = (modifierCount <= 5) ? 0 : (modifierCount <= 10) ? 1 : 2;

        for(EntityEquipmentSlot equipmentSlot : EntityEquipmentSlot.values()) {
            ItemStack current = entity.getItemStackFromSlot(equipmentSlot);
            ItemStack replacement = null;
            boolean canReplace = current.isEmpty();

            if(!canReplace && ConfigHandler.compat.infernalMobs.replaceExistingEquipment) {
                replacement = scalingHealthIssues$getInfernalEquipment(entity, prefix, equipmentSlot);
                canReplace = BetterBlightHandler.isBetterThanCurrentEquipment(entity, replacement, equipmentSlot);
            }

            if(canReplace) {
                if(replacement == null) {
                    replacement = scalingHealthIssues$getInfernalEquipment(entity, prefix, equipmentSlot);
                }

                replacement = this.scalingHealthIssues$initEquipment(entity, replacement, modifierCount, prefix);
                entity.setItemStackToSlot(equipmentSlot, replacement);
            }
        }
    }

    @Unique
    private ItemStack scalingHealthIssues$initEquipment(EntityLivingBase mob, ItemStack itemStack, int enchantmentLimitModifier, int prefix) {
        Item item = itemStack.getItem();
        if (item != Items.AIR) {
            if (item instanceof ItemEnchantedBook) {
                itemStack = ItemEnchantedBook.getEnchantedItemStack(this.getRandomEnchantment(mob.getRNG()));
            }
            else {
                Random rand = mob.world.rand;
                RandomValueRange range;
                ValueRangeEntry rangeConfig = ConfigHandler.compat.infernalMobs.infernalEnchantability.get(ConfigEnum.InfernalTier.values()[prefix]);
                if(rangeConfig == null) {
                    range = new RandomValueRange(item.getItemEnchantability());
                }
                else {
                    range = new RandomValueRange(rangeConfig.min, rangeConfig.max);
                }

                if(ConfigHandler.compat.infernalEnchantmentPool) {
                    switch (ConfigHandler.compat.infernalMobs.equipmentEnchantmentList) {
                        case ENCHANT_WITH_LEVELS:
                            EnchantmentPools.applyWithLevelsLootFunc(
                                    itemStack,
                                    rand,
                                    EnchantmentPools.generateLootContext(
                                            mob,
                                            mob.getLastDamageSource()
                                    ),
                                    range,
                                    true
                            );
                            break;
                        case ENCHANTMENT_HELPER:
                            EnchantmentHelper.addRandomEnchantment(
                                    rand,
                                    itemStack,
                                    range.generateInt(mob.world.rand),
                                    true
                            );
                            break;
                        case FORGE_REGISTRY:
                        default:
                            this.enchantRandomly(rand, itemStack, range.generateInt(rand), Math.min(5, enchantmentLimitModifier));
                    }
                }
                else {
                    this.enchantRandomly(rand, itemStack, range.generateInt(rand), Math.min(5, enchantmentLimitModifier));
                }
            }
        }
        return itemStack;
    }

    @Unique
    private static ItemStack scalingHealthIssues$getInfernalEquipment(EntityLivingBase entityLivingBase, int prefix, EntityEquipmentSlot equipmentSlot) {
        ItemStack configRef;
        List<ItemStack> configList;

        switch (prefix) {
            case 0: configList = scalingHealthIssues$ELITE_EQUIPMENT_LOOT.get(equipmentSlot); break;
            case 1: configList = scalingHealthIssues$ULTRA_EQUIPMENT_LOOT.get(equipmentSlot); break;
            case 2: configList = scalingHealthIssues$INFERNAL_EQUIPMENT_LOOT.get(equipmentSlot); break;
            default: configList = Collections.emptyList();
        }
        if(configList == null || configList.isEmpty()) {
            configRef = ItemStack.EMPTY;
        }
        else {
            configRef = configList.get(entityLivingBase.world.rand.nextInt(configList.size()));
        }

        if(configRef == null)
            configRef = ItemStack.EMPTY;

        return configRef.copy();
    }

}
