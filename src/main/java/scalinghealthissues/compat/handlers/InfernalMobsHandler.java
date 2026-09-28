package scalinghealthissues.compat.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.compat.InfernalMobsUtil;
import scalinghealthissues.config.ForgeConfigHandler;

public class InfernalMobsHandler {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if(event.getEntityLiving().world.isRemote) return;
        if(!ForgeConfigHandler.compat.infernalKilledByPlayerMessage) return;

        EntityLivingBase victim = event.getEntityLiving();
        Entity killer = event.getSource().getTrueSource();

        if (!(killer instanceof EntityPlayer)) {
            return;
        }

        if(!InfernalMobsUtil.isModified(victim)) return;
        if(InfernalMobsUtil.getModifierCount(victim) < ForgeConfigHandler.compat.infernalKilledByPlayerModifiers) return;

        ITextComponent messageWrapper = InfernalMobsUtil.createInfernalText(victim);
        ITextComponent message = victim.getCombatTracker().getDeathMessage();
        messageWrapper.appendText(" ");

        if(message.getStyle().getColor() == null) message.getStyle().setColor(TextFormatting.RESET);
        messageWrapper.appendSibling(message);

        if(victim.getServer() != null) {
            victim.getServer().getPlayerList().sendMessage(messageWrapper);
        }
        else {
            victim.world.playerEntities.forEach(player -> player.sendMessage(messageWrapper));
        }
    }
}
