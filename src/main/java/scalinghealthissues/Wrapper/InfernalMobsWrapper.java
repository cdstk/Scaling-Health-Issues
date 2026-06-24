package scalinghealthissues.Wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import scalinghealthissues.compat.InfernalMobsUtil;

import javax.annotation.Nullable;

public abstract class InfernalMobsWrapper {

    public static boolean isModified(Entity entity) {
        return InfernalMobsUtil.isModified(entity);
    }

    /** Returns a new TextComponent, DOES NOT modify existing **/
    public static ITextComponent prependInfernalText(ITextComponent message, @Nullable Entity entity) {
        ITextComponent messageWrapper = InfernalMobsUtil.createInfernalText(entity);
        messageWrapper.appendText(" ");

        if(message.getStyle().getColor() == null) message.getStyle().setColor(TextFormatting.RESET);
        messageWrapper.appendSibling(message);
        return messageWrapper;
    }
}
