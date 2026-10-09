package scalinghealthissues.mixin.vanilla;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.inventory.ContainerHorseChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractHorse.class)
public interface AbstractHorse_AccessorMixin {

    @Accessor("horseChest")
    ContainerHorseChest scalingHealthIssues$getHorseChest();

    @Invoker("getInventorySize")
    int scalingHealthIssues$invokeGetInventorySize();
}
