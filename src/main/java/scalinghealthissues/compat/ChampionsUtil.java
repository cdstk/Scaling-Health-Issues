package scalinghealthissues.compat;

import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.util.ChampionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;
import scalinghealthissues.util.HexToColorMap;

import javax.annotation.Nullable;

public abstract class ChampionsUtil {

    public static boolean isEntityChampion(Entity entity) {
        if(!(entity instanceof EntityLiving)) return false;

        IChampionship championship = CapabilityChampionship.getChampionship((EntityLiving) entity);
        return championship != null && ChampionHelper.isElite(championship.getRank());
    }

    /** Creates a new TextComponent **/
    public static ITextComponent createChampionText(@Nullable Entity entity) {
        if(entity instanceof EntityLiving) {
            IChampionship championship = CapabilityChampionship.getChampionship((EntityLiving) entity);
            if(championship != null) {
                ITextComponent championText = new TextComponentTranslation("champions.identifier");
//                ITextComponent championText = new TextComponentTranslation("champions.champion_egg.tooltip.tier", championship.getRank().getTier());
//                championText.appendText(" ").appendSibling(new TextComponentTranslation("champions.identifier"));

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

