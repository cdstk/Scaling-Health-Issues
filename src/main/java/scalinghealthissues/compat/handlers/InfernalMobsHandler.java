package scalinghealthissues.compat.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scalinghealthissues.compat.InfernalMobsUtil;
import scalinghealthissues.config.ConfigHandler;

public class InfernalMobsHandler {

//    @SubscribeEvent
//    public static void onLivingDeath(LivingDeathEvent event) {
//        if(event.getEntityLiving().world.isRemote) return;
//        if(!ConfigHandler.compat.infernalMobs.killedByPlayerMessage) return;
//
//        EntityLivingBase victim = event.getEntityLiving();
//        Entity killer = event.getSource().getTrueSource();
//
//        if (!(killer instanceof EntityPlayer)) {
//            return;
//        }
//
//        if(!InfernalMobsUtil.isModified(victim)) return;
//        if(InfernalMobsUtil.getModifierCount(victim) < ConfigHandler.compat.infernalMobs.killedByPlayerModifiers) return;
//
//        ITextComponent deathMessage = victim.getCombatTracker().getDeathMessage();
//
//        if(victim.getServer() != null) {
//            victim.getServer().getPlayerList().sendMessage(deathMessage);
//        }
//        else {
//            victim.world.playerEntities.forEach(player -> player.sendMessage(deathMessage));
//        }
//    }
}
