package me.tuanzi.curiosities.items.scroll_of_spacetime;

import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 时空卷轴物品
 * 右键使用可在当前位置创建时空锚点，再次使用可瞬间传送回锚点位置
 */
public class ScrollOfSpacetimeItem extends Item {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrollOfSpacetimeItem.class);
    // NBT标签常量
    private static final String TAG_ANCHOR_X = "AnchorX";
    private static final String TAG_ANCHOR_Y = "AnchorY";
    private static final String TAG_ANCHOR_Z = "AnchorZ";
    private static final String TAG_DIMENSION = "Dimension";
    private static final String TAG_HAS_ANCHOR = "HasAnchor";
    // 传送时需要的使用时间（以刻为单位，1.5秒=30刻）
    private static final int TELEPORT_USE_TIME = 30;

    public ScrollOfSpacetimeItem() {
        super(new Item.Properties().durability(300));
    }

    /**
     * 当物品有锚点时显示附魔光泽效果
     */
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        CompoundTag tag = stack.getTag();
        // 如果有锚点，显示附魔光泽
        if (tag != null && tag.getBoolean(TAG_HAS_ANCHOR)) {
            return true;
        }
        // 否则，只有在真正有附魔时才显示光泽（继承原始逻辑）
        return super.isFoil(stack);
    }

    /**
     * 定义物品的最大使用时间
     *
     * @param stack 物品堆
     * @return 最大使用时间（以刻为单位）
     */
    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        // 只有在有锚点且准备传送时才需要使用时间
        if (tag.getBoolean(TAG_HAS_ANCHOR)) {
            return TELEPORT_USE_TIME;
        }
        // 设置锚点时不需要使用时间
        return 1;
    }

    /**
     * 定义物品的使用动画
     *
     * @param stack 物品堆
     * @return 使用动画类型
     */
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        // 有锚点时使用法杖类动画
        if (tag.getBoolean(TAG_HAS_ANCHOR)) {
            return UseAnim.SPEAR;
        }
        return UseAnim.NONE;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        // 如果物品被禁用，直接返回
        if (!ModConfigManager.SCROLL_OF_SPACETIME_ENABLED.get()) {
            player.displayClientMessage(Component.translatable("item.curiosities.scroll_of_spacetime.disabled").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();

        // 如果玩家按住Shift键，清除锚点
        if (player.isShiftKeyDown()) {
            if (tag.getBoolean(TAG_HAS_ANCHOR)) {
                tag.putBoolean(TAG_HAS_ANCHOR, false);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 0.5F);
                player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.anchor_cleared"), true);
                return InteractionResultHolder.success(stack);
            } else {
                player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.no_anchor"), true);
                return InteractionResultHolder.fail(stack);
            }
        }

        // 检查冷却时间（使用原版冷却系统）
        if (player.getCooldowns().isOnCooldown(this)) {
            float cooldownPercent = player.getCooldowns().getCooldownPercent(this, 0);
            int totalCooldownTicks = ModConfigManager.SCROLL_OF_SPACETIME_COOLDOWN.get() * 20;
            int remainingTicks = (int) (cooldownPercent * totalCooldownTicks);
            int remainingSeconds = (int) Math.ceil(remainingTicks / 20.0);
            player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.cooldown", remainingSeconds), true);
            return InteractionResultHolder.fail(stack);
        }

        if (!tag.getBoolean(TAG_HAS_ANCHOR)) {
            // 创建新的锚点
            tag.putBoolean(TAG_HAS_ANCHOR, true);
            tag.putInt(TAG_ANCHOR_X, Mth.floor(player.getX()));
            tag.putInt(TAG_ANCHOR_Y, Mth.floor(player.getY()));
            tag.putInt(TAG_ANCHOR_Z, Mth.floor(player.getZ()));
            tag.putString(TAG_DIMENSION, level.dimension().location().toString());

            // 播放创建锚点的音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.0F);

            // 创建锚点时的粒子效果
            if (level instanceof ServerLevel serverLevel) {
                RandomSource random = level.getRandom();
                for (int i = 0; i < 20; i++) {
                    double offsetX = random.nextGaussian() * 0.2;
                    double offsetY = random.nextGaussian() * 0.2;
                    double offsetZ = random.nextGaussian() * 0.2;
                    serverLevel.sendParticles(
                            ParticleTypes.PORTAL,
                            player.getX() + offsetX,
                            player.getY() + 1.0 + offsetY,
                            player.getZ() + offsetZ,
                            1, 0, 0, 0, 0
                    );
                }
            }

            player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.anchor_created"), true);
            return InteractionResultHolder.success(stack);
        } else {
            // 检查传送距离
            int anchorX = tag.getInt(TAG_ANCHOR_X);
            int anchorY = tag.getInt(TAG_ANCHOR_Y);
            int anchorZ = tag.getInt(TAG_ANCHOR_Z);
            String dimensionKey = tag.getString(TAG_DIMENSION);

            // 确认当前维度与锚点维度相同
            if (!dimensionKey.equals(level.dimension().location().toString())) {
                player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.wrong_dimension"), true);
                return InteractionResultHolder.fail(stack);
            }

            // 检查传送距离
            double distance = Math.sqrt(
                    Math.pow(player.getX() - anchorX, 2) +
                            Math.pow(player.getY() - anchorY, 2) +
                            Math.pow(player.getZ() - anchorZ, 2)
            );

            int maxDistance = ModConfigManager.SCROLL_OF_SPACETIME_MAX_DISTANCE.get();
            if (distance > maxDistance) {
                player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.too_far", (int) distance, maxDistance), true);
                return InteractionResultHolder.fail(stack);
            }

            // 启动使用计时
            player.startUsingItem(hand);
            // 播放准备传送音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.6F, 1.0F);

            // 传送起始点粒子效果
            if (level instanceof ServerLevel serverLevel) {
                spawnTeleportationParticles(serverLevel, player.getX(), player.getY(), player.getZ(), 10);
            }

            return InteractionResultHolder.consume(stack);
        }
    }

    /**
     * 物品在使用过程中每帧都会调用此方法
     */
    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack, int remainingUseTicks) {
        if (!(livingEntity instanceof Player player) || level.isClientSide()) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean(TAG_HAS_ANCHOR)) {
            return;
        }

        // 在传送过程中每隔几帧生成粒子效果
        if (remainingUseTicks % 4 == 0 && level instanceof ServerLevel serverLevel) {
            // 玩家周围的粒子效果
            spawnTeleportationParticles(serverLevel, player.getX(), player.getY(), player.getZ(), 5);

            // 目标位置的粒子效果
            int anchorX = tag.getInt(TAG_ANCHOR_X);
            int anchorY = tag.getInt(TAG_ANCHOR_Y);
            int anchorZ = tag.getInt(TAG_ANCHOR_Z);
            spawnTeleportationParticles(serverLevel, anchorX + 0.5, anchorY + 1.0, anchorZ + 0.5, 5);

            // 连接线粒子效果 - 创建一条从玩家到目标点的粒子线
            if (remainingUseTicks % 8 == 0) {
                createParticleLine(
                        serverLevel,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        anchorX + 0.5, anchorY + 1.0, anchorZ + 0.5,
                        10, ParticleTypes.END_ROD
                );
            }
        }
    }

    /**
     * 物品使用完成时执行传送
     *
     * @param stack        物品堆
     * @param level        世界
     * @param entityLiving 使用物品的实体
     * @return 使用后的物品堆
     */
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving) {
        if (!(entityLiving instanceof Player player)) {
            return stack;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean(TAG_HAS_ANCHOR)) {
            return stack;
        }

        int anchorX = tag.getInt(TAG_ANCHOR_X);
        int anchorY = tag.getInt(TAG_ANCHOR_Y);
        int anchorZ = tag.getInt(TAG_ANCHOR_Z);

        // 设置冷却时间（使用原版系统）
        int cooldownTicks = ModConfigManager.SCROLL_OF_SPACETIME_COOLDOWN.get() * 20; // 转换为游戏刻
        player.getCooldowns().addCooldown(this, cooldownTicks);

        // 消耗耐久
        int durabilityUse = ModConfigManager.SCROLL_OF_SPACETIME_DURABILITY_COST.get();
        stack.hurtAndBreak(durabilityUse, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

        // 在传送前添加大量粒子效果
        if (level instanceof ServerLevel serverLevel) {
            // 玩家位置的爆发粒子
            spawnTeleportationBurst(serverLevel, player.getX(), player.getY(), player.getZ(), 30);

            // 目标位置的爆发粒子
            spawnTeleportationBurst(serverLevel, anchorX + 0.5, anchorY + 1.0, anchorZ + 0.5, 30);
        }

        // 播放传送音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // 传送玩家
        if (player instanceof ServerPlayer) {
            player.teleportTo(anchorX + 0.5, anchorY, anchorZ + 0.5);
        }

        // 播放到达锚点的音效
        level.playSound(null, anchorX, anchorY, anchorZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.teleported"), true);

        return stack;
    }

    /**
     * 物品使用被中断时停止传送
     */
    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            player.displayClientMessage(Component.translatable("message.curiosities.scroll_of_spacetime.teleport_canceled"), true);
            // 播放取消音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.3F, 0.5F);

            // 取消时的粒子效果
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 10; i++) {
                    double offsetX = level.getRandom().nextGaussian() * 0.5;
                    double offsetY = level.getRandom().nextGaussian() * 0.5;
                    double offsetZ = level.getRandom().nextGaussian() * 0.5;
                    serverLevel.sendParticles(
                            ParticleTypes.SMOKE,
                            player.getX() + offsetX,
                            player.getY() + 1.0 + offsetY,
                            player.getZ() + offsetZ,
                            1, 0, 0, 0, 0.05
                    );
                }
            }
        }
    }

    /**
     * 在指定位置生成传送粒子效果
     */
    private void spawnTeleportationParticles(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        // 随机选择几种粒子效果
        ParticleOptions[] particles = {
                ParticleTypes.PORTAL,
                ParticleTypes.REVERSE_PORTAL,
                ParticleTypes.END_ROD,
                ParticleTypes.ENCHANT
        };

        for (int i = 0; i < count; i++) {
            double offsetX = random.nextGaussian() * 0.3;
            double offsetY = random.nextGaussian() * 0.3;
            double offsetZ = random.nextGaussian() * 0.3;

            ParticleOptions particle = particles[random.nextInt(particles.length)];

            level.sendParticles(
                    particle,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1, 0, 0, 0, 0.05
            );
        }
    }

    /**
     * 在指定位置生成大量爆发式传送粒子
     */
    private void spawnTeleportationBurst(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        // 传送完成时的爆发粒子
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = random.nextDouble() * 0.5;
            double height = random.nextDouble() * 2 - 1;

            double offsetX = Math.sin(angle) * radius;
            double offsetY = height * 0.5;
            double offsetZ = Math.cos(angle) * radius;

            level.sendParticles(
                    ParticleTypes.PORTAL,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1, 0, 0, 0, 0.1
            );

            if (i % 3 == 0) {
                level.sendParticles(
                        ParticleTypes.END_ROD,
                        x + offsetX * 1.2,
                        y + offsetY * 1.2,
                        z + offsetZ * 1.2,
                        1, 0, 0, 0, 0.05
                );
            }
        }
    }

    /**
     * 创建一条从起点到终点的粒子线
     */
    private void createParticleLine(ServerLevel level, double startX, double startY, double startZ,
                                    double endX, double endY, double endZ, int points, ParticleOptions particle) {
        for (int i = 0; i < points; i++) {
            double progress = (double) i / (points - 1);
            double x = startX + (endX - startX) * progress;
            double y = startY + (endY - startY) * progress;
            double z = startZ + (endZ - startZ) * progress;

            // 添加一些随机偏移使线条看起来更自然
            double offsetX = level.getRandom().nextGaussian() * 0.1;
            double offsetY = level.getRandom().nextGaussian() * 0.1;
            double offsetZ = level.getRandom().nextGaussian() * 0.1;

            level.sendParticles(particle, x + offsetX, y + offsetY, z + offsetZ, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!ModConfigManager.SCROLL_OF_SPACETIME_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.disabled").withStyle(ChatFormatting.RED));
            return;
        }

        tooltip.add(Component.translatable("item.curiosities.scroll_of_spacetime.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.scroll_of_spacetime.tooltip.2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.scroll_of_spacetime.tooltip.3").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.scroll_of_spacetime.tooltip.4").withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("item.curiosities.scroll_of_spacetime.tooltip.max_distance",
                ModConfigManager.SCROLL_OF_SPACETIME_MAX_DISTANCE.get()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.curiosities.scroll_of_spacetime.tooltip.cooldown",
                ModConfigManager.SCROLL_OF_SPACETIME_COOLDOWN.get()).withStyle(ChatFormatting.BLUE));

        // 显示是否有锚点
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean(TAG_HAS_ANCHOR)) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("item.curiosities.scroll_of_spacetime.tooltip.has_anchor").withStyle(ChatFormatting.GREEN));
            int x = tag.getInt(TAG_ANCHOR_X);
            int y = tag.getInt(TAG_ANCHOR_Y);
            int z = tag.getInt(TAG_ANCHOR_Z);
            tooltip.add(Component.literal(String.format("X: %d, Y: %d, Z: %d", x, y, z)).withStyle(ChatFormatting.AQUA));
        }
    }
} 