package me.tuanzi.curiosities.items;

import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.util.DebugLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * 因果怀表物品
 * 可以储存玩家当前状态并在15秒内回溯到储存的状态
 * 耐久度25，可在岩浆中漂浮，使用下界之星修复，不可被附魔
 */
public class CausalPocketWatchItem extends Item {
    private static final Logger LOGGER = LoggerFactory.getLogger(CausalPocketWatchItem.class);

    // NBT标签常量
    private static final String STORED_DATA_TAG = "StoredPlayerData";
    private static final String STORAGE_TIME_TAG = "StorageTime";
    private static final String LAST_USE_TIME_TAG = "LastUseTime";
    private static final String PLAYER_NBT_TAG = "PlayerNBT";
    private static final String PLAYER_UUID_TAG = "PlayerUUID";
    private static final String DIMENSION_TAG = "Dimension";
    private static final String POSITION_TAG = "Position";
    private static final String EXPIRY_NOTIFIED_TAG = "ExpiryNotified";

    public CausalPocketWatchItem() {
        super(new Item.Properties()
                        .durability(25)  // 耐久度25
                        .fireResistant() // 可在岩浆中漂浮
                        .rarity(net.minecraft.world.item.Rarity.EPIC) // 史诗级稀有度
//                .stacksTo(1) // 最大堆叠数量为1，避免NBT数据混乱
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 检查物品是否启用
        if (!ModConfigManager.CAUSAL_POCKET_WATCH_ENABLED.get()) {
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.disabled")
                        .withStyle(ChatFormatting.RED));
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            long currentTime = level.getGameTime();
            CompoundTag nbt = stack.getOrCreateTag();

            // 检查冷却时间（使用官方ItemCooldowns API）
            if (player.getCooldowns().isOnCooldown(this)) {
                // 冷却中，不允许使用
                return InteractionResultHolder.fail(stack);
            }

            // 检查是否有储存的数据，使用带玩家参数的方法以便发送ActionBar消息
            if (checkAndClearExpiredData(stack, currentTime, serverPlayer)) {
                // 有有效的储存数据，执行回溯
                boolean restoreSuccess = restorePlayerData(stack, serverPlayer);

                if (restoreSuccess) {
                    // 消耗耐久度
                    stack.hurtAndBreak(1, serverPlayer, (p) -> p.broadcastBreakEvent(hand));

                    // 设置冷却时间（使用官方ItemCooldowns API）
                    int cooldownTicks = ModConfigManager.CAUSAL_POCKET_WATCH_COOLDOWN_TIME.get() * 20;
                    player.getCooldowns().addCooldown(this, cooldownTicks);

                    // 同时更新NBT以便tooltip显示
                    nbt.putLong(LAST_USE_TIME_TAG, currentTime);

                    // 清除储存的数据（这会自动移除附魔光泽）
                    clearStoredData(stack);

                    // 强制刷新物品栏以立即更新附魔光泽效果
                    serverPlayer.getInventory().setChanged();

                    player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.restored")
                            .withStyle(ChatFormatting.BLUE));
                }
                // 如果恢复失败，restorePlayerData方法内部已经处理了错误消息
            } else {
                // 没有有效数据，储存新的状态数据
                // 注意：如果有过期数据，checkAndClearExpiredData已经处理了清理和通知
                storePlayerData(stack, serverPlayer, currentTime);

                // 强制刷新物品栏以立即更新附魔光泽效果
                serverPlayer.getInventory().setChanged();

                player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.stored")
                        .withStyle(ChatFormatting.GREEN));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * 储存玩家数据
     * 使用NBT完整复制玩家状态，确保所有信息都能准确保存
     */
    private void storePlayerData(ItemStack stack, ServerPlayer player, long currentTime) {
        try {
            CompoundTag nbt = stack.getOrCreateTag();
            CompoundTag storedData = new CompoundTag();

            // 储存时间戳
            storedData.putLong(STORAGE_TIME_TAG, currentTime);

            // 储存玩家UUID进行安全验证
            storedData.putUUID(PLAYER_UUID_TAG, player.getUUID());

            // 储存维度信息
            storedData.putString(DIMENSION_TAG, player.level().dimension().location().toString());

            // 储存位置信息（单独保存，因为NBT中的位置可能不够精确）
            Vec3 pos = player.position();
            CompoundTag positionTag = new CompoundTag();
            positionTag.putDouble("x", pos.x);
            positionTag.putDouble("y", pos.y);
            positionTag.putDouble("z", pos.z);
            positionTag.putFloat("yaw", player.getYRot());
            positionTag.putFloat("pitch", player.getXRot());
            storedData.put(POSITION_TAG, positionTag);

            // 使用NBT完整复制玩家状态
            CompoundTag playerNBT = new CompoundTag();
            player.saveWithoutId(playerNBT);
            storedData.put(PLAYER_NBT_TAG, playerNBT);

            nbt.put(STORED_DATA_TAG, storedData);

            // 清除过期通知标记（如果存在）
            nbt.remove(EXPIRY_NOTIFIED_TAG);

            // 强制标记物品栈已更改，确保客户端同步
            stack.setTag(nbt);

            DebugLogger.debugLog("因果怀表储存玩家状态成功，玩家UUID: {}, 维度: {}, NBT包含储存数据: {}",
                    player.getUUID(), player.level().dimension().location(), nbt.contains(STORED_DATA_TAG));

        } catch (Exception e) {
            LOGGER.error("因果怀表储存玩家状态时发生错误", e);
            // 发送错误消息给玩家
            player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.store_error")
                    .withStyle(ChatFormatting.RED));
        }
    }

