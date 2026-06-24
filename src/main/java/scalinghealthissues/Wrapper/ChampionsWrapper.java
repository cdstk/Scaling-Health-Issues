package scalinghealthissues.Wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import scalinghealthissues.compat.ChampionsUtil;

import javax.annotation.Nullable;

public abstract class ChampionsWrapper {

    public static boolean isEntityChampion(Entity entity) {
        return ChampionsUtil.isEntityChampion(entity);
    }

    /** Returns a new TextComponent, DOES NOT modify existing **/
    public static ITextComponent prependChampionText(ITextComponent message, @Nullable Entity entity) {
        ITextComponent messageWrapper = ChampionsUtil.createChampionText(entity);
        messageWrapper.appendText(" ");

        if(message.getStyle().getColor() == null) message.getStyle().setColor(TextFormatting.RESET);
        messageWrapper.appendSibling(message);
        return messageWrapper;
    }
}
