package me.tuanzi.curiosities.enchantments.chain_mining;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理玩家的连锁挖掘状态
 */
public class ChainMiningState {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 使用ConcurrentHashMap保证线程安全
    private static final Map<UUID, Boolean> playerChainMiningState = new ConcurrentHashMap<>();

    /**
     * 设置玩家的连锁挖掘状态
     *
     * @param playerUUID 玩家的UUID
     * @param active     是否激活连锁挖掘
     */
    public static void setPlayerChainMiningActive(UUID playerUUID, boolean active) {
        // 如果连锁挖掘被禁用，不允许激活
        if (active && !ModConfigManager.CHAIN_MINING_ENABLED.get()) {
            LOGGER.debug("[连锁挖掘] 玩家 {} 尝试激活连锁挖掘模式，但配置已禁用", playerUUID);
            // 确保玩家状态为禁用
            playerChainMiningState.remove(playerUUID);
            return;
        }

        if (active) {
            playerChainMiningState.put(playerUUID, true);
            LOGGER.debug("[连锁挖掘] 玩家 {} 激活了连锁挖掘模式", playerUUID);
        } else {
            playerChainMiningState.remove(playerUUID);
            LOGGER.debug("[连锁挖掘] 玩家 {} 停用了连锁挖掘模式", playerUUID);
        }
    }

    /**
     * 检查玩家是否激活了连锁挖掘模式
     *
     * @param playerUUID 玩家的UUID
     * @return 如果玩家激活了连锁挖掘模式，则返回true
     */
    public static boolean isPlayerChainMiningActive(UUID playerUUID) {
        // 如果连锁挖掘被禁用，始终返回false
        if (!ModConfigManager.CHAIN_MINING_ENABLED.get()) {
            return false;
        }
        return playerChainMiningState.getOrDefault(playerUUID, false);
    }

    /**
     * 玩家离开时清除状态
     *
     * @param playerUUID 玩家的UUID
     */
    public static void clearPlayerChainMiningState(UUID playerUUID) {
        playerChainMiningState.remove(playerUUID);
        LOGGER.debug("[连锁挖掘] 玩家 {} 的连锁挖掘状态已清除", playerUUID);
    }

    /**
     * 清除所有玩家的连锁挖掘状态
     */
    public static void clearAllPlayerChainMiningState() {
        playerChainMiningState.clear();
        LOGGER.debug("[连锁挖掘] 所有玩家的连锁挖掘状态已清除");
    }
} 