package me.tuanzi.curiosities.network;

import me.tuanzi.curiosities.Curiosities;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * 网络通信处理器
 * 用于客户端和服务端之间的网络包传输
 */
public class PacketHandler {
    // 协议版本，用于兼容性检查
    private static final String PROTOCOL_VERSION = "1";
    
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // 网络通道实例
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Curiosities.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    // 消息ID计数器
    private static int id = 0;

    /**
     * 注册网络包和处理器
     */
    public static void register() {
        LOGGER.info("[连锁挖掘] 注册网络包处理器");
        
        // 注册连锁挖掘触发包（客户端 -> 服务端）
        // 当玩家使用连锁挖掘破坏方块时发送
        INSTANCE.registerMessage(id++, PacketTriggerChainMining.class, 
                PacketTriggerChainMining::toBytes, 
                PacketTriggerChainMining::new, 
                PacketTriggerChainMining::handle);
        
        // 注册连锁挖掘激活状态包（客户端 -> 服务端）
        // 当玩家切换连锁挖掘激活状态时发送
        INSTANCE.registerMessage(id++, PacketTriggerChainMining.ChainMiningActivation.class, 
                PacketTriggerChainMining.ChainMiningActivation::toBytes, 
                PacketTriggerChainMining.ChainMiningActivation::new, 
                PacketTriggerChainMining.ChainMiningActivation::handle);
        
        LOGGER.info("[连锁挖掘] 网络包注册完成");
    }
    
    /**
     * 记录网络包发送日志
     * 
     * @param packetName 包名称
     * @param params 额外参数
     */
    public static void logPacketSend(String packetName, Object... params) {
        LOGGER.debug("[连锁挖掘] 发送网络包: {} {}", packetName, params);
    }
}