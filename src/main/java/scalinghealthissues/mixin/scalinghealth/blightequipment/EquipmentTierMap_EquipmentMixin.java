package scalinghealthissues.mixin.scalinghealth.blightequipment;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.utils.EquipmentTierMap;
import net.silentchaos512.scalinghealth.utils.StackProducer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.config.folders.BlightConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(EquipmentTierMap.class)
public abstract class EquipmentTierMap_EquipmentMixin {

    @Mutable
    @Shadow(remap = false) @Final public int tierCount;

    @Shadow(remap = false) @Final public EntityEquipmentSlot slot;

    @Unique
    private Map<Integer, List<StackProducer>> scalingHealthIssues$apiEquipment = new HashMap<>();

    @ModifyVariable(
            method = "<init>",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true,
            remap = false
    )
    private static int scalingHealthIssues_shEquipmentTierMap_initNoLists(int tierCount){
        return 0;
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void scalingHealthIssues_shEquipmentTierMap_initSetMaxTier(int tierCount, EntityEquipmentSlot slot, CallbackInfo ci){
        this.tierCount = ConfigHandler.mob.blight.equipment.maxExpectedTier + 1;
    }

    @WrapMethod(
            method = "put(Lnet/silentchaos512/scalinghealth/utils/StackProducer;I)V",
            remap = false
    )
    private void scalingHealthIssues_shEquipmentTierMap_putAPIItemStack(StackProducer producer, int tier, Operation<Void> original){
        tier = Math.max(0, tier);
        this.scalingHealthIssues$apiEquipment.putIfAbsent(tier, new ArrayList<>());

        this.scalingHealthIssues$apiEquipment.get(tier).add(producer);
    }

    @WrapMethod(
            method = "getRandom",
            remap = false
    )
    private ItemStack scalingHealthIssues_shEquipmentTierMap_getRandomItemStackConfigurable(int tier, Operation<ItemStack> original){
        Random rand = ScalingHealth.random;
        int apiTier = Math.max(0, tier);
        int configTier = apiTier;
        List<StackProducer> apiList = null;
        List<ItemStack> configList = null;
        List<ItemStack> combinedList = new ArrayList<>();

        while(apiTier >= 0 && (apiList == null || apiList.isEmpty())) {
            apiList = this.scalingHealthIssues$apiEquipment.get(apiTier--);
        }

        while(configTier >= 0 && (configList == null || configList.isEmpty())) {
            configList = BlightConfig.getTieredItemsForSlot(this.slot).get(configTier--);
        }

        if(apiList != null) {
            apiList.forEach(stackProducer -> {
                ItemStack copy = stackProducer.get(rand).copy();
                combinedList.add(copy);
            });
        }
        if(configList != null) {
            configList.forEach(itemStack -> {
                combinedList.add(itemStack.copy());
            });
        }

        if(combinedList.isEmpty())
            return ItemStack.EMPTY;

        return combinedList.get(rand.nextInt(combinedList.size()));
    }

    @WrapMethod(
            method = "get",
            remap = false
    )
    private ItemStack scalingHealthIssues_shEquipmentTierMap_getItemStackConfigurable(int tier, int index, Operation<ItemStack> original){
        tier = Math.max(0, tier);

        Random rand = ScalingHealth.random;
        List<StackProducer> apiList = this.scalingHealthIssues$apiEquipment.get(tier);
        List<ItemStack> configList = BlightConfig.getTieredItemsForSlot(this.slot).get(tier);
        List<ItemStack> combinedList = new ArrayList<>();

        if(apiList != null) {
            apiList.forEach(stackProducer -> {
                ItemStack copy = stackProducer.get(rand).copy();
                combinedList.add(copy);
            });
        }
        if(configList != null) {
            configList.forEach(itemStack -> {
                combinedList.add(itemStack.copy());
            });
        }

        if(combinedList.isEmpty())
            return ItemStack.EMPTY;

        return combinedList.get(rand.nextInt(combinedList.size()));
    }
}
