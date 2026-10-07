package scalinghealthissues.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import scalinghealthissues.Tags;
import scalinghealthissues.mixin.scalinghealth.DifficultyHandler_InvokerMixin;

import javax.annotation.Nullable;

public abstract class BetterDifficultyHandler {

    public static final String VANILLA_INITIAL_SPAWN = Tags.MODID + ":VanillaInitialSpawn";

    /** Returns a new TextComponent, DOES NOT modify existing **/
    public static ITextComponent wrapDifficultyHoverText(ITextComponent originalMessage, Entity victim, Entity killer) {
        ITextComponent messageWrapper = new TextComponentString("");
        BetterDifficultyHandler.addDifficultyHoverText(messageWrapper, victim, killer);
        messageWrapper.appendSibling(originalMessage);
        return messageWrapper;
    }

    /** Returns a new TextComponent, DOES NOT modify existing **/
    public static ITextComponent prependBlightText(ITextComponent message, Entity entity) {
        ITextComponent messageWrapper = new TextComponentTranslation("blight.scalinghealth.name", "");
        addDifficultyHoverText(messageWrapper, entity, null);
        messageWrapper.getStyle().setColor(TextFormatting.DARK_PURPLE);

        if(message.getStyle().getColor() == null) message.getStyle().setColor(TextFormatting.RESET);
        messageWrapper.appendSibling(message);
        return messageWrapper;
    }

    /** Modifies a TextComponent and returns it **/
    public static ITextComponent addDifficultyHoverText(ITextComponent message, @Nullable Entity victim, @Nullable Entity killer) {
        if(message.getStyle().getHoverEvent() == null) {
            boolean hasDifficulty = false;
            ITextComponent difficultyText = new TextComponentString("");
            if (victim instanceof EntityLivingBase) {
                double victimDifficulty = ScalingHealthAPI.getEntityDifficulty((EntityLivingBase) victim);
                if(victimDifficulty > Config.Difficulty.minValue) {
                    ITextComponent victimText = new TextComponentTranslation(ScalingHealth.i18n.miscText("difficultyMeterText"));
                    victimText.appendText(" " + String.format("%.2f", victimDifficulty) + " " + victim.getName());
                    difficultyText.appendSibling(victimText);
                    hasDifficulty = true;
                }
            }
            if (killer instanceof EntityLivingBase) {
                double killerDifficulty = ScalingHealthAPI.getEntityDifficulty((EntityLivingBase) killer);
                if(killerDifficulty > Config.Difficulty.minValue) {
                    ITextComponent killerText = new TextComponentTranslation(ScalingHealth.i18n.miscText("difficultyMeterText"));
                    killerText.appendText(" " + String.format("%.2f", killerDifficulty) + " " + killer.getName());
                    difficultyText.appendText("\n").appendSibling(killerText);
                    hasDifficulty = true;
                }
            }

            Entity entityInArea = victim != null ? victim : killer;
            if(entityInArea != null) {
                double areaDifficulty = ScalingHealthAPI.getAreaDifficulty(entityInArea.world, entityInArea.getPosition());
                if(areaDifficulty > Config.Difficulty.minValue) {
                    ITextComponent areaText = new TextComponentTranslation(ScalingHealth.i18n.miscText("difficultyMeterText"));
                    areaText.appendText(" " + String.format("%.2f", areaDifficulty) + " ");
                    difficultyText.appendText("\n").appendSibling(areaText).appendSibling(new TextComponentTranslation("structure_block.position")); // "Relative Position"
                    hasDifficulty = true;
                }
            }

            if(hasDifficulty)
                message.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, difficultyText));
        }
        return message;
    }

    // Rehandled Tick events canceled by mixins and replaced with theses
    // World Spawner, Mob Spawner, and Mob Egg instead of every tick
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if(DifficultyHandler.INSTANCE instanceof DifficultyHandler_InvokerMixin) {
            ((DifficultyHandler_InvokerMixin) DifficultyHandler.INSTANCE).scalingHealthIssues$invokeProcess(event.getEntityLiving());

            NBTTagCompound nbt = event.getEntityLiving().getEntityData();
            if(nbt.hasKey(VANILLA_INITIAL_SPAWN)) {
                if(!nbt.getBoolean(VANILLA_INITIAL_SPAWN))
                    event.setCanceled(true); // Don't run vanilla onInitialSpawn

                nbt.removeTag(VANILLA_INITIAL_SPAWN);
            }
        }
    }

    // Anything not caught be the above
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(!(event.getEntity() instanceof EntityLiving)) return;
        if(event.getWorld().isRemote) return;
        EntityLiving entity = (EntityLiving) event.getEntity();

        if(!DifficultyHandler_InvokerMixin.scalingHealthIssues$invokeIsProcessed(entity)) {
            if(DifficultyHandler.INSTANCE instanceof DifficultyHandler_InvokerMixin) {
                ((DifficultyHandler_InvokerMixin) DifficultyHandler.INSTANCE).scalingHealthIssues$invokeProcess(entity);
            }
        }
    }
}
