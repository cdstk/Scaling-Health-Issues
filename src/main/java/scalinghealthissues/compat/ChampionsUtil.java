package scalinghealthissues.compat;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import scalinghealthissues.util.HexToColorMap;

import javax.annotation.Nullable;

public abstract class ChampionsUtil {

    public static boolean shouldAnnounceKill(EntityLivingBase entityLivingBase) {
        if(ChampionHelper.isValidChampion(entityLivingBase)) {
            IChampionship chp = CapabilityChampionship.getChampionship((EntityLiving)entityLivingBase);
            if(chp != null) {
                return ChampionHelper.isElite(chp.getRank()) && chp.getRank().getTier() >= ConfigHandler.deathMessageTier;
            }
        }
        return false;
    }

    public static boolean isEntityChampion(Entity entity) {
        if(!(entity instanceof EntityLiving)) return false;

        IChampionship championship = CapabilityChampionship.getChampionship((EntityLiving) entity);
        return championship != null && ChampionHelper.isElite(championship.getRank());
    }

    /** Returns a new TextComponent, DOES NOT modify existing **/
    public static ITextComponent prependChampionText(ITextComponent message, @Nullable Entity entity) {
        ITextComponent messageWrapper = ChampionsUtil.createChampionText(entity);
        messageWrapper.appendText(" ");

        if(message.getStyle().getColor() == null) message.getStyle().setColor(TextFormatting.RESET);
        messageWrapper.appendSibling(message);
        return messageWrapper;
    }

    /** Creates a new TextComponent **/
    public static ITextComponent createChampionText(@Nullable Entity entity) {
        if(entity instanceof EntityLiving) {
            IChampionship championship = CapabilityChampionship.getChampionship((EntityLiving) entity);
            if(championship != null) {
                ITextComponent championText = new TextComponentTranslation("champions.identifier");
                championText.getStyle().setColor(HexToColorMap.nearestColor(championship.getRank().getColor()));

                addChampionHoverText(championText, (EntityLiving) entity);
                return championText;
            }
        }
        return new TextComponentString("");
    }

    /** Modifies a TextComponent and returns it **/
    public static ITextComponent addChampionHoverText(ITextComponent message, EntityLiving entity) {
        if(message.getStyle().getHoverEvent() == null) {
            IChampionship championship = CapabilityChampionship.getChampionship(entity);
            if (championship != null) {
                message.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(championship.getAffixes().toString())));
            }
        }
        return message;
    }
}

