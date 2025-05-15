package me.tuanzi.curiosities.items.rocket_boots;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 火箭跳跃靴子
 * 允许玩家蹲下跳跃，实现高跳和缓降效果
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class RocketBootsItem extends ArmorItem {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 保存正在蓄力的玩家
    private static final Map<UUID, Long> chargingPlayers = new HashMap<>();

    // 保存正在使用火箭靴跳跃的玩家
    private static final Map<UUID, RocketJumpInfo> jumpingPlayers = new HashMap<>();

    // 保存玩家跳跃状态（是否按住跳跃键）
    private static final Map<UUID, Boolean> playerJumpStates = new HashMap<>();

    // 保存玩家跳跃开始时间
    private static final Map<UUID, Long> jumpStartTimes = new HashMap<>();

    // 保存玩家上一帧是否在地面
    private static final Map<UUID, Boolean> playerGroundStates = new HashMap<>();

    // 记录哪些玩家的跳跃已被捕获（避免重复跳跃）
    private static final Map<UUID, Long> jumpCaptured = new HashMap<>();

    // NBT标签
    private static final String TAG_FUEL = "RocketFuel";

    // 长按跳跃阈值(毫秒)
    private static final long JUMP_THRESHOLD = 250;

    // 检测跳跃的间隔时间
    private static final int JUMP_CHECK_INTERVAL = 5;

    // 玩家跳跃检测计数器
    private static final Map<UUID, Integer> jumpDetectionTicks = new HashMap<>();

    // 跳跃冷却时间（毫秒）- 防止连续跳跃
    private static final long JUMP_COOLDOWN = 500;

    public RocketBootsItem(Properties properties) {
        super(ArmorMaterials.LEATHER, Type.BOOTS, properties.durability(250));
    }

    /**
     * 处理玩家着陆事件，减少或消除摔落伤害
     */
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof RocketBootsItem && ModConfigManager.ROCKET_BOOTS_ENABLED.get()) {
            // 玩家使用火箭靴降落，减少75%的摔落伤害
            event.setDistance(event.getDistance() * 0.25f);

            // 如果曾经处于火箭跳跃状态，则取消所有伤害
            if (jumpingPlayers.containsKey(player.getUUID())) {
                event.setCanceled(true);
                // 清除跳跃状态，确保后续不会因为受伤再次触发
                jumpingPlayers.remove(player.getUUID());

                // 如果是服务器玩家，清除hurtMarked标记
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.hurtMarked = false;
                }

                LOGGER.debug("[火箭靴] 玩家 {} 火箭靴降落，取消摔落伤害", player.getName().getString());
            }
        }
    }

    /**
     * 监听实体受伤事件，防止在坠落中受伤时再次触发火箭跳跃
     */
    @SubscribeEvent
    public static void onLivingHurt(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 如果玩家穿着火箭靴且之前已经激活过火箭跳跃，但现在正在坠落
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof RocketBootsItem && player.getDeltaMovement().y < 0) {
            // 检查玩家是否有跳跃标记
            if (jumpingPlayers.containsKey(player.getUUID())) {
                RocketJumpInfo info = jumpingPlayers.get(player.getUUID());

                // 如果玩家已经进入下降阶段或者起跳后足够长时间，则不应该再次触发跳跃
                if (info.isDescending || (System.currentTimeMillis() - info.startTime > 1000)) {
                    // 清除hurtMarked标记，防止受伤时触发向量更新
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.hurtMarked = false;
                    }

                    LOGGER.debug("[火箭靴] 玩家 {} 在下落过程中受伤，阻止再次触发火箭跳跃", player.getName().getString());
                }
            }
        }
    }

    /**
     * 监听生物跳跃事件
     * 用于检测玩家跳跃并阻止普通跳跃
     */
    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 检查玩家是否装备了火箭靴
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof RocketBootsItem)) {
            return;
        }

        // 检查是否启用
        if (!ModConfigManager.ROCKET_BOOTS_ENABLED.get()) {
            return;
        }

        UUID playerId = player.getUUID();

        // 如果玩家正在蓄力或者蹲下跳跃，阻止普通跳跃
        if (chargingPlayers.containsKey(playerId) || player.isCrouching()) {
            // 取消Y轴速度，阻止普通跳跃
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0, motion.z);
            LOGGER.debug("拦截到玩家 {} 的跳跃事件，{}，已阻止普通跳跃",
                    player.getName().getString(),
                    player.isCrouching() ? "玩家正在蹲下" : "正在蓄力");

            // 如果玩家蹲下跳跃且不在蓄力中，记录跳跃
            if (player.isCrouching() && !chargingPlayers.containsKey(playerId) && !jumpCaptured.containsKey(playerId)) {
                jumpCaptured.put(playerId, System.currentTimeMillis());
                playerJumpStates.put(playerId, true);
                jumpStartTimes.put(playerId, System.currentTimeMillis());
                LOGGER.debug("玩家 {} 蹲下跳跃，记录跳跃开始时间", player.getName().getString());
            }
        }
    }

    /**
     * 检查玩家是否装备了火箭靴
     *
     * @param player 要检查的玩家
     * @return 是否装备火箭靴
     */
    private static boolean hasRocketBoots(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        return boots.getItem() instanceof RocketBootsItem;
    }

    /**
     * 查找玩家装备的火箭靴
     *
     * @param player 要检查的玩家
     * @return 火箭靴物品栈，如果没有则返回空
     */
    private static ItemStack findRocketBoots(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof RocketBootsItem) {
            return boots;
        }
        return ItemStack.EMPTY;
    }

    /**
     * 静态版本的获取燃料数量方法
     */
    private static int getFuelStatic(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof RocketBootsItem)) {
            return 0;
        }
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(TAG_FUEL);
    }

    /**
     * 每服务器tick检查玩家状态，处理火箭靴逻辑
     */
    @SubscribeEvent
    public static void serverTick(TickEvent.PlayerTickEvent event) {
        // 确保只在服务器端的POST阶段执行
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        // 获取玩家
        Player player = event.player;
        UUID playerId = player.getUUID();

        // 检查玩家是否有火箭靴
        if (!hasRocketBoots(player)) {
            // 从跟踪信息中移除
            chargingPlayers.remove(playerId);
            jumpingPlayers.remove(playerId);
            return;
        }

        // 获取火箭靴物品
        ItemStack boots = findRocketBoots(player);
        int fuel = getFuelStatic(boots);

        // 处理跳跃中状态
        if (jumpingPlayers.containsKey(playerId)) {
            RocketJumpInfo jumpInfo = jumpingPlayers.get(playerId);

            // 检查玩家是否已着地
            if (player.onGround() && System.currentTimeMillis() - jumpInfo.startTime > 500) {
                // 着地至少0.5秒后才移除状态，避免误判
                jumpingPlayers.remove(playerId);
            } else {
                // 防摔落伤害
                player.fallDistance = 0;
            }
        }
    }

    /**
     * 处理蓄力状态更新与取消
     */
    private static void handleCharging(Player player, ItemStack boots) {
        UUID playerId = player.getUUID();
        boolean wasCharging = chargingPlayers.containsKey(playerId);

        boolean isCharging = player.isShiftKeyDown() && !player.onGround() && getFuelStatic(boots) >= ModConfigManager.ROCKET_BOOTS_FUEL_CONSUMPTION.get();

        // 开始蓄力
        if (isCharging && !wasCharging) {
            chargingPlayers.put(playerId, System.currentTimeMillis());
            if (!player.level().isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.curiosities.rocket_boots.charging")
                                .withStyle(ChatFormatting.YELLOW), true);
            }
        }
        // 结束蓄力
        else if (!isCharging && wasCharging) {
            chargingPlayers.remove(playerId);

            // 检查是否满足触发条件（至少蓄力0.2秒，在服务器端）
            if (!player.level().isClientSide && !player.onGround()) {
                long chargingStart = chargingPlayers.getOrDefault(playerId, 0L);
                long chargingTime = System.currentTimeMillis() - chargingStart;

                // 蓄力时间达到要求
                if (chargingTime >= 200) {
                    // 在服务器端触发火箭跳跃
                    if (player instanceof ServerPlayer serverPlayer) {
                        LOGGER.debug("[火箭靴] 触发服务器端火箭跳跃");
                        // 直接触发火箭跳跃函数
                        if (serverPlayer.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof RocketBootsItem rocketBoots) {
                            rocketBoots.triggerRocketJump(serverPlayer, serverPlayer.getItemBySlot(EquipmentSlot.FEET), chargingTime);
                        }
                    }
                }
            }
        }
    }

    /**
     * 每游戏刻更新物品状态
     */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player) || level.isClientSide()) {
            return;
        }

        // 检查是否启用
        if (!ModConfigManager.ROCKET_BOOTS_ENABLED.get()) {
            return;
        }

        // 只处理装备在脚部的火箭靴
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots != stack) {
            return;
        }

        // 如果玩家正在跳跃中
        UUID playerId = player.getUUID();
        if (jumpingPlayers.containsKey(playerId)) {
            handleRocketJump((ServerPlayer) player, jumpingPlayers.get(playerId));
        }

        // 获取玩家状态
        boolean onGround = player.onGround();
        boolean wasOnGround = playerGroundStates.getOrDefault(playerId, false);
        boolean wasJumping = playerJumpStates.getOrDefault(playerId, false);
        boolean isCrouching = player.isCrouching();

        // 更新地面状态
        playerGroundStates.put(playerId, onGround);

        // 清理过期的跳跃捕获记录
        long currentTime = System.currentTimeMillis();
        if (jumpCaptured.containsKey(playerId) &&
                currentTime - jumpCaptured.get(playerId) > JUMP_COOLDOWN) {
            LOGGER.debug("玩家 {} 的跳跃冷却已过期，允许新的跳跃", player.getName().getString());
            jumpCaptured.remove(playerId);
        }

        // 检测跳跃意图 - 必须同时蹲下
        boolean isJumpKeyPressed = detectJumpIntent(player) && isCrouching;

        // 调试日志
        LOGGER.debug("玩家 {} 状态: 在地面={}, 蹲下={}, 跳跃键按下={}, 跳跃+蹲下={}, 蓄力中={}",
                player.getName().getString(),
                onGround,
                isCrouching,
                detectJumpIntent(player),
                isJumpKeyPressed,
                chargingPlayers.containsKey(playerId));

        // 开始蓄力状态：玩家在地面上且蹲下按住跳跃键
        if (isJumpKeyPressed && onGround) {
            // 首次按下跳跃键
            if (!wasJumping) {
                // 记录跳跃开始时间
                jumpStartTimes.put(playerId, currentTime);
                playerJumpStates.put(playerId, true);

                // 立即抑制普通跳跃
                Vec3 motion = player.getDeltaMovement();
                if (motion.y > 0) {
                    player.setDeltaMovement(motion.x, 0, motion.z);
                    LOGGER.debug("玩家 {} 初次跳跃被抑制，Y轴速度已归零", player.getName().getString());
                }

                // 记录该跳跃已被捕获
                jumpCaptured.put(playerId, currentTime);
                LOGGER.debug("玩家 {} 蹲下跳跃，已捕获并开始跟踪", player.getName().getString());
            }
            // 继续按住跳跃键
            else if (jumpStartTimes.containsKey(playerId)) {
                long pressTime = currentTime - jumpStartTimes.get(playerId);

                // 打印按压时间
                if (pressTime % 100 == 0) { // 降低日志量
                    LOGGER.debug("玩家 {} 蹲下持续按压跳跃键 {}ms, 阈值={}ms",
                            player.getName().getString(), pressTime, JUMP_THRESHOLD);
                }

                // 超过阈值且未开始蓄力，开始蓄力
                if (pressTime > JUMP_THRESHOLD && !chargingPlayers.containsKey(playerId) && canJump(stack)) {
                    chargingPlayers.put(playerId, currentTime);
                    LOGGER.debug("玩家 {} 开始蓄力", player.getName().getString());
                    player.displayClientMessage(Component.translatable("message.curiosities.rocket_boots.charging")
                            .withStyle(ChatFormatting.YELLOW), true);
                }

                // 蓄力过程中的效果
                if (chargingPlayers.containsKey(playerId) && level instanceof ServerLevel serverLevel) {
                    long chargingTime = currentTime - chargingPlayers.get(playerId);
                    float chargeProgress = Math.min(chargingTime / 1000.0f, 1.0f);

                    // 记录蓄力进度
                    if (chargingTime % 200 == 0) {  // 每200ms记录一次，避免日志过多
                        LOGGER.debug("玩家 {} 蓄力中: 时间={}ms, 进度={}%",
                                player.getName().getString(), chargingTime, Math.round(chargeProgress * 100));

                    }

                    // 显示粒子效果
                    int particleCount = (int) (10 * chargeProgress);
                    for (int i = 0; i < particleCount; i++) {
                        double offsetX = (Math.random() - 0.5) * 0.5;
                        double offsetZ = (Math.random() - 0.5) * 0.5;
                        serverLevel.sendParticles(ParticleTypes.FLAME,
                                player.getX() + offsetX,
                                player.getY() + 0.1,
                                player.getZ() + offsetZ,
                                1, 0, 0.05, 0, 0.02);
                    }

                    // 强制阻止普通跳跃，取消玩家Y轴位移
                    Vec3 motion = player.getDeltaMovement();
                    if (motion.y > 0) {
                        player.setDeltaMovement(motion.x, 0, motion.z);
                        LOGGER.debug("玩家 {} 蓄力过程中跳跃被抑制，Y轴速度已归零", player.getName().getString());
                    }
                }
            }
        }
        // 玩家松开跳跃键或不再蹲下
        else if (((!detectJumpIntent(player) || !isCrouching) && wasJumping)) {
            playerJumpStates.put(playerId, false);
            String reason = !detectJumpIntent(player) ? "松开跳跃键" : "停止蹲下";
            LOGGER.debug("玩家 {} 结束跳跃蓄力，原因: {}", player.getName().getString(), reason);

            // 如果有蓄力记录，触发火箭跳跃
            if (chargingPlayers.containsKey(playerId)) {
                long chargingStartTime = chargingPlayers.get(playerId);
                long chargingTime = currentTime - chargingStartTime;

                // 移除蓄力记录
                chargingPlayers.remove(playerId);

                // 如果蓄力时间足够并且在地面上，触发火箭跳跃
                if (chargingTime >= 200 && onGround && canJump(stack)) {
                    LOGGER.debug("玩家 {} 触发火箭跳跃, 蓄力时间={}ms", player.getName().getString(), chargingTime);
                    triggerRocketJump((ServerPlayer) player, stack, chargingTime);
                } else {
                    LOGGER.debug("玩家 {} 中断蓄力, 条件不满足: 蓄力时间={}ms, 在地面={}, 燃料足够={}",
                            player.getName().getString(), chargingTime, onGround, canJump(stack));
                }
            }

            // 清除跳跃开始时间
            jumpStartTimes.remove(playerId);
        }
    }

    /**
     * 检测玩家跳跃意图
     * 只检测跳跃，不考虑蹲下状态（蹲下状态在inventoryTick中处理）
     */
    private boolean detectJumpIntent(Player player) {
        UUID playerId = player.getUUID();
        Vec3 motion = player.getDeltaMovement();
        boolean previouslyOnGround = playerGroundStates.getOrDefault(playerId, true);
        boolean currentlyOnGround = player.onGround();

        // 已经在蓄力中，继续保持跳跃状态
        if (chargingPlayers.containsKey(playerId)) {
            return true;
        }

        // 跳跃冷却期间，不检测新的跳跃
        if (jumpCaptured.containsKey(playerId)) {
            long jumpCaptureTime = jumpCaptured.get(playerId);
            long timeSinceCapture = System.currentTimeMillis() - jumpCaptureTime;
            if (timeSinceCapture < JUMP_COOLDOWN) {
                boolean maintaining = playerJumpStates.getOrDefault(playerId, false);
                return maintaining;
            }
        }

        // 刚离开地面且Y轴速度为正，判定为跳跃
        if (previouslyOnGround && !currentlyOnGround && motion.y > 0.1) {
            // 记录该跳跃已被捕获
            jumpCaptured.put(playerId, System.currentTimeMillis());
            LOGGER.debug("玩家 {} 刚离开地面，Y轴速度={}, 判定为跳跃", player.getName().getString(), motion.y);
            return true;
        }

        // 维持已经判定的跳跃状态
        return playerJumpStates.getOrDefault(playerId, false);
    }

    /**
     * 检查火箭靴是否有足够的燃料
     */
    private boolean canJump(ItemStack stack) {
        int currentFuel = getFuel(stack);
        return currentFuel >= ModConfigManager.ROCKET_BOOTS_FUEL_CONSUMPTION.get();
    }

    /**
     * 触发火箭跳跃，根据蓄力时间计算跳跃高度
     */
    private void triggerRocketJump(ServerPlayer player, ItemStack stack, long chargingTime) {
        float chargeProgress = Math.min(chargingTime / 1000.0f, 1.0f);

        // 优化跳跃高度计算
        float maxJumpBlocks = ModConfigManager.ROCKET_BOOTS_MAX_JUMP_HEIGHT.get().floatValue(); // 配置的最大跳跃高度(方块数)

        // 在Minecraft中，初始跳跃速度约为0.42，跳跃高度约为1.25格
        // 我们需要计算出一个能够达到目标高度的初始速度
        float baseJumpVelocity = 0.42f; // 基础跳跃速度

        // 根据测试，对于单纯的抛物线运动，初始速度v与最大高度h的关系约为：h ≈ (v^2)/(2*g)
        // 在Minecraft中，重力加速度g ≈ 0.08，所以 h ≈ (v^2)/0.16
        // 反过来，要达到高度h，需要的初始速度v ≈ sqrt(0.16*h)

        // 计算所需的初始y轴速度，加上基础跳跃作为最小值
        float targetHeight = 1.0f + (chargeProgress * (maxJumpBlocks - 1.0f));
        float requiredVelocity = (float) Math.sqrt(0.16 * targetHeight);
        float jumpStrength = Math.max(baseJumpVelocity, requiredVelocity);

        // 记录用于日志的估计跳跃高度
        float estimatedBlocks = (jumpStrength * jumpStrength) / 0.16f;

        LOGGER.debug("[火箭靴] 触发火箭跳跃，蓄力进度: {}%, 计算跳跃强度: {}, 估计高度: {}格",
                Math.round(chargeProgress * 100), jumpStrength, estimatedBlocks);

        // 消耗燃料
        int fuelCost = ModConfigManager.ROCKET_BOOTS_FUEL_CONSUMPTION.get();
        int currentFuel = getFuel(stack);
        setFuel(stack, currentFuel - fuelCost);

        // 记录起始位置和速度
        double startX = player.getX();
        double startY = player.getY();
        double startZ = player.getZ();
        Vec3 oldMotion = player.getDeltaMovement();

        // 获取服务器级别
        ServerLevel level = player.serverLevel();

        // 记录跳跃信息并延长持续时间
        RocketJumpInfo jumpInfo = new RocketJumpInfo(player.getY(), chargeProgress, System.currentTimeMillis());
        jumpingPlayers.put(player.getUUID(), jumpInfo);

        LOGGER.debug("[火箭靴] 玩家起始位置: ({}, {}, {}), 原速度: ({}, {}, {})",
                startX, startY, startZ, oldMotion.x, oldMotion.y, oldMotion.z);

        // 设置垂直速度 - 保留水平速度
        player.setDeltaMovement(oldMotion.x, jumpStrength, oldMotion.z);
        LOGGER.debug("[火箭靴] 设置deltaMovement: ({}, {}, {})", oldMotion.x, jumpStrength, oldMotion.z);

        // 确保玩家能够移动和不受伤害
        player.hasImpulse = true;
        player.fallDistance = 0;
        player.hurtMarked = true; // 标记玩家需要更新运动

        // 播放音效和粒子效果
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS,
                1.0f, 0.8f + (chargeProgress * 0.4f));

        // 根据蓄力程度生成粒子效果
        spawnRocketParticles(level, player.getX(), player.getY(), player.getZ(),
                15 + (int) (chargeProgress * 20), chargeProgress);

        // 向玩家发送信息
        player.displayClientMessage(Component.translatable("message.curiosities.rocket_boots.jump")
                .withStyle(ChatFormatting.GREEN), true);

        // 创建持续性上升效果 - 每个tick检查速度并在需要时增加
        // 减少持续时间和增加力度
        int totalTicks = 20; // 1秒的持续上升检查，减少时间
        for (int i = 2; i <= totalTicks; i++) { // 从tick 2开始，因为tick 1已经使用
            final int tick = i;
            level.getServer().tell(new net.minecraft.server.TickTask(i, () -> {
                if (!player.isAlive() || player.isRemoved()) return;

                // 获取当前速度和位置
                Vec3 currentMotion = player.getDeltaMovement();
                double currentY = player.getY();
                boolean hasJumpFlag = jumpingPlayers.containsKey(player.getUUID());

                // 如果玩家不再在跳跃状态，停止处理
                if (!hasJumpFlag) return;

                // 每10tick记录一次状态
                if (tick % 10 == 0 || tick == 2) {
                    LOGGER.debug("[火箭靴] 上升状态: tick={}, 位置Y={}, 上升={}, 当前速度=({},{},{})",
                            tick, currentY, currentY - startY,
                            currentMotion.x, currentMotion.y, currentMotion.z);

                    // 同时记录当前高度对应的方块数
                    LOGGER.debug("[火箭靴] 当前高度: {}格, Y轴速度: {}",
                            Math.round(currentY - startY), currentMotion.y);
                }

                // 如果Y速度降低过多，且仍在上升阶段，给予额外推力，但推力减小
                RocketJumpInfo info = jumpingPlayers.get(player.getUUID());
                if (!info.isDescending && currentMotion.y < 0.5 && tick < 15 &&
                        currentY - startY < estimatedBlocks * 0.7) { // 减小推力有效范围

                    // 给予额外推力 - 计算随时间递减的推力，减小推力
                    float boostFactor = 1.0f - (tick / 15.0f); // 随时间逐渐减小
                    float boostStrength = jumpStrength * 0.3f * boostFactor; // 减小推力系数

                    // 应用推力，保留水平速度
                    player.setDeltaMovement(currentMotion.x, Math.max(currentMotion.y, boostStrength), currentMotion.z);
                    player.hasImpulse = true;
                    player.hurtMarked = true; // 再次标记需要更新

                    // 再次同步到客户端
                    player.connection.resetPosition();

                    LOGGER.debug("[火箭靴] 应用额外推力: tick={}, 位置Y={}, 上升={}, 原速度Y={}, 新速度Y={}",
                            tick, currentY, currentY - startY, currentMotion.y, boostStrength);

                    // 每5tick更新一次粒子效果
                    if (tick % 5 == 0) {
                        spawnRocketParticles(level, player.getX(), currentY, player.getZ(), 15, chargeProgress * boostFactor);
                    }
                }
            }));
        }

        LOGGER.warn("[火箭靴] 火箭跳跃触发完成: 蓄力时间={}ms, 跳跃强度={}, 消耗燃料={}, 剩余燃料={}, 估计高度={}格",
                chargingTime, jumpStrength, fuelCost, currentFuel - fuelCost, Math.round(estimatedBlocks));
    }

    /**
     * 生成火箭粒子效果
     */
    private void spawnRocketParticles(ServerLevel level, double x, double y, double z, int count, float intensity) {
        // 尾气粒子
        for (int i = 0; i < count; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetZ = (Math.random() - 0.5) * 0.5;
            level.sendParticles(ParticleTypes.LARGE_SMOKE,
                    x + offsetX, y, z + offsetZ,
                    1, 0, -0.2, 0, 0.1);
        }

        // 火焰粒子
        for (int i = 0; i < count / 2; i++) {
            double offsetX = (Math.random() - 0.5) * 0.5;
            double offsetZ = (Math.random() - 0.5) * 0.5;
            level.sendParticles(ParticleTypes.FLAME,
                    x + offsetX, y - 0.2, z + offsetZ,
                    1, 0, -0.1, 0, 0.05);
        }
    }

    /**
     * 处理火箭跳跃过程中的玩家状态
     */
    private void handleRocketJump(ServerPlayer player, RocketJumpInfo jumpInfo) {
        // 跳跃高度计算
        double jumpHeight = player.getY() - jumpInfo.startY;
        // 根据配置计算最大高度限制
        double maxHeight = ModConfigManager.ROCKET_BOOTS_MAX_JUMP_HEIGHT.get() * jumpInfo.chargePercent;

        // 增加跳跃计数
        jumpInfo.ticks++;

        if (jumpInfo.ticks % 20 == 0) { // 每秒记录一次
            LOGGER.debug("[火箭靴] 跳跃状态: 当前高度={}, 已上升={}格, 最大高度={}格, Y轴速度={}, tick={}",
                    player.getY(), Math.round(jumpHeight), Math.round(maxHeight), player.getDeltaMovement().y, jumpInfo.ticks);
        }

        // 如果玩家已经开始下落但不在下降状态，设置为下降状态
        if (player.getDeltaMovement().y < 0 && !jumpInfo.isDescending && jumpInfo.ticks > 20) {
            jumpInfo.isDescending = true;
            LOGGER.debug("[火箭靴] 玩家 {} 开始下降，当前高度 {}格, 上升高度 {}格",
                    player.getName().getString(), Math.round(player.getY()), Math.round(jumpHeight));
        }

        // 最大高度检查
        if (jumpHeight >= maxHeight && !jumpInfo.isDescending) {
            // 如果到达最大高度，开始缓降
            jumpInfo.isDescending = true;
            LOGGER.debug("[火箭靴] 玩家 {} 达到最大高度 {}格, 开始缓降",
                    player.getName().getString(), Math.round(jumpHeight));
        }

        // 如果正在下降，应用缓降效果，但缓降效果减弱
        if (jumpInfo.isDescending) {
            // 计算缓降效果，减小下落速度
            Vec3 motion = player.getDeltaMovement();
            if (motion.y < -0.1) {
                // 减缓坠落速度，允许更快的下坠速度
                double newYVelocity = Math.max(motion.y, -0.8);
                player.setDeltaMovement(motion.x, newYVelocity, motion.z);

                // 生成缓降粒子效果
                if (jumpInfo.ticks % 5 == 0) { // 每5tick生成一次粒子
                    ServerLevel level = player.serverLevel();
                    int particleCount = 5;
                    for (int i = 0; i < particleCount; i++) {
                        double offsetX = (Math.random() - 0.5) * 0.5;
                        double offsetZ = (Math.random() - 0.5) * 0.5;
                        level.sendParticles(ParticleTypes.CLOUD,
                                player.getX() + offsetX,
                                player.getY(),
                                player.getZ() + offsetZ,
                                1, 0, 0.05, 0, 0.02);
                    }
                }
            }
        }

        // 如果玩家落地或跳跃时间过长，移除跳跃状态
        if (player.onGround() || (System.currentTimeMillis() - jumpInfo.startTime > 30000)) {
            String reason = player.onGround() ? "玩家落地" : "超时";
            LOGGER.debug("[火箭靴] 结束火箭跳跃 - 原因: {}, 总持续时间: {}ms, 最终高度: {}格, 上升高度: {}格",
                    reason, System.currentTimeMillis() - jumpInfo.startTime,
                    Math.round(player.getY()), Math.round(player.getY() - jumpInfo.startY));

            // 完全移除跳跃状态，防止重新激活
            jumpingPlayers.remove(player.getUUID());
            // 清除hurtMarked标记，防止后续受伤时触发额外跳跃
            player.hurtMarked = false;

            // 如果是落地而不是超时，产生着陆特效
            if (player.onGround()) {
                ServerLevel level = player.serverLevel();
                level.sendParticles(ParticleTypes.EXPLOSION,
                        player.getX(), player.getY(), player.getZ(),
                        1, 0, 0, 0, 0);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5f, 1.2f);
            }
        }
    }

    /**
     * 使用物品时充能
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide() || !ModConfigManager.ROCKET_BOOTS_ENABLED.get()) {
            return InteractionResultHolder.pass(stack);
        }

        // 如果玩家手持火药并按下使用键，给火箭靴充能
        ItemStack offHandItem = player.getOffhandItem();
        ItemStack mainHandItem = player.getMainHandItem();

        // 检查玩家是否有火药（任一只手）
        boolean hasGunpowderInOffhand = offHandItem.getItem() == Items.GUNPOWDER;
        boolean hasGunpowderInMainhand = mainHandItem.getItem() == Items.GUNPOWDER;

        if (hasGunpowderInOffhand || hasGunpowderInMainhand) {
            int currentFuel = getFuel(stack);
            int maxFuel = ModConfigManager.ROCKET_BOOTS_MAX_FUEL.get();

            // 检查是否已经充满
            if (currentFuel >= maxFuel) {
                player.displayClientMessage(Component.translatable("message.curiosities.rocket_boots.full_fuel")
                        .withStyle(ChatFormatting.RED), true);
                return InteractionResultHolder.fail(stack);
            }

            // 计算可以充能的数量
            int fuelToAdd = Math.min(10, maxFuel - currentFuel);

            // 添加燃料并消耗火药
            setFuel(stack, currentFuel + fuelToAdd);

            // 根据使用的手消耗火药
            if (hasGunpowderInMainhand && hand == InteractionHand.MAIN_HAND) {
                mainHandItem.shrink(1);
            } else if (hasGunpowderInOffhand) {
                offHandItem.shrink(1);
            }

            // 播放充能音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 0.5f, 1.0f);

            // 生成充能粒子效果
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        10, 0.2, 0.2, 0.2, 0.05);
            }

            // 显示充能成功消息
            player.displayClientMessage(Component.translatable("message.curiosities.rocket_boots.fuel_added", fuelToAdd)
                    .withStyle(ChatFormatting.GREEN), true);

            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    /**
     * 获取燃料数量
     */
    public int getFuel(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(TAG_FUEL);
    }

    /**
     * 设置燃料数量
     */
    public void setFuel(ItemStack stack, int fuel) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(TAG_FUEL, Math.max(0, Math.min(fuel, ModConfigManager.ROCKET_BOOTS_MAX_FUEL.get())));
    }

    /**
     * 显示物品提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        int currentFuel = getFuel(stack);
        int maxFuel = ModConfigManager.ROCKET_BOOTS_MAX_FUEL.get();

        tooltip.add(Component.translatable("item.curiosities.rocket_boots.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.rocket_boots.tooltip.2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.curiosities.rocket_boots.tooltip.3", currentFuel, maxFuel)
                .withStyle(currentFuel > 0 ? ChatFormatting.GREEN : ChatFormatting.RED));

        if (!ModConfigManager.ROCKET_BOOTS_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.disabled").withStyle(ChatFormatting.RED));
        }
    }

    /**
     * 返回盔甲纹理的资源路径
     * 这是盔甲渲染的关键方法，确保穿戴时正确显示纹理
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        // 返回自定义盔甲纹理的路径
        // 盔甲纹理分为两层：layer1是主体部分，layer2是附加部分
        // 因为火箭靴是靴子，所以使用layer1纹理
        return Curiosities.MODID + ":textures/models/armor/rocket_boots_layer_1.png";
    }

    /**
     * 处理物品栏中的物品交互
     * 允许玩家拿着火药右键点击火箭靴来添加燃料
     */
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action,
                                            Player player, SlotAccess access) {
        if (action == ClickAction.SECONDARY && other.getItem() == Items.GUNPOWDER) {
            // 检查是否启用
            if (!ModConfigManager.ROCKET_BOOTS_ENABLED.get()) {
                return false;
            }

            int currentFuel = getFuel(stack);
            int maxFuel = ModConfigManager.ROCKET_BOOTS_MAX_FUEL.get();

            // 检查是否已经充满
            if (currentFuel >= maxFuel) {
                player.displayClientMessage(Component.translatable("message.curiosities.rocket_boots.full_fuel")
                        .withStyle(ChatFormatting.RED), true);
                return false;
            }

            // 计算可以充能的数量
            int fuelToAdd = Math.min(10, maxFuel - currentFuel);

            // 添加燃料并消耗火药
            setFuel(stack, currentFuel + fuelToAdd);
            other.shrink(1);

            // 播放充能音效
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 0.5f, 1.0f);

            if (player.level() instanceof ServerLevel serverLevel) {
                // 生成充能粒子效果
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        10, 0.2, 0.2, 0.2, 0.05);
            }

            // 显示充能成功消息
            player.displayClientMessage(Component.translatable("message.curiosities.rocket_boots.fuel_added", fuelToAdd)
                    .withStyle(ChatFormatting.GREEN), true);

            return true;
        }

        return false;
    }

    /**
     * 记录火箭跳跃的信息
     */
    private static class RocketJumpInfo {
        public final double startY;           // 起跳高度
        public final float chargePercent;     // 蓄力百分比
        public final long startTime;          // 开始跳跃时间
        public boolean isDescending = false;  // 是否开始下降
        public boolean maxHeightReached = false; // 是否已达到最大高度
        public int ticks = 0;                 // 跳跃持续的tick数

        public RocketJumpInfo(double startY, float chargePercent, long startTime) {
            this.startY = startY;
            this.chargePercent = chargePercent;
            this.startTime = startTime;
        }
    }
} 