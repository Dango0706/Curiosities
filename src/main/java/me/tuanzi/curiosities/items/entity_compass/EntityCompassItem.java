package me.tuanzi.curiosities.items.entity_compass;

import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.network.PacketEntityGlow;
import me.tuanzi.curiosities.network.PacketHandler;
import me.tuanzi.curiosities.network.PacketOpenEntitySelectionGui;
import me.tuanzi.curiosities.util.DebugLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 生物指南针物品
 * 右键使用：寻找生物功能
 * Shift+右键使用：选择生物功能
 */
public class EntityCompassItem extends Item {

    private static final String SELECTED_ENTITY_KEY = "SelectedEntity";
    private static final String COOLDOWN_KEY = "Cooldown";
    private static final int COOLDOWN_TICKS = 100; // 5秒冷却时间

    public EntityCompassItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .fireResistant()
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 检查功能是否启用
        if (!ModConfigManager.ENTITY_COMPASS_ENABLED.get()) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("item.curiosities.entity_compass.disabled")
                                .withStyle(ChatFormatting.RED),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        // 检查冷却时间
        if (isOnCooldown(stack)) {
            if (!level.isClientSide) {
                int remainingTicks = getCooldownRemaining(stack);
                int remainingSeconds = (remainingTicks + 19) / 20; // 向上取整
                player.displayClientMessage(
                        Component.translatable("message.curiosities.entity_compass.cooldown", remainingSeconds)
                                .withStyle(ChatFormatting.RED),
                        true
                );
            }
            return InteractionResultHolder.fail(stack);
        }

        if (player.isShiftKeyDown()) {
            // Shift+右键：打开生物选择GUI
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                // 发送网络包到客户端打开GUI
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new PacketOpenEntitySelectionGui(hand));

                player.displayClientMessage(
                        Component.translatable("message.curiosities.entity_compass.gui_opened")
                                .withStyle(ChatFormatting.GREEN),
                        false
                );
            }
        } else {
            // 右键：寻找生物功能
            if (!level.isClientSide) {
                findAndHighlightEntities(level, player, stack);
                setCooldown(stack);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * 寻找并高亮显示生物
     */
    private void findAndHighlightEntities(Level level, Player player, ItemStack stack) {
        String selectedEntityType = getSelectedEntityType(stack);

        if (selectedEntityType == null || selectedEntityType.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("message.curiosities.entity_compass.no_entity_selected")
                            .withStyle(ChatFormatting.YELLOW),
                    false
            );
            return;
        }

        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(
                net.minecraft.resources.ResourceLocation.tryParse(selectedEntityType)
        );

        if (entityType == null) {
            player.displayClientMessage(
                    Component.translatable("message.curiosities.entity_compass.invalid_entity")
                            .withStyle(ChatFormatting.RED),
                    false
            );
            return;
        }

        int range = ModConfigManager.ENTITY_COMPASS_GLOW_RANGE.get();
        BlockPos playerPos = player.blockPosition();
        AABB searchArea = new AABB(
                playerPos.getX() - range, playerPos.getY() - range, playerPos.getZ() - range,
                playerPos.getX() + range, playerPos.getY() + range, playerPos.getZ() + range
        );

        List<Entity> foundEntities = level.getEntitiesOfClass(Entity.class, searchArea,
                entity -> entity.getType() == entityType && entity != player);

        if (foundEntities.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("message.curiosities.entity_compass.no_entities_found",
                                    Component.translatable(entityType.getDescriptionId()))
                            .withStyle(ChatFormatting.YELLOW),
                    false
            );
        } else {
            // 为找到的生物添加发光效果
            List<Integer> entityIds = new ArrayList<>();
            for (Entity entity : foundEntities) {
                entityIds.add(entity.getId());
                DebugLogger.debugLog("[EntityCompass] 添加生物到发光列表: {} ID: {}", entity.getType().getDescriptionId(), entity.getId());
            }

            // 发送网络包到客户端添加发光效果
            if (player instanceof ServerPlayer serverPlayer) {
                DebugLogger.debugLog("[EntityCompass] 发送发光效果网络包到玩家: {}, 生物数量: {}", serverPlayer.getName().getString(), entityIds.size());
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new PacketEntityGlow(entityIds));
                DebugLogger.debugLog("[EntityCompass] 网络包已发送");
            }

            player.displayClientMessage(
                    Component.translatable("message.curiosities.entity_compass.entities_found",
                                    foundEntities.size(),
                                    Component.translatable(entityType.getDescriptionId()))
                            .withStyle(ChatFormatting.GREEN),
                    false
            );

            // 播放音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    /**
     * 获取选定的生物类型
     */
    private String getSelectedEntityType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(SELECTED_ENTITY_KEY)) {
            return tag.getString(SELECTED_ENTITY_KEY);
        }
        return null;
    }

    /**
     * 设置选定的生物类型
     */
    public void setSelectedEntityType(ItemStack stack, String entityType) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(SELECTED_ENTITY_KEY, entityType);
    }

    /**
     * 检查是否在冷却中
     */
    private boolean isOnCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(COOLDOWN_KEY)) {
            long cooldownEnd = tag.getLong(COOLDOWN_KEY);
            return System.currentTimeMillis() < cooldownEnd;
        }
        return false;
    }

    /**
     * 获取剩余冷却时间（刻）
     */
    private int getCooldownRemaining(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(COOLDOWN_KEY)) {
            long cooldownEnd = tag.getLong(COOLDOWN_KEY);
            long remaining = cooldownEnd - System.currentTimeMillis();
            return (int) (remaining / 50); // 转换为刻
        }
        return 0;
    }

    /**
     * 设置冷却时间
     */
    private void setCooldown(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong(COOLDOWN_KEY, System.currentTimeMillis() + (COOLDOWN_TICKS * 50L));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (!ModConfigManager.ENTITY_COMPASS_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.entity_compass.tooltip.disabled")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        tooltip.add(Component.translatable("item.curiosities.entity_compass.tooltip.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.entity_compass.tooltip.2")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.entity_compass.tooltip.3")
                .withStyle(ChatFormatting.GRAY));

        String selectedEntity = getSelectedEntityType(stack);
        if (selectedEntity != null && !selectedEntity.isEmpty()) {
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(
                    net.minecraft.resources.ResourceLocation.tryParse(selectedEntity)
            );
            if (entityType != null) {
                tooltip.add(Component.translatable("item.curiosities.entity_compass.tooltip.selected",
                                Component.translatable(entityType.getDescriptionId()))
                        .withStyle(ChatFormatting.AQUA));
            }
        }

        int range = ModConfigManager.ENTITY_COMPASS_GLOW_RANGE.get();
        tooltip.add(Component.translatable("item.curiosities.entity_compass.tooltip.range", range)
                .withStyle(ChatFormatting.YELLOW));

        if (isOnCooldown(stack)) {
            int remainingSeconds = (getCooldownRemaining(stack) + 19) / 20;
            tooltip.add(Component.translatable("item.curiosities.entity_compass.tooltip.cooldown", remainingSeconds)
                    .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // 当选择了生物时显示附魔光泽
        String selectedEntity = getSelectedEntityType(stack);
        return selectedEntity != null && !selectedEntity.isEmpty();
    }
}