    /**
     * 清除储存的数据
     */
    private void clearStoredData(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.remove(STORED_DATA_TAG);
        nbt.remove(EXPIRY_NOTIFIED_TAG); // 同时清除过期通知标记

        // 强制标记物品栈已更改，触发客户端同步
        stack.setTag(nbt);

        DebugLogger.debugLog("因果怀表储存数据已清除");
    }


    /**
     * 检查并清理过期数据的统一方法
     * 用于在isFoil()和appendHoverText()中统一处理过期数据
     *
     * @param stack           物品堆栈
     * @param currentGameTime 当前游戏时间（可选，为null时不进行时间检查）
     * @param player          玩家对象（可选，用于发送ActionBar消息）
     * @return 是否有有效的储存数据
     */
    private boolean checkAndClearExpiredData(ItemStack stack, @Nullable Long currentGameTime, @Nullable ServerPlayer player) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (!nbt.contains(STORED_DATA_TAG)) {
            return false;
        }

        CompoundTag storedData = nbt.getCompound(STORED_DATA_TAG);
        if (!storedData.contains(STORAGE_TIME_TAG)) {
            // 数据不完整，清理掉
            clearStoredData(stack);
            return false;
        }

        // 如果没有提供当前时间，只检查数据是否存在
        if (currentGameTime == null) {
            return true;
        }

        // 检查数据是否过期
        long storageTime = storedData.getLong(STORAGE_TIME_TAG);
        long storageDurationTicks = ModConfigManager.CAUSAL_POCKET_WATCH_STORAGE_TIME.get() * 20L;

        if (currentGameTime - storageTime > storageDurationTicks) {
            // 数据过期，清除数据
            clearStoredData(stack);

            // 如果有玩家上下文，发送ActionBar消息
            if (player != null) {
                player.displayClientMessage(Component.translatable("message.curiosities.causal_pocket_watch.expired")
                        .withStyle(ChatFormatting.YELLOW), true);

                // 强制刷新物品栏以立即更新附魔光泽效果
                player.getInventory().setChanged();

                DebugLogger.debugLog("因果怀表过期数据已清理并通知玩家，UUID: {}", player.getUUID());
            } else {
                DebugLogger.debugLog("因果怀表过期数据已自动清理");
            }
            return false;
        }

