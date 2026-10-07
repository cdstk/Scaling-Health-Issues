package scalinghealthissues.Wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import scalinghealthissues.compat.handlers.FirstAidHandler;
import scalinghealthissues.handlers.BetterDifficultyHandler;

public abstract class ScalingHealthWrapper {

    public static boolean isBlight(Entity entity) {
        if(entity instanceof EntityLivingBase)
            return ScalingHealthAPI.isBlight((EntityLivingBase) entity);

        return false;
    }

    /** Returns a new TextComponent, DOES NOT modify existing **/
    public static ITextComponent prependBlightText(ITextComponent message, Entity entity) {
        return BetterDifficultyHandler.prependBlightText(message, entity);
    }

    /** Returns a new TextComponent, DOES NOT modify existing **/
    public static ITextComponent wrapDifficultyHoverText(ITextComponent originalMessage, Entity victim, Entity killer) {
        ITextComponent messageWrapper = new TextComponentString("");
        BetterDifficultyHandler.addDifficultyHoverText(messageWrapper, victim, killer);
        messageWrapper.appendSibling(originalMessage);
        return messageWrapper;
    }

    public static void setMorphineInternal() {
        FirstAidHandler.morphineAppliedInternally = true;
        FirstAidHandler.morphineRemovedInternally = true;
    }
}
