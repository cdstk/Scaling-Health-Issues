package scalinghealthissues.config.folders;

import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.Order;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Config;
import org.apache.logging.log4j.Level;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.Tags;
import scalinghealthissues.config.ConfigHandler;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@MixinConfig(name = Tags.MODID)
public class DamageScalingConfig {

    private static final Set<Class<?>> ENTITY_ClASS_WHITELIST = new HashSet<>();
    private static final Set<Class<?>> ENTITY_CLASS_BLACKLIST = new HashSet<>();

    @Config.Comment({
            "Overhauls the Damage Scaling Mechanic (Main Config in Mod Options -> Scaling Health -> player -> damage)",
            "\tConfig for Hostile Mobs, no longer limited to instances of IMob, a limited group.",
            "\tConfig for Passive Mobs, no longer limited any non Player or IMob, a very wide group.",
            "\tAdditional Config for Scaling Value for NULL True Source, aka environmental damage",
            "\tAdditional Config for Scaling Value for non-NULL True Source, aka damage with an attacker",
            "Treated as additional scaling rules where if none match, falls back to vanilla Scaling Health"
    })
    @Config.Name("Mixin: Overhaul Damage Scaling")
    @MixinConfig.MixinToggle(lateMixin = "mixins.scalinghealthissues.scalinghealth.overhauldmgscaling.json", defaultValue = true)
    @Config.RequiresMcRestart
    @Order(0)
    public boolean overhaulDamageScaling = true;

    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The whitelist is check AFTER the blacklist"
    })
    @Config.Name("Entity Class Whitelist")
    public String[] entityClassWhitelist = {
            IMob.class.getName(),
            IAnimals.class.getName()
    };

    @Config.Comment({
            "List of exact Class names which the damaged Entity will be instance checked",
            "'*' wildcard will match everything",
            "The blacklist is check BEFORE the whitelist"
    })
    @Config.Name("Entity Class Blacklist")
    public String[] entityClassBlacklist = {
            "*"
    };


    @Config.Comment({
            "Similar to the vanilla Scaling Health player -> damage -> \"Scale By Source\" config",
            "The default config includes Vanilla Damage Sources that Scaling Health 1.12 did not include"
    })
    @Config.Name("Damage Scaling Values - Has Attacker")
    public Map<String, Float> damageScalingsAttacker = createDefaultScalingConfig();

    @Config.Comment({
            "Similar to the vanilla Scaling Health player -> damage -> \"Scale By Source\" config",
            "The default config includes Vanilla Damage Sources that Scaling Health 1.12 did not include"
    })
    @Config.Name("Damage Scaling Values - Environmental")
    public Map<String, Float> damageScalingsEnvironment = createDefaultScalingConfig();

    public static float getScaleForSource(DamageSource damageSource, Map<String, Float> generalConfig, float genericScale) {
        String damageType = damageSource.getDamageType();

        // True Source is living
        if(damageSource.getTrueSource() instanceof EntityLivingBase) {
            Float value = ConfigHandler.dmgScale.damageScalingsAttacker.get(damageType);
            if(value != null) return value;
        }
        // Immediate is not living
        else if(!(damageSource.getImmediateSource() instanceof EntityLivingBase)) {
            Float value = ConfigHandler.dmgScale.damageScalingsEnvironment.get(damageType);
            if(value != null) return value;
        }

        return generalConfig.getOrDefault(damageType, genericScale);
    }

    public static boolean getVictimReceivesScaledDamage(Entity entity) {
        for(Class<?> clazz : ENTITY_CLASS_BLACKLIST) {
            if(clazz.isInstance(entity)) return false;
        }
        for(Class<?> clazz : ENTITY_ClASS_WHITELIST) {
            if(clazz.isInstance(entity)) return true;
        }
        return false;
    }

    public void init() {
        ENTITY_ClASS_WHITELIST.clear();
        ENTITY_CLASS_BLACKLIST.clear();

        initClassSet(ConfigHandler.dmgScale.entityClassWhitelist, ENTITY_ClASS_WHITELIST);
        initClassSet(ConfigHandler.dmgScale.entityClassBlacklist, ENTITY_CLASS_BLACKLIST);
    }

    public static Map<String, Float> createDefaultScalingConfig() {
        Map<String, Float> map = new LinkedHashMap<>();

        map.put("mob", 0.0F);
        map.put("player", 0.0F);
        map.put("arrow", 0.0F);
        map.put("thrown", 0.0F);
        map.put("indirectMagic", 0.0F);
        map.put("thorns", 0.0F);
        map.put("explosion.player", 0.0F);
        map.put("explosion", 0.0F);

        return map;
    }

    private static void initClassSet(String[] config, Set<Class<?>> classSet) {
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
                ScalingHealthIssues.LOGGER.log(Level.WARN, "Damage Scaling Config, Class not found for entry: {}, skipping", entry);
            }
        }

        if(classSet.contains(Object.class))
            classSet.removeIf(clazz -> clazz != Object.class);

        if(ConfigHandler.debug.logConfig)
            ScalingHealthIssues.LOGGER.log(Level.DEBUG, "Damage Scaling Config: {}", classSet);
    }
}

