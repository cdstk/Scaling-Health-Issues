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
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.mixin.scalinghealth.DifficultyHandler_InvokerMixin;
import scalinghealthissues.network.PacketEntityDifficulty;
import scalinghealthissues.network.PacketHandler;

import javax.annotation.Nullable;

public abstract class BetterDifficultyHandler {

    public static final String VANILLA_INITIAL_SPAWN = ScalingHealthIssues.MODID + ":VanillaInitialSpawn";

    public static final String NBT_DIFFICULTY_PROCESSED = ScalingHealth.MOD_ID_OLD + "Issues.DifficultyProcessed";

    public static boolean isDifficultyProcessed(EntityLivingBase entityLivingBase) {
        NBTTagCompound nbt = entityLivingBase.getEntityData();
        return nbt.hasKey(NBT_DIFFICULTY_PROCESSED) && nbt.getBoolean(NBT_DIFFICULTY_PROCESSED);
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
            ITextComponent difficultyText = new TextComponentString("");
            if (victim instanceof EntityLivingBase) {
                ITextComponent victimText = new TextComponentTranslation(ScalingHealth.i18n.miscText("difficultyMeterText"));
                victimText.appendText(" " + ScalingHealthAPI.getEntityDifficulty((EntityLivingBase) victim) + " " + victim.getName());
                difficultyText.appendSibling(victimText);
            }
            if (killer instanceof EntityLivingBase) {
                ITextComponent killerText = new TextComponentTranslation(ScalingHealth.i18n.miscText("difficultyMeterText"));
                killerText.appendText(" " + ScalingHealthAPI.getEntityDifficulty((EntityLivingBase) killer) + " " + killer.getName());
                difficultyText.appendText("\n").appendSibling(killerText);
            }

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
        EntityLiving entity = (EntityLiving) event.getEntity();

        if(event.getWorld().isRemote) {
            PacketHandler.instance.sendToServer(new PacketEntityDifficulty(entity));
        }
        else if(!isDifficultyProcessed(entity)) {
            if(DifficultyHandler.INSTANCE instanceof DifficultyHandler_InvokerMixin) {
                ((DifficultyHandler_InvokerMixin) DifficultyHandler.INSTANCE).scalingHealthIssues$invokeProcess(entity);
            }
        }
    }
}
