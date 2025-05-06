package me.tuanzi.curiosities.network;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.enchantments.chain_mining.ChainMiningLogic;
import me.tuanzi.curiosities.enchantments.chain_mining.ChainMiningState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * 连锁挖掘触发网络包
 * 从客户端发送到服务端，用于触发连锁挖掘功能
 */
public class PacketTriggerChainMining {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    // 目标方块位置
    private final BlockPos pos;

    /**
     * 构造函数
     *
     * @param pos 目标方块位置
     */
    public PacketTriggerChainMining(BlockPos pos) {
        this.pos = pos;
    }

    /**
     * 从网络数据读取包内容
     *
     * @param buf 网络缓冲区
     */
    public PacketTriggerChainMining(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    /**
     * 将包内容写入网络数据
     *
     * @param buf 网络缓冲区
     */
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    /**
     * 处理网络包
     *
     * @param supplier 网络上下文提供者
     * @return 是否处理成功
     */
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // 确保在服务端线程执行
            ServerPlayer player = ctx.getSender();
            if (player != null) {
                // 记录日志
                LOGGER.info("[连锁挖掘] 服务端收到连锁挖掘数据包，玩家: {}, 方块位置: {}", player.getName().getString(), pos);
                // 调用服务端逻辑
                ChainMiningLogic.triggerChainMining(player, pos, player.level());
            }
        });
        return true;
    }

    /**
     * 连锁挖掘激活状态网络包
     * 从客户端发送到服务端，用于设置玩家的连锁挖掘激活状态
     */
    public static class ChainMiningActivation {
        // 日志记录器
        private static final Logger LOGGER = LogUtils.getLogger();

        // 激活状态
        private final boolean active;

        /**
         * 构造函数
         *
         * @param active 激活状态
         */
        public ChainMiningActivation(boolean active) {
            this.active = active;
        }

        /**
         * 从网络数据读取包内容
         *
         * @param buf 网络缓冲区
         */
        public ChainMiningActivation(FriendlyByteBuf buf) {
            this.active = buf.readBoolean();
        }

        /**
         * 将包内容写入网络数据
         *
         * @param buf 网络缓冲区
         */
        public void toBytes(FriendlyByteBuf buf) {
            buf.writeBoolean(active);
        }

        /**
         * 处理网络包
         *
         * @param supplier 网络上下文提供者
         * @return 是否处理成功
         */
        public boolean handle(Supplier<NetworkEvent.Context> supplier) {
            NetworkEvent.Context ctx = supplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player != null) {
                    LOGGER.info("[连锁挖掘] 服务端收到连锁挖掘状态变更，玩家: {}, 状态: {}",
                            player.getName().getString(), active ? "激活" : "停用");

                    // 更新玩家的连锁挖掘状态
                    ChainMiningState.setPlayerChainMiningActive(player.getUUID(), active);
                }
            });
            return true;
        }
    }
}