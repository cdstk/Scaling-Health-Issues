package scalinghealthissues.mixin.scalinghealth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.silentchaos512.scalinghealth.entity.EntityBlightFire;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityBlightFire.class)
public abstract class EntityBlightFire_ParentIDMixin extends Entity {

    @Shadow(remap = false)
    @Final private static String NBT_PARENT;
    @Shadow(remap = false) private EntityLivingBase parent;

    @Unique
    private static final String NBT_PARENT_UUID = "ParentBlightUUID";

    @Unique
    private Integer scalingHealthIssues$parentID = null;
    @Unique
    private UUID scalingHealthIssues$parentUUID = null;

    public EntityBlightFire_ParentIDMixin(World world) {
        super(world);
    }

    @Inject(
            method = "onUpdate",
            at = @At("HEAD")
    )
    private void scalingHealthIssues_shEntityBlightFire_onUpdateLoadNBTParent(CallbackInfo ci){
        // Most accurate UUID
        if(this.scalingHealthIssues$parentUUID != null) {
            if(this.world instanceof WorldServer) {
                Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.scalingHealthIssues$parentUUID);
                if(entity instanceof EntityLivingBase && this.parent == null)
                    this.parent = (EntityLivingBase) entity;

                this.scalingHealthIssues$parentUUID = null;
            }
        }

        // Process old numerical ID
        if(this.scalingHealthIssues$parentID != null) {
            Entity entity = this.world.getEntityByID(this.scalingHealthIssues$parentID);

            if(entity instanceof EntityLivingBase && this.parent == null)
                this.parent = (EntityLivingBase) entity;

            this.scalingHealthIssues$parentID = null;
        }
    }

    @ModifyExpressionValue(
            method = "readEntityFromNBT",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;hasKey(Ljava/lang/String;)Z")
    )
    private boolean scalingHealthIssues_shEntityBlightFire_readEntityFromNBTParent(boolean hasNumericalID, NBTTagCompound compound){
        if(compound.hasKey(NBT_PARENT_UUID)) {
            if(this.world instanceof WorldServer) {
                compound.removeTag(NBT_PARENT);
                this.scalingHealthIssues$parentUUID = compound.getUniqueId(NBT_PARENT_UUID);
                return false;
            }
        }
        return hasNumericalID;
    }

    @WrapOperation(
            method = "writeEntityToNBT",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setInteger(Ljava/lang/String;I)V")
    )
    private void scalingHealthIssues_shEntityBlightFire_writeEntityToNBTParent(NBTTagCompound instance, String key, int value, Operation<Void> original){
        // Replace numerical ID with UUID
        instance.setUniqueId(NBT_PARENT_UUID, this.parent.getUniqueID());
    }

    @ModifyExpressionValue(
            method = "readSpawnData",
            at = @At(value = "INVOKE", target = "Lio/netty/buffer/ByteBuf;readInt()I"),
            remap = false
    )
    private int scalingHealthIssues_shEntityBlightFire_readSpawnDataToLoad(int entityID){
        this.scalingHealthIssues$parentID = entityID; // Parent may not exist on client yet
        return entityID;
    }
}
