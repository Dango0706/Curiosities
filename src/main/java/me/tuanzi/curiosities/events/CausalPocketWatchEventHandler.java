package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.items.ModItems;
import me.tuanzi.curiosities.util.DebugLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 因果怀表事件处理器
 * 负责实时监控因果怀表的过期时间并发送提示消息
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class CausalPocketWatchEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    // NBT标签常量
    private static final String STORED_DATA_TAG = "StoredPlayerData";
    private static final String STORAGE_TIME_TAG = "StorageTime";
    private static final String PLAYER_UUID_TAG = "PlayerUUID";
    private static final String EXPIRY_NOTIFIED_TAG = "ExpiryNotified";

    // 用于跟踪玩家的检查间隔，避免每tick都检查
    private static final Map<UUID, Integer> playerCheckCounters = new HashMap<>();
    private static final int CHECK_INTERVAL_TICKS = 20; // 每秒检查一次

    /**
     * 服务器玩家tick事件处理
     * 定期检查玩家物品栏中的因果怀表是否过期
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 确保只在服务器端的END阶段执行
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        // 检查功能是否启用
        if (!ModConfigManager.CAUSAL_POCKET_WATCH_ENABLED.get()) {
            return;
        }

        Player player = event.player;
        UUID playerId = player.getUUID();

        // 实现检查间隔，避免每tick都检查
        int counter = playerCheckCounters.getOrDefault(playerId, 0);
        counter++;

        if (counter < CHECK_INTERVAL_TICKS) {
            playerCheckCounters.put(playerId, counter);
            return;
        }

        // 重置计数器
        playerCheckCounters.put(playerId, 0);

        // 检查玩家物品栏中的所有因果怀表
        checkPlayerCausalPocketWatches((ServerPlayer) player);
    }

    /**
     * 检查玩家物品栏中的所有因果怀表
     */
    private static void checkPlayerCausalPocketWatches(ServerPlayer player) {
        long currentGameTime = player.level().getGameTime();

        // 检查主手和副手
        checkCausalPocketWatch(player.getMainHandItem(), player, currentGameTime);
        checkCausalPocketWatch(player.getOffhandItem(), player, currentGameTime);

        // 检查物品栏
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            checkCausalPocketWatch(stack, player, currentGameTime);
        }
    }

    /**
     * 检查单个因果怀表物品是否过期
     */
    private static void checkCausalPocketWatch(ItemStack stack, ServerPlayer player, long currentGameTime) {
        // 检查是否为因果怀表
        if (!stack.is(ModItems.CAUSAL_POCKET_WATCH.get())) {
            return;
        }

        CompoundTag nbt = stack.getOrCreateTag();

        // 检查是否有储存数据
        if (!nbt.contains(STORED_DATA_TAG)) {
            return;
        }

        CompoundTag storedData = nbt.getCompound(STORED_DATA_TAG);

        // 检查是否有时间标签
        if (!storedData.contains(STORAGE_TIME_TAG)) {
            return;
        }

        // 检查是否已经通知过过期
        if (nbt.getBoolean(EXPIRY_NOTIFIED_TAG)) {
            return;
        }

        // 验证玩家UUID（安全检查）
        if (storedData.contains(PLAYER_UUID_TAG)) {
            UUID storedUUID = storedData.getUUID(PLAYER_UUID_TAG);
            if (!storedUUID.equals(player.getUUID())) {
                return; // 不是当前玩家的数据，跳过
            }
        }

        // 检查是否过期
        long storageTime = storedData.getLong(STORAGE_TIME_TAG);
        long storageDurationTicks = ModConfigManager.CAUSAL_POCKET_WATCH_STORAGE_TIME.get() * 20L;

        if (currentGameTime - storageTime > storageDurationTicks) {
            // 数据过期，发送通知并清理
            handleExpiredData(stack, player);
        }
    }

    /**
     * 处理过期数据的清理和通知
     */
    private static void handleExpiredData(ItemStack stack, ServerPlayer player) {
        // 标记已通知过期，防止重复通知
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putBoolean(EXPIRY_NOTIFIED_TAG, true);

        // 发送ActionBar消息
        player.displayClientMessage(
                Component.translatable("message.curiosities.causal_pocket_watch.expired")
                        .withStyle(ChatFormatting.YELLOW),
                true
        );

        // 清除储存的数据
        clearStoredData(stack);

        // 强制刷新物品栏以立即更新附魔光泽效果
        player.getInventory().setChanged();

        DebugLogger.debugLog("因果怀表过期数据已实时清理并通知玩家，UUID: {}", player.getUUID());
    }

    /**
     * 清除储存的数据
     */
    private static void clearStoredData(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.remove(STORED_DATA_TAG);
        nbt.remove(EXPIRY_NOTIFIED_TAG); // 同时清除通知标记

        // 强制标记物品栈已更改，触发客户端同步
        stack.setTag(nbt);
    }

    /**
     * 玩家登出时清理跟踪数据
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        playerCheckCounters.remove(event.getEntity().getUUID());
    }

    /**
     * 服务器停止时清理所有跟踪数据
     */
    @SubscribeEvent
    public static void onServerStopping(net.minecraftforge.event.server.ServerStoppingEvent event) {
        playerCheckCounters.clear();
    }
}
