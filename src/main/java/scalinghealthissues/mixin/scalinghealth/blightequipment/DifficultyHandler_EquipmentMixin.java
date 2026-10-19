package scalinghealthissues.mixin.scalinghealth.blightequipment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.data.EnchantWithLevelsEntry;
import scalinghealthissues.config.folders.BlightConfig;
import scalinghealthissues.enchantment.EnchantmentPools;
import scalinghealthissues.handlers.BetterBlightHandler;

import java.util.List;
import java.util.Random;

@Mixin(value = DifficultyHandler.class, priority = 1001) // Run after config inversion fix
public abstract class DifficultyHandler_EquipmentMixin {

    @ModifyConstant(
            method = "makeEntityBlight",
            constant = @Constant(intValue = 4),
            remap = false
    )
    private int scalingHealthIssues_shDifficultyHandler_makeEntityBlightMaxTier(int highestTier){
        return ConfigHandler.mob.blight.equipment.maxExpectedTier;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "FIELD", target = "Lnet/silentchaos512/scalinghealth/config/Config;BLIGHT_EQUIPMENT_HIGHEST_COMMON_TIER:I"),
            remap = false
    )
    private int scalingHealthIssues_shDifficultyHandler_makeEntityBlightHighestCommonTier(int highestCommonTier, @Local(argsOnly = true) EntityLiving entityLiving){
        Integer value = BlightConfig.getRangeMapValue(BlightConfig.COMMON_TIER_BY_DIFFICULTY, ScalingHealthAPI.getEntityDifficulty(entityLiving));
        if(value != null) {
            return value;
        }
        return highestCommonTier;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "FIELD", target = "Lnet/silentchaos512/scalinghealth/config/Config;BLIGHT_EQUIPMENT_TIER_UP_CHANCE:F"),
            remap = false
    )
    private float scalingHealthIssues_shDifficultyHandler_makeEntityBlightTierUpChance(float tierUpChance, @Local(argsOnly = true) EntityLiving entityLiving){
        Float value = BlightConfig.getRangeMapValue(BlightConfig.TIER_UP_CHANCE_BY_DIFFICULTY, ScalingHealthAPI.getEntityDifficulty(entityLiving));
        if(value != null) {
            return value;
        }
        return tierUpChance;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", slice = "armor"),
                    @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", slice = "mainhand"),
                    @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", slice = "offhand")
            },
            slice = {
                    @Slice(id = "armor",
                            from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0),
                            to = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0)
                    ),
                    @Slice(id = "mainhand",
                            from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2),
                            to = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2)
                    ),
                    @Slice(id = "offhand",
                            from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 4),
                            to = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 4)
                    )
            }
    )
    private boolean scalingHealthIssues_shDifficultyHandler_makeEntityBlightReplaceEquipment(boolean isEmpty){
        return isEmpty || ConfigHandler.mob.blight.equipment.replaceExistingEquipment;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "FIELD", target = "Lnet/silentchaos512/scalinghealth/config/Config;BLIGHT_EQUIPMENT_ARMOR_PIECE_CHANCE:F"),
            remap = false
    )
    private float scalingHealthIssues_shDifficultyHandler_makeEntityBlightArmorChance(float armorChance, @Local(argsOnly = true) EntityLiving entityLiving){
        Float value = BlightConfig.getRangeMapValue(BlightConfig.ARMOR_CHANCE_BY_DIFFICULTY, ScalingHealthAPI.getEntityDifficulty(entityLiving));
        if(value != null) {
            return value;
        }
        return armorChance;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 1)
    )
    private boolean scalingHealthIssues_shDifficultyHandler_makeEntityBlightReplaceArmor(boolean isEmpty, @Local(argsOnly = true) EntityLiving entityLiving, @Local EntityEquipmentSlot slot, @Local ItemStack newEquipment){
        if(!ConfigHandler.mob.blight.equipment.replaceExistingEquipment)
            return isEmpty;

        return !BetterBlightHandler.isBetterThanCurrentEquipment(entityLiving, newEquipment, slot);
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "FIELD", target = "Lnet/silentchaos512/scalinghealth/config/Config;BLIGHT_EQUIPMENT_HAND_PIECE_CHANCE:F"),
            remap = false
    )
    private float scalingHealthIssues_shDifficultyHandler_makeEntityBlightHanceChance(float handChance, @Local(argsOnly = true) EntityLiving entityLiving){
        Float value = BlightConfig.getRangeMapValue(BlightConfig.HAND_CHANCE_BY_DIFFICULTY, ScalingHealthAPI.getEntityDifficulty(entityLiving));
        if(value != null) {
            return ConfigHandler.mixin.fixConfigEquipmentChance
                    ? value
                    : 1F - value;
        }
        return handChance;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "INVOKE", target = "Lnet/silentchaos512/scalinghealth/event/DifficultyHandler;selectEquipmentForSlot(Lnet/minecraft/inventory/EntityEquipmentSlot;I)Lnet/minecraft/item/ItemStack;", ordinal = 1),
            remap = false
    )
    private ItemStack scalingHealthIssues_shDifficultyHandler_makeEntityBlightReplaceBow(ItemStack original, @Local(name = "tier") int tier, @Local ItemStack oldEquipment){
        if(oldEquipment.getItem() instanceof ItemBow) {
            List<ItemStack> possibleBows = null;
            int configTier = tier;
            while(configTier >= 0 && (possibleBows == null || possibleBows.isEmpty())) {
                possibleBows = BlightConfig.BOW_BY_TIER.get(configTier--);
            }
            if(possibleBows != null && !possibleBows.isEmpty()) {
                return possibleBows.get(ScalingHealth.random.nextInt(possibleBows.size()));
            }

            if(!(original.getItem() instanceof ItemBow)) {
                return oldEquipment;
            }
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 3)
    )
    private boolean scalingHealthIssues_shDifficultyHandler_makeEntityBlightReplaceMainhand(boolean isEmpty, @Local(argsOnly = true) EntityLiving entityLiving, @Local ItemStack newEquipment){
        if(!ConfigHandler.mob.blight.equipment.replaceExistingEquipment)
            return isEmpty;

        return !BetterBlightHandler.isBetterThanCurrentEquipment(entityLiving, newEquipment, EntityEquipmentSlot.MAINHAND);
    }

    @ModifyExpressionValue(
            method = "makeEntityBlight",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 5)
    )
    private boolean scalingHealthIssues_shDifficultyHandler_makeEntityBlightReplaceOffhand(boolean isEmpty, @Local(argsOnly = true) EntityLiving entityLiving, @Local ItemStack newEquipment){
        if(!ConfigHandler.mob.blight.equipment.replaceExistingEquipment)
            return isEmpty;

        return !BetterBlightHandler.isBetterThanCurrentEquipment(entityLiving, newEquipment, EntityEquipmentSlot.OFFHAND);
    }

    @WrapOperation(
            method = "makeEntityBlight",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack scalingHealthIssues_shDifficultyHandler_makeEntityBlightEnchantItem(Random random, ItemStack stack, int level, boolean allowTreasure, Operation<ItemStack> original, @Local(argsOnly = true) EntityLiving entityLiving, @Local(argsOnly = true) Random rand){
        if(stack.isItemEnchantable()) {
            double difficulty = ScalingHealthAPI.getEntityDifficulty(entityLiving);
            EnchantWithLevelsEntry enchantConfig = BlightConfig.getRangeMapValue(BlightConfig.RANDOM_ENCHANT_BY_DIFFICULTY, difficulty);
            if(enchantConfig != null) {
                switch (ConfigHandler.mob.blight.enchantments.equipmentEnchantmentList) {
                    case ENCHANTMENT_HELPER:
                        return EnchantmentHelper.addRandomEnchantment(
                                random,
                                stack,
                                enchantConfig.getRandomValue(rand),
                                enchantConfig.isTreasure
                        );
                    case ENCHANT_WITH_LEVELS:
                        EntityPlayer player = entityLiving.getEntityWorld().getClosestPlayerToEntity(entityLiving, 128);
                        return EnchantmentPools.applyWithLevelsLootFunc(
                                stack,
                                random,
                                EnchantmentPools.generateLootContextWithPlayer(entityLiving, player),
                                new RandomValueRange(enchantConfig.min, enchantConfig.max),
                                enchantConfig.isTreasure
                        );
                }
            }
            return original.call(random, stack, level, allowTreasure);
        }
        return stack;
    }

    @ModifyConstant(
            method = "selectEquipmentForSlot",
            constant = @Constant(intValue = 4),
            remap = false
    )
    private int scalingHealthIssues_shDifficultyHandler_selectEquipmentForSlotMaxTier(int maxTier){
        return ConfigHandler.mob.blight.equipment.maxExpectedTier;
    }
}
