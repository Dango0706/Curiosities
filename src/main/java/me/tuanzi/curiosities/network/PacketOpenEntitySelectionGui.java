package me.tuanzi.curiosities.network;

import me.tuanzi.curiosities.items.entity_compass.EntityCompassItem;
import me.tuanzi.curiosities.items.entity_compass.EntitySelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 打开生物选择GUI的网络包
 */
public class PacketOpenEntitySelectionGui {
    private final InteractionHand hand;

    public PacketOpenEntitySelectionGui(InteractionHand hand) {
        this.hand = hand;
    }

    public static void encode(PacketOpenEntitySelectionGui packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.hand);
    }

    public static PacketOpenEntitySelectionGui decode(FriendlyByteBuf buffer) {
        return new PacketOpenEntitySelectionGui(buffer.readEnum(InteractionHand.class));
    }

    public static void handle(PacketOpenEntitySelectionGui packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // 只在客户端执行
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    ItemStack stack = minecraft.player.getItemInHand(packet.hand);
                    if (stack.getItem() instanceof EntityCompassItem) {
                        minecraft.setScreen(new EntitySelectionScreen(stack));
                    }
                }
            });
        });
        context.setPacketHandled(true);
    }
}
