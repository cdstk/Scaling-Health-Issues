package scalinghealthissues.compat;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.MobModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;

public abstract class InfernalMobsUtil {

    public static boolean isModified(Entity entity) {
        return entity instanceof EntityLivingBase &&  InfernalMobsCore.getIsRareEntity((EntityLivingBase) entity);
    }

    public static boolean isInfernal(EntityLivingBase entity) {
        MobModifier modifier = InfernalMobsCore.getMobModifiers(entity);

        if(modifier != null) {
            return modifier.getModSize() > 10;
        }

        return false;
    }

    public static boolean isUltra(EntityLivingBase entity) {
        MobModifier modifier = InfernalMobsCore.getMobModifiers(entity);

        if(modifier != null) {
            return modifier.getModSize() > 5 && modifier.getModSize() <= 10;
        }

        return false;
    }

    public static boolean isRare(EntityLivingBase entity) {
        MobModifier modifier = InfernalMobsCore.getMobModifiers(entity);

        if(modifier != null) {
            return modifier.getModSize() <= 5;
        }

        return false;
    }

    public static int getModifierCount(EntityLivingBase entity) {
        MobModifier modifier = InfernalMobsCore.getMobModifiers(entity);
        if(modifier != null) {
            return modifier.getModSize();
        }
        return 0;
    }

    /** Creates a new TextComponent **/
    public static ITextComponent createInfernalText(@Nullable Entity entity) {
        if(entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            MobModifier modifier = InfernalMobsCore.getMobModifiers(entityLivingBase);
            if(modifier != null) {
                ITextComponent infernalText;
                if(isInfernal(entityLivingBase)) {
                    infernalText = new TextComponentTranslation("translation.infernalmobs:infernalClass");
                    infernalText.getStyle().setColor(TextFormatting.GOLD);
                }
                else if(isUltra(entityLivingBase)) {
                    infernalText = new TextComponentTranslation("translation.infernalmobs:ultraClass");
                    infernalText.getStyle().setColor(TextFormatting.YELLOW);
                }
                else {
                    infernalText = new TextComponentTranslation("translation.infernalmobs:rareClass");
                    infernalText.getStyle().setColor(TextFormatting.AQUA);
                }
                addInfernalHoverText(infernalText, entityLivingBase);
                return infernalText;
            }
        }
        return new TextComponentString("");
    }

    /** Modifies a TextComponent and returns it **/
    public static ITextComponent addInfernalHoverText(ITextComponent message, EntityLivingBase entity) {
        if(message.getStyle().getHoverEvent() == null) {
            MobModifier modifier = InfernalMobsCore.getMobModifiers(entity);
            if(modifier != null) {
                message.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(modifier.getLinkedModName())));
            }
        }
        return message;
    }
}