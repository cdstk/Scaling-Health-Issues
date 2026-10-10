package scalinghealthissues.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.silentchaos512.scalinghealth.config.Config;
import scalinghealthissues.client.gui.inventory.GuiScreenEntityInventory;
import scalinghealthissues.compat.ChampionsUtil;
import scalinghealthissues.compat.InfernalMobsUtil;
import scalinghealthissues.compat.ModLoadedUtil;
import scalinghealthissues.config.ConfigHandler;
import scalinghealthissues.inventory.ContainerEntity;
import scalinghealthissues.network.packet.PacketEntityViewableData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GuiHandler implements IGuiHandler {

    public static final int VIEW_ENTITY_INVENTORY = 1;

    private static final Cache<UUID, Integer> VIEWABLE_ENTITY_ID_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(70) // Mob Cap
            .build();
    private static final Cache<Integer, NBTTagCompound> VIEWABLE_ENTITY_DATA_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(70) // Mob Cap
            .build();
    private static final Cache<NBTTagCompound, Entity> GENERATED_VIEWED_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .maximumSize(70) // Mob Cap
            .build();

    /**
     * Server uses the full mapping in order to process queries in order to lookup/create cache
     * <p>
     * Client only needs the id -> nbt mapping in order to create a viewable cache
     *
     * @param uuid maps to loaded entity id, null won't make this mapping
     * @param id maps to entity nbt
     * @param nbt null won't make the id -> nbt mapping
     */
    public static void putViewableData(@Nullable UUID uuid, int id, @Nullable NBTTagCompound nbt) {
        if(uuid != null)
            VIEWABLE_ENTITY_ID_CACHE.put(uuid, id);
        if(nbt != null)
            VIEWABLE_ENTITY_DATA_CACHE.put(id, nbt);
    }

    @Nullable
    public static Integer getViewableEntityID(@Nonnull UUID uuid) {
        return VIEWABLE_ENTITY_ID_CACHE.getIfPresent(uuid);
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == VIEW_ENTITY_INVENTORY) {
            Entity targetEntity = getLoadedOrCreateCached(world, x);

            if(targetEntity instanceof EntityPlayer) {
                if(ConfigHandler.server.entityViewPlayers) {
                    return new ContainerEntity(targetEntity, player);
                }
                else {
                    player.sendMessage(new TextComponentTranslation("scalinghealthissues.gui.entityinventory.playererror").setStyle(new Style().setColor(TextFormatting.RED)));
                }
            }
            else if(targetEntity != null) {
                return new ContainerEntity(targetEntity, player);
            }
            else {
                player.sendMessage(new TextComponentTranslation("scalinghealthissues.gui.entityinventory.nullerror").setStyle(new Style().setColor(TextFormatting.RED)));
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == VIEW_ENTITY_INVENTORY) {
            Entity targetEntity = getLoadedOrCreateCached(world, x);

            if(targetEntity instanceof EntityPlayer) {
                return new GuiScreenEntityInventory(new ContainerEntity(targetEntity, player));
            }
            else if(targetEntity != null) {
                return new GuiScreenEntityInventory(new ContainerEntity(targetEntity, player));
            }
            else {
                player.sendMessage(new TextComponentTranslation("scalinghealthissues.gui.entityinventory.nullerror").setStyle(new Style().setColor(TextFormatting.DARK_RED)));
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if(!event.getEntityLiving().world.isRemote) {
            // Always cache Player Victim
            if (event.getEntityLiving() instanceof EntityPlayer) {
                Entity killer = event.getSource().getImmediateSource();
                if(killer instanceof EntityLivingBase) {
                    cachePlayerKillOrKiller(killer);
                }

                if(killer != event.getSource().getTrueSource()) {
                    killer = event.getSource().getTrueSource();
                    if(killer instanceof EntityLivingBase) {
                        cachePlayerKillOrKiller(killer);
                    }
                }
            }
            // Cache if victim would be notified in chat
            else if(shouldCacheKilled(event.getEntityLiving())) {
                cachePlayerKillOrKiller(event.getEntityLiving());

                // Cache their killer as they are in the msg
                Entity killer = event.getSource().getImmediateSource();
                if(killer instanceof EntityLivingBase) {
                    cachePlayerKillOrKiller(killer);
                }

                if(killer != event.getSource().getTrueSource()) {
                    killer = event.getSource().getTrueSource();
                    if(killer instanceof EntityLivingBase) {
                        cachePlayerKillOrKiller(killer);
                    }
                }
            }
        }
    }

    private static Entity getLoadedOrCreateCached(World world, int selectedEntityID) {
        Entity loadedEntity = world.getEntityByID(selectedEntityID);
        if(loadedEntity == null) {
            NBTTagCompound nbt = VIEWABLE_ENTITY_DATA_CACHE.getIfPresent(selectedEntityID);
            if(nbt != null) {
                loadedEntity = GENERATED_VIEWED_CACHE.getIfPresent(nbt);
                if(loadedEntity == null) {
                    loadedEntity = AnvilChunkLoader.readWorldEntity(nbt, world, false);
                    if(loadedEntity != null) {
                        GENERATED_VIEWED_CACHE.put(nbt, loadedEntity);
                    }
                }
            }
        }
        return loadedEntity;
    }

    private static void cachePlayerKillOrKiller(Entity entity) {
        if(entity instanceof EntityPlayer)
            return;

        NBTTagCompound nbt = entity.writeToNBT(new NBTTagCompound());
        addEntityTypeTag(nbt, entity);

        PacketEntityViewableData entityViewableData = new PacketEntityViewableData(entity, nbt);
        PacketHandler.instance.sendToDimension(entityViewableData, entity.dimension);

        putViewableData(entity.getUniqueID(), entity.getEntityId(), nbt);
    }

    private static boolean shouldCacheKilled(EntityLivingBase entityLivingBase) {
        if(!ConfigHandler.server.entityViewCacheKilled)
            return false;

        if(ModLoadedUtil.CHAMPIONS.isLoaded()) {
            if(ChampionsUtil.shouldAnnounceKill(entityLivingBase)) {
                return true;
            }
        }

        if(ModLoadedUtil.INFERNAL_MOBS.isLoaded()) {
            if(InfernalMobsUtil.shouldAnnounceKill(entityLivingBase)) {
                return true;
            }
        }

        for(ResourceLocation config : ConfigHandler.server.entityViewCacheAdditional) {
            if(config.equals(EntityList.getKey(entityLivingBase)))
                return true;
        }

        return ScalingHealthAPI.isBlight(entityLivingBase) && Config.Mob.Blight.notifyOnDeath;
    }

    public static void addEntityTypeTag(NBTTagCompound nbtTagCompound, Entity entity) {
        ResourceLocation resourcelocation = EntityList.getKey(entity);
        if (resourcelocation != null) {
            nbtTagCompound.setString("id", resourcelocation.toString());
        }
    }
}