        return true;
    }

    /**
     * 检查并清理过期数据的重载方法（不发送消息）
     *
     * @param stack           物品堆栈
     * @param currentGameTime 当前游戏时间（可选，为null时不进行时间检查）
     * @return 是否有有效的储存数据
     */
    private boolean checkAndClearExpiredData(ItemStack stack, @Nullable Long currentGameTime) {
        return checkAndClearExpiredData(stack, currentGameTime, null);
    }

    /**
     * 恢复玩家数据
     * 使用NBT完整恢复玩家状态，确保所有信息都能准确恢复
     *
     * @return 恢复是否成功
     */
    private boolean restorePlayerData(ItemStack stack, ServerPlayer player) {
        try {
            CompoundTag nbt = stack.getOrCreateTag();
            if (!nbt.contains(STORED_DATA_TAG)) {
                LOGGER.warn("因果怀表没有储存的数据");
                return false;
            }

            CompoundTag storedData = nbt.getCompound(STORED_DATA_TAG);

            // 安全验证：检查玩家UUID是否匹配
            if (storedData.contains(PLAYER_UUID_TAG)) {
                UUID storedUUID = storedData.getUUID(PLAYER_UUID_TAG);
                if (!storedUUID.equals(player.getUUID())) {
                    player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.wrong_player")
                            .withStyle(ChatFormatting.RED));
                    LOGGER.warn("因果怀表UUID验证失败，储存UUID: {}, 当前UUID: {}", storedUUID, player.getUUID());
                    return false;
                }
            }

            // 检查维度是否相同
            String storedDimension = storedData.getString(DIMENSION_TAG);
            String currentDimension = player.level().dimension().location().toString();
            if (!storedDimension.equals(currentDimension)) {
                player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.wrong_dimension")
                        .withStyle(ChatFormatting.RED));
                LOGGER.debug("因果怀表维度不匹配，储存维度: {}, 当前维度: {}", storedDimension, currentDimension);
                return false;
            }

            // 检查是否有完整的玩家NBT数据
            if (!storedData.contains(PLAYER_NBT_TAG)) {
                LOGGER.error("因果怀表缺少玩家NBT数据");
                player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.restore_error")
                        .withStyle(ChatFormatting.RED));
                return false;
            }

            // 获取储存的位置信息
            Vec3 storedPosition = null;
            float storedYaw = 0.0f;
            float storedPitch = 0.0f;

            if (storedData.contains(POSITION_TAG)) {
                CompoundTag positionTag = storedData.getCompound(POSITION_TAG);
                storedPosition = new Vec3(
                        positionTag.getDouble("x"),
                        positionTag.getDouble("y"),
                        positionTag.getDouble("z")
                );
                storedYaw = positionTag.getFloat("yaw");
                storedPitch = positionTag.getFloat("pitch");
            }

            // 使用NBT完整恢复玩家状态
            CompoundTag playerNBT = storedData.getCompound(PLAYER_NBT_TAG);
            player.load(playerNBT);

            // 恢复位置（NBT恢复后再次设置位置，确保位置准确）
            if (storedPosition != null) {
                player.teleportTo(storedPosition.x, storedPosition.y, storedPosition.z);
                player.setYRot(storedYaw);
                player.setXRot(storedPitch);
            }

            // 强制同步玩家状态到客户端
            player.refreshTabListName();
            player.getInventory().setChanged();

            DebugLogger.debugLog("因果怀表恢复玩家状态成功，玩家UUID: {}, 位置: {}",
                    player.getUUID(), storedPosition);

            return true;

        } catch (Exception e) {
            LOGGER.error("因果怀表恢复玩家状态时发生错误", e);
            // 发送错误消息给玩家
            player.sendSystemMessage(Component.translatable("message.curiosities.causal_pocket_watch.restore_error")
                    .withStyle(ChatFormatting.RED));
            return false;
        }
    }

    /**
     * 检查储存的数据是否有效（未过期）
     *
     * @param stack           物品堆栈
     * @param currentGameTime 当前游戏时间（可选，为null时不进行时间检查）
     * @return 是否有有效的储存数据
     */
    private boolean hasValidStoredData(ItemStack stack, @Nullable Long currentGameTime) {
        // 使用统一的检查和清理方法
        return checkAndClearExpiredData(stack, currentGameTime);
    }

    /**
     * 检查物品是否有附魔光泽效果
     * 只有在有有效（未过期）的储存数据时才显示光泽
     */
    @Override
    public boolean isFoil(ItemStack stack) {
        // 检查并清理过期数据，如果数据过期会自动清理并发送提示
        boolean hasValidData = checkAndClearExpiredData(stack, null);

        DebugLogger.debugLog("isFoil检查: 有有效储存数据={}", hasValidData);
        return hasValidData;
    }

    /**
     * 添加物品工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 检查物品是否启用
        if (!ModConfigManager.CAUSAL_POCKET_WATCH_ENABLED.get()) {
            tooltip.add(Component.translatable("tooltip.curiosities.item_disabled")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        // 添加物品描述
        tooltip.add(Component.translatable("item.curiosities.causal_pocket_watch.desc")
                .withStyle(ChatFormatting.GRAY));

        // 获取NBT数据，用于后续检查
        CompoundTag nbt = stack.getOrCreateTag();

        // 检查并清理过期数据，获取当前游戏时间用于过期检测
        Long currentGameTime = level != null ? level.getGameTime() : null;
        boolean hasValidData = checkAndClearExpiredData(stack, currentGameTime);

        DebugLogger.debugLog("appendHoverText: 有有效储存数据={}", hasValidData);

        if (hasValidData) {
            tooltip.add(Component.translatable("item.curiosities.causal_pocket_watch.has_data")
                    .withStyle(ChatFormatting.GREEN));

            // 显示剩余时间
            if (level != null) {
                CompoundTag storedData = nbt.getCompound(STORED_DATA_TAG);
                if (storedData.contains(STORAGE_TIME_TAG)) {
                    long storageTime = storedData.getLong(STORAGE_TIME_TAG);
                    long storageDurationTicks = ModConfigManager.CAUSAL_POCKET_WATCH_STORAGE_TIME.get() * 20L;
                    long remainingTicks = storageDurationTicks - (currentGameTime - storageTime);
                    long remainingSeconds = Math.max(0, remainingTicks / 20L);

                    DebugLogger.debugLog("appendHoverText: 剩余时间={}秒", remainingSeconds);

                    tooltip.add(Component.translatable("item.curiosities.causal_pocket_watch.remaining_time", remainingSeconds)
                            .withStyle(ChatFormatting.AQUA));
                }
            }
        } else {
            tooltip.add(Component.translatable("item.curiosities.causal_pocket_watch.no_data")
                    .withStyle(ChatFormatting.YELLOW));
        }

        // 显示冷却状态
        boolean hasCooldownTag = nbt.contains(LAST_USE_TIME_TAG);
        DebugLogger.debugLog("appendHoverText: NBT包含冷却标签={}", hasCooldownTag);

        if (hasCooldownTag && level != null) {
            long lastUseTime = nbt.getLong(LAST_USE_TIME_TAG);
            long currentTime = level.getGameTime();
            long cooldownTicks = ModConfigManager.CAUSAL_POCKET_WATCH_COOLDOWN_TIME.get() * 20L;
            long timeSinceUse = currentTime - lastUseTime;

            DebugLogger.debugLog("appendHoverText: 上次使用时间={}, 当前时间={}, 冷却时间={}, 已过时间={}",
                    lastUseTime, currentTime, cooldownTicks, timeSinceUse);

            if (timeSinceUse < cooldownTicks) {
                long remainingSeconds = (cooldownTicks - timeSinceUse) / 20L;
                DebugLogger.debugLog("appendHoverText: 冷却剩余时间={}秒", remainingSeconds);
                tooltip.add(Component.translatable("item.curiosities.causal_pocket_watch.cooldown", remainingSeconds)
                        .withStyle(ChatFormatting.RED));
            }
        }
    }

    /**
     * 不可被附魔
     */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    /**
     * 不可被书本附魔
     */
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    /**
     * 修复材料为下界之星
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == net.minecraft.world.item.Items.NETHER_STAR;
    }
}
