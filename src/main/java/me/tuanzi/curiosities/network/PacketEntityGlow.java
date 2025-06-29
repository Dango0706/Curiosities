package me.tuanzi.curiosities.network;

import me.tuanzi.curiosities.client.EntityGlowHandler;
import me.tuanzi.curiosities.util.DebugLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * 生物发光效果网络包
 * 用于通知客户端为指定生物添加发光效果
 */
public class PacketEntityGlow {
    private final List<Integer> entityIds;

    public PacketEntityGlow(List<Integer> entityIds) {
        this.entityIds = entityIds;
    }

    public static void encode(PacketEntityGlow packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.entityIds.size());
        for (int entityId : packet.entityIds) {
            buffer.writeInt(entityId);
        }
    }

    public static PacketEntityGlow decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<Integer> entityIds = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            entityIds.add(buffer.readInt());
        }
        return new PacketEntityGlow(entityIds);
    }

    public static void handle(PacketEntityGlow packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            DebugLogger.debugLog("[PacketEntityGlow] 收到网络包，生物ID数量: {}", packet.entityIds.size());
            // 只在客户端执行
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                DebugLogger.debugLog("[PacketEntityGlow] 在客户端处理网络包");
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.level != null) {
                    DebugLogger.debugLog("[PacketEntityGlow] 客户端世界存在，开始处理生物发光");
                    // 为指定的生物添加发光效果
                    int processedCount = 0;
                    for (int entityId : packet.entityIds) {
                        Entity entity = minecraft.level.getEntity(entityId);
                        if (entity != null) {
                            DebugLogger.debugLog("[PacketEntityGlow] 找到生物: {} ID: {}", entity.getType().getDescriptionId(), entityId);
                            EntityGlowHandler.addGlowingEntity(entity);
                            processedCount++;
                        } else {
                            DebugLogger.debugLog("[PacketEntityGlow] 未找到生物 ID: {}", entityId);
                        }
                    }
                    DebugLogger.debugLog("[PacketEntityGlow] 处理完成，成功添加发光效果的生物数量: {}", processedCount);
                } else {
                    DebugLogger.debugLog("[PacketEntityGlow] 客户端世界不存在");
                }
            });
        });
        context.setPacketHandled(true);
    }
}
