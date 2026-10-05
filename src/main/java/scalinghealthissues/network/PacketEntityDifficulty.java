package scalinghealthissues.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.scalinghealth.event.DifficultyHandler;
import org.apache.logging.log4j.Level;
import scalinghealthissues.ScalingHealthIssues;
import scalinghealthissues.config.ConfigHandler;

import java.util.UUID;

// Currently not used, difficulty in death message is from server
public class PacketEntityDifficulty implements IMessage {

    private boolean toServer;
    private UUID entityUUID;

    private int entityId;
    private short entityDifficulty = -1;

    public PacketEntityDifficulty() {}
    public PacketEntityDifficulty(Entity entity) {
        if(entity.world.isRemote) {
            this.toServer = true;
            this.entityUUID = entity.getUniqueID();
        }
        else {
            this.toServer = false;
            this.entityId = entity.getEntityId();
            this.entityDifficulty = entity.getEntityData().getShort(DifficultyHandler.NBT_ENTITY_DIFFICULTY);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        this.toServer = packet.readBoolean();

        if(this.toServer)
            this.entityUUID = packet.readUniqueId();
        else {
            this.entityId = packet.readInt();
            this.entityDifficulty = packet.readShort();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeBoolean(this.toServer);

        if(this.toServer)
            packet.writeUniqueId(this.entityUUID);
        else {
            packet.writeInt(this.entityId);
            packet.writeShort(this.entityDifficulty);
        }
    }

    public static class ServerHandler implements IMessageHandler<PacketEntityDifficulty, IMessage> {

        @Override
        public IMessage onMessage(final PacketEntityDifficulty message, final MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private static void handle(PacketEntityDifficulty message, MessageContext ctx) {
            if(!message.toServer) return;

            EntityPlayerMP player = ctx.getServerHandler().player;
            Entity entity = player.getServerWorld().getEntityFromUuid(message.entityUUID);
            if(entity != null)
                PacketHandler.instance.sendTo(new PacketEntityDifficulty(entity), player);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<PacketEntityDifficulty, IMessage> {

        @Override
        public IMessage onMessage(PacketEntityDifficulty message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if(message.toServer) return;


                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                if (entity instanceof EntityLivingBase)
                    entity.getEntityData().setShort(DifficultyHandler.NBT_ENTITY_DIFFICULTY, message.entityDifficulty);

                if(ConfigHandler.debug.logDifficulty)
                    ScalingHealthIssues.LOGGER.log(Level.INFO, "Sync Difficulty {} -> {}", message.entityDifficulty, entity);
            });
            return null;
        }
    }
}
