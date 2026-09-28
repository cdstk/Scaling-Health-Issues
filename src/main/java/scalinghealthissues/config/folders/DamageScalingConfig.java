package scalinghealthissues.config.folders;

import fermiumbooter.annotations.MixinConfig;
import gnu.trove.map.hash.THashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ForgeConfigHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@MixinConfig(name = ScalingHealthIssues.MODID)
public class DamageScalingConfig {

    private static final Set<Class<?>> passiveWhitelist = new HashSet<>();
    private static final Set<Class<?>> passiveBlacklist = new HashSet<>();
    private static final Set<Class<?>> hostileWhitelist = new HashSet<>();
    private static final Set<Class<?>> hostileBlacklist = new HashSet<>();
    private static final Set<Class<?>> generalWhitelist = new HashSet<>();
    private static final Set<Class<?>> generalBlacklist = new HashSet<>();

    // TODO One to many map bc I forgot they existed lmao and use Predicate for mixed Class + ID
    private static final Map<String, Float> nullTrueSourceScales = new THashMap<>();
    private static final Map<Class<?>, Float> wildcardTrueSourceScales = new THashMap<>();
    private static final Map<String, Map<Class<?>, Float>> classTrueSourceScales = new THashMap<>();

    private static final Map<ResourceLocation, Float> wildcardIdTrueSourceScales = new THashMap<>();
    private static final Map<String, Map<ResourceLocation, Float>> idTrueSourceScales = new THashMap<>();
    private static final Map<String, Map<String, Float>> modTrueSourceScales = new THashMap<>();

    private static final Map<String, Float> nullImmediateSourceScales = new THashMap<>();
    private static final Map<Class<?>, Float> wildcardImmediateSourceScales = new THashMap<>();
    private static final Map<String, Map<Class<?>, Float>> classImmediateSourceScales = new THashMap<>();

    private static final Map<ResourceLocation, Float> wildcardIdImmediateSourceScales = new THashMap<>();
    private static final Map<String, Map<ResourceLocation, Float>> idImmediateSourceScales = new THashMap<>();
    private static final Map<String, Map<String, Float>> modImmediateSourceScales = new THashMap<>();

    @Config.Comment({
            "Overhauls the Damage Scaling Mechanic (Main Config in Mod Options -> Scaling Health -> player -> damage)",
            "Hostile Mobs no longer limited to instances of IMob, a limited group.",
            "Passive Mobs no longer limited any non Player or IMob, a very wide group.",
            "\tConfigs to Whitelist/Blacklist entity Classes",
            "Damage Scaling is able to be set based on True Source entity and Immediate Source entity.",
            "\tConfigs to Whitelist/Blacklist by Class, Entity ID, and Mod ID",
            "\tTreated as additional scaling rules where if none match, falls back to vanilla Scaling Health"
    })
    @Config.Name("Mixin: Overhaul Damage Scaling")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.scalinghealth.overhauldmgscaling.json", defaultValue = true)
    @Config.RequiresMcRestart
    public boolean overhaulDamageScaling = true;

