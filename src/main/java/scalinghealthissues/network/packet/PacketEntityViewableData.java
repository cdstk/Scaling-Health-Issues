package scalinghealthissues.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scalinghealthissues.network.GuiHandler;

import java.io.IOException;

public class PacketEntityViewableData implements IMessage {

    private int entityId;
    private NBTTagCompound nbt;

    public PacketEntityViewableData() {}
    public PacketEntityViewableData(Entity entity, NBTTagCompound nbt) {
        this.entityId = entity.getEntityId();
        this.nbt = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);

        this.entityId = packet.readInt();
        try {
            this.nbt = packet.readCompoundTag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);

        packet.writeInt(this.entityId);
        packet.writeCompoundTag(this.nbt);
    }

    public static class ServerHandler implements IMessageHandler<PacketEntityViewableData, IMessage> {

        @Override
        public IMessage onMessage(final PacketEntityViewableData message, final MessageContext ctx) {
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler<PacketEntityViewableData, IMessage> {

        @Override
        public IMessage onMessage(PacketEntityViewableData message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiHandler.putViewableData(null, message.entityId, message.nbt);
            });
            return null;
        }
    }
}