    private final static String passiveEntityWhitelistName = "Passive Entity Class Whitelist";
    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The whitelist is check AFTER the blacklist"
    })
    @Config.Name(passiveEntityWhitelistName)
    public String[] passiveEntityWhitelist = {
            IAnimals.class.getName()
    };

    private final static String passiveEntityBlacklistName = "Passive Entity Class Blacklist";
    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The blacklist is check BEFORE the whitelist"
    })
    @Config.Name(passiveEntityBlacklistName)
    public String[] passiveEntityBlacklist = {
            "*",
            EntityPlayer.class.getName()
    };

    private final static String hostileEntityWhitelistName = "Hostile Entity Class Whitelist";
    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The whitelist is check AFTER the blacklist"
    })
    @Config.Name(hostileEntityWhitelistName)
    public String[] hostileEntityWhitelist = {
            IMob.class.getName()
    };

    private final static String hostileEntityBlacklistName = "Hostile Entity Class Blacklist";
    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The blacklist is check BEFORE the whitelist"
    })
    @Config.Name(hostileEntityBlacklistName)
    public String[] hostileEntityBlacklist = {
            "*"
    };

    private final static String generalEntityWhitelistName = "General Entity Class Whitelist";
    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The whitelist is check AFTER the blacklist"
    })
    @Config.Name(generalEntityWhitelistName)
    public String[] generalEntityWhitelist = {

    };

    private final static String generalEntityBlacklistName = "General Entity Class Blacklist";
    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The blacklist is check BEFORE the whitelist"
    })
    @Config.Name(generalEntityBlacklistName)
    public String[] generalEntityBlacklist = {
            "*"
    };

    private final static String trueSourceClassesName = "Scale With True Source Class";
    @Config.Comment({
            "Similar to the vanilla Scaling Health player -> damage -> \"Scale By Source\" config",
            "The default config includes Vanilla Damage Sources that Scaling Health 1.12 did not include",
            "Format: [damageType : damageScale , comma-separated-class-names]",
            "'*' can be set as a damageType wildcard where the provided entity condition must be met",
            "'NULL' can be set as a Class Name, which will match Damage Sources not credited to an Entity",
            "\tex: [arrow: 0.0, net.minecraft.entity.projectile.EntityArrow]",
            "\tex: [*: 0.0, net.minecraft.entity.player.EntityPlayer]",
            "\tex: [wither: 0.0, NULL]",
            "Classes are checked after Mod and Entity IDs, True Source is checked before Immediate Source"
    })
    @Config.Name(trueSourceClassesName)
    public String[] trueSourceClasses = {
            "mob: 0.0",
            "player: 0.0, " + EntityPlayer.class.getName(),
            "arrow: 0.0",
            "thrown: 0.0",
            "indirectMagic",
            "thorns: 0.0",
            "explosion.player: 0.0, " + EntityPlayer.class.getName(),
            "explosion: 0.0"
    };

    private final static String trueSourceIDsName = "Scale With True Source ID";
    @Config.Comment({
            "Similar to the vanilla Scaling Health player -> damage -> \"Scale By Source\" config",
            "Format: [damageType : damageScale , comma-separated-entity-ids]",
            "'modid:*' can be set as a wildcard to match all entities from a mod",
            "\tex: [mob: 0.0, minecraft:giant, minecraft:illusion_illager]",
            "\tex: [dragonBreath: 0.0, minecraft:ender_dragon]",
            "\tex: [exampleSource: 0.0, minecraft:*]",
            "Entity ID is checked first and then Mod ID, True Source is checked before Immediate Source"
    })
    @Config.Name(trueSourceIDsName)
    public String[] trueSourceIDs = {

    };

    private final static String immediateSourceClassesName = "Scale With Immediate Source Class";
    @Config.Comment({
            "Similar to the vanilla Scaling Health player -> damage -> \"Scale By Source\" config",
            "The default config includes Vanilla Damage Sources that Scaling Health 1.12 did not include",
            "Format: [damageType : damageScale , comma-separated-class-names]",
            "'*' can be set as a damageType wildcard where the provided entity condition must be met",
            "'NULL' can be set as a Class Name, which will match Damage Sources not credited to an Entity",
            "\tex: [arrow: 0.0, net.minecraft.entity.projectile.EntityArrow]",
            "\tex: [*: 0.0, net.minecraft.entity.player.EntityPlayer]",
            "\tex: [wither: 0.0, NULL]",
            "Classes are checked after Mod and Entity IDs, True Source is checked before Immediate Source"
    })
    @Config.Name(immediateSourceClassesName)
    public String[] immediateSourceClasses = {
            "mob: 0.0, " + EntityLiving.class.getName(),
            "player: 0.0, " + EntityPlayer.class.getName(),
            "arrow: 0.0, " + EntityArrow.class.getName(),
            "onFire: 0.0, " + EntityLargeFireball.class.getName() + ", " + EntitySmallFireball.class.getName(),
            "thrown: 0.0, " + EntityThrowable.class.getName(),
            "indirectMagic: 0.0, " + EntityGuardian.class.getName() + ", " + EntityEvokerFangs.class.getName() + ", " + EntityPotion.class.getName(),
            "thorns: 0.0, " + EntityLivingBase.class.getName(),
            "explosion.player: 0.0, " + EntityWither.class.getName() + ", " + EntityLargeFireball.class.getName() + ", " + EntityWitherSkull.class.getName(),
            "explosion: 0.0, NULL"
    };

    private final static String immediateSourceIDsName = "Scale With Immediate Source ID";
    @Config.Comment({
            "Similar to the vanilla Scaling Health player -> damage -> \"Scale By Source\" config",
            "Format: [damageType : damageScale , comma-separated-entity-ids]",
            "'modid:*' can be set as a wildcard to match all entities from a mod",
            "\tex: [mob: 0.0, minecraft:giant, minecraft:illusion_illager]",
            "\tex: [dragonBreath: 0.0, minecraft:ender_dragon]",
            "\tex: [exampleSource: 0.0, minecraft:*]",
            "Entity ID is checked first and then Mod ID, True Source is checked before Immediate Source"
    })
    @Config.Name(immediateSourceIDsName)
    public String[] immediateSourceIDs = {

    };

    public static float getScaleForSource(DamageSource damageSource, Map<String, Float> generalConfig, float genericScale) {
        String damageType = damageSource.getDamageType();

        Float scale = getScale(
                damageSource.getTrueSource(),
                damageType,
                nullTrueSourceScales,
                idTrueSourceScales,
                modTrueSourceScales,
                wildcardIdTrueSourceScales,
                classTrueSourceScales,
                wildcardTrueSourceScales
        );

        if(scale == null)
            scale = getScale(
                    damageSource.getImmediateSource(),
                    damageType,
                    nullImmediateSourceScales,
                    idImmediateSourceScales,
                    modImmediateSourceScales,
                    wildcardIdImmediateSourceScales,
                    classImmediateSourceScales,
                    wildcardImmediateSourceScales
            );

        return scale == null ? generalConfig.getOrDefault(damageType, genericScale) : scale;
    }

    private static Float getScale(
            @Nullable Entity damageSourceEntity,
            String damageType,
            Map<String, Float> nullMap,
            Map<String, Map<ResourceLocation, Float>> entityMap,
            Map<String, Map<String, Float>> modMap,
            Map<ResourceLocation, Float> wildcardMap,
            Map<String, Map<Class<?>, Float>> sourceClassMap,
            Map<Class<?>, Float> wildcardClassMap
    ) {
        if(damageSourceEntity == null) {
            if(nullMap.containsKey(damageType))
                return nullMap.get(damageType);
        }
        else {
            ResourceLocation id = EntityList.getKey(damageSourceEntity);
            if(id != null) {
                Map<?, Float> scaleMap;
                scaleMap = entityMap.get(damageType);
                if(scaleMap != null)
                    if(scaleMap.containsKey(id))
                        return scaleMap.get(id);
                scaleMap = modMap.get(damageType);
                if(scaleMap != null)
                    if(scaleMap.containsKey(id.getNamespace()))
                        return scaleMap.get(id.getNamespace());
                if(wildcardMap.containsKey(id))
                    return wildcardMap.get(id);
            }

            Map<Class<?>, Float> classMap = sourceClassMap.get(damageType);
            if(classMap != null) {
                for(Class<?> clazz : classMap.keySet()) {
                    if(clazz.isInstance(damageSourceEntity))
                        return classMap.get(clazz);
                }
            }

            for(Class<?> clazz : wildcardClassMap.keySet()) {
                if(clazz.isInstance(damageSourceEntity))
                    return wildcardClassMap.get(clazz);
            }
        }
        return null;
    }

    public static Boolean isHostileScalable(Entity entity) {
        for(Class<?> clazz : hostileBlacklist) {
            if(clazz.isInstance(entity)) return false;
        }
        for(Class<?> clazz : hostileWhitelist) {
            if(clazz.isInstance(entity)) return true;
        }
        return null;
    }

    public static Boolean isPassiveScalable(Entity entity) {
        for(Class<?> clazz : passiveBlacklist) {
            if(clazz.isInstance(entity)) return false;
        }
        for(Class<?> clazz : passiveWhitelist) {
            if(clazz.isInstance(entity)) return true;
        }
        return null;
    }

    public static boolean isEntityScalable(Entity entity) {
        for(Class<?> clazz : generalBlacklist) {
            if(clazz.isInstance(entity)) return false;
        }
        for(Class<?> clazz : generalWhitelist) {
            if(clazz.isInstance(entity)) return true;
        }
        return false;
    }

    public void init() {
        initClassSet(passiveEntityWhitelist, passiveWhitelist, passiveEntityWhitelistName);
        initClassSet(passiveEntityBlacklist, passiveBlacklist, passiveEntityBlacklistName);
        initClassSet(hostileEntityWhitelist, hostileWhitelist, hostileEntityWhitelistName);
        initClassSet(hostileEntityBlacklist, hostileBlacklist, hostileEntityBlacklistName);
        initClassSet(generalEntityWhitelist, generalWhitelist, generalEntityWhitelistName);
        initClassSet(generalEntityBlacklist, generalBlacklist, generalEntityBlacklistName);
        
        initScaleByClass(trueSourceClasses, classTrueSourceScales, wildcardTrueSourceScales, nullTrueSourceScales, trueSourceClassesName);
        initScaleByClass(immediateSourceClasses, classImmediateSourceScales, wildcardImmediateSourceScales, nullImmediateSourceScales, immediateSourceClassesName);
        
        initScaleByID(trueSourceIDs, modTrueSourceScales, idTrueSourceScales, wildcardIdTrueSourceScales, trueSourceIDsName);
        initScaleByID(immediateSourceIDs, modImmediateSourceScales, idImmediateSourceScales, wildcardIdImmediateSourceScales, immediateSourceIDsName);
    }

    private static void initClassSet(String[] config, Set<Class<?>> classSet, String configName) {
        classSet.clear();
        for(String entry : config) {
            try {
                if(entry.trim().equals("*"))
                    classSet.add(Object.class);
                else {
                    Class<?> clazz = Class.forName(entry.trim());
                    classSet.add(clazz);   
                }
            } catch (ClassNotFoundException e) {
                ScalingHealthIssues.LOGGER.log(Level.WARN, "Damage Scaling Config: {}, Class not found for entry: {}, skipping", configName, entry);
            }
        }

        if(classSet.contains(Object.class))
            classSet.removeIf(clazz -> clazz != Object.class);

        if(ForgeConfigHandler.debug.logConfig)
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {} -> {}", configName, classSet);
    }

    private static void initScaleByClass(String[] config, Map<String, Map<Class<?>, Float>> sourceMap, Map<Class<?>, Float> wildcardMap, Map<String, Float> nullMap, String configName) {
        nullMap.clear();
        wildcardMap.clear();
        sourceMap.clear();
        for(String line : config) {
            List<String> entries = Arrays.asList(line.split(","));
            if(entries.size() < 2) continue;
            
            String[] scaleConfig = entries.get(0).split(":");
            if(scaleConfig.length < 2) continue;

            String type = scaleConfig[0].trim();
            float scale;
            
            try {
                scale = Float.parseFloat(scaleConfig[1].trim());
            } catch (NumberFormatException e) {
                ScalingHealthIssues.LOGGER.log(Level.WARN, "Damage Scaling Config: {}, Scale not found in line: {}, skipping", configName, line);
                continue;
            }

            for(int i = 1; i < entries.size(); i++) {
                String entry = entries.get(i);
                String entryClass = entry.trim();
                if(entryClass.equalsIgnoreCase("NULL")) {
                    nullMap.put(type, scale);
                }
                else {
                    try {
                        Class<?> clazz = Class.forName(entryClass);
                        if(type.equals("*")) {
                            wildcardMap.put(clazz, scale);
                        }
                        else {
                            sourceMap.computeIfAbsent(type, source -> new THashMap<>()).put(clazz, scale);
                        }
                    } catch (ClassNotFoundException e) {
                        ScalingHealthIssues.LOGGER.log(Level.WARN, "Damage Scaling Config: {}, Class not found for entry: {}, in line: {}, skipping", configName, entry, line);
                    }
                }
            }
        }
        if(ForgeConfigHandler.debug.logConfig) {
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {} Null -> {}", configName, nullMap);
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {} Wildcards -> {}", configName, wildcardMap);
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {} Sources -> {}", configName, sourceMap);
        }
    }

    private static void initScaleByID(String[] config, Map<String, Map<String, Float>> modMap, Map<String, Map<ResourceLocation, Float>> entityMap, Map<ResourceLocation, Float> wildcardMap, String configName) {
        wildcardMap.clear();
        modMap.clear();
        entityMap.clear();
        for(String line : config) {
            List<String> entries = Arrays.asList(line.split(","));
            if(entries.size() < 2) continue;

            String[] scaleConfig = entries.get(0).split(":");
            if(scaleConfig.length < 2) continue;

            String type = scaleConfig[0].trim();
            float scale;

            try {
                scale = Float.parseFloat(scaleConfig[1].trim());
            } catch (NumberFormatException e) {
                ScalingHealthIssues.LOGGER.log(Level.WARN, "Damage Scaling Config: {}, Scale not found in line: {}, skipping", configName, line);
                continue;
            }

            for(int i = 1; i < entries.size(); i++) {
                String entry = entries.get(i);
                String[] entryID = entry.split(":");
                if(entryID.length < 2) continue;
                
                if(entryID[1].trim().equals("*")) {
                    String id = entryID[0].trim();
                    if(Loader.isModLoaded(id))
                        modMap.computeIfAbsent(type, source -> new THashMap<>()).put(id, scale);
                    else
                        ScalingHealthIssues.LOGGER.log(Level.WARN, "Damage Scaling Config: {}, Mod not found for entry: {}, in line: {}, skipping", configName, entryID, line);
                }
                else {
                    ResourceLocation id = new ResourceLocation(entry.trim());
                    EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(id);
                    if(entityEntry != null) {
                        if (type.equals("*"))
                            wildcardMap.put(id, scale);
                        else
                            entityMap.computeIfAbsent(type, source -> new THashMap<>()).put(id, scale);
                    }
                    else
                        ScalingHealthIssues.LOGGER.log(Level.WARN, "Damage Scaling Config: {}, ID not found for entry: {}, in line: {}, skipping", configName, entryID, line);
                }
            }
        }
        if(ForgeConfigHandler.debug.logConfig) {
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {} Wildcards -> {}", configName, wildcardMap);
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {} Mods -> {}", configName, modMap);
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {} Entities -> {}", configName, entityMap);
        }
    }
}

