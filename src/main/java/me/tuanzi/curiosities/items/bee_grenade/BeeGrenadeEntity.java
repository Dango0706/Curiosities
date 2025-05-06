package me.tuanzi.curiosities.items.bee_grenade;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.entities.ModEntities;
import me.tuanzi.curiosities.items.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 蜜蜂手雷实体
 * 用于处理蜜蜂手雷投掷后的逻辑
 */
public class BeeGrenadeEntity extends ThrowableItemProjectile {
    private static final Logger LOGGER = LogUtils.getLogger();
    // 使用ConcurrentHashMap存储每一组蜜蜂的潜在目标
    // 键是爆炸ID（用UUID表示），值是区域内的所有可能目标
    private static final Map<UUID, List<LivingEntity>> BEE_TARGETS = new ConcurrentHashMap<>();
    // 存储蜜蜂与其爆炸ID的关联
    private static final Map<Bee, UUID> BEE_TO_EXPLOSION = new WeakHashMap<>();
    // 存储爆炸ID与是否对玩家友好的设置
    private static final Map<UUID, Boolean> EXPLOSION_PLAYER_FRIENDLY = new ConcurrentHashMap<>();
    // 存储爆炸ID与投掷者的关联
    private static final Map<UUID, UUID> EXPLOSION_THROWER = new ConcurrentHashMap<>();
    private Player thrower;

    public BeeGrenadeEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BeeGrenadeEntity(Level level, LivingEntity livingEntity) {
        super(ModEntities.BEE_GRENADE.get(), livingEntity, level);
        if (livingEntity instanceof Player) {
            this.thrower = (Player) livingEntity;
        }
    }

    public BeeGrenadeEntity(Level level, double x, double y, double z) {
        super(ModEntities.BEE_GRENADE.get(), x, y, z, level);
    }

    /**
     * 获取下一个目标
     *
     * @param bee           蜜蜂实体
     * @param currentTarget 当前目标（可能已死亡）
     * @return 下一个目标，如果没有则返回null
     */
    public static LivingEntity getNextTarget(Bee bee, LivingEntity currentTarget) {
        // 获取蜜蜂关联的爆炸ID
        UUID explosionId = BEE_TO_EXPLOSION.get(bee);
        if (explosionId == null) return null;

        // 获取爆炸关联的目标列表
        List<LivingEntity> targets = BEE_TARGETS.get(explosionId);
        if (targets == null || targets.isEmpty()) return null;

        // 过滤掉已死亡的目标并获取下一个目标
        targets.removeIf(entity -> !entity.isAlive());

        // 如果当前目标已死亡或不存在，选择一个新目标
        if (currentTarget == null || !currentTarget.isAlive()) {
            // 获取投掷者友好设置
            Boolean playerFriendly = EXPLOSION_PLAYER_FRIENDLY.get(explosionId);
            UUID throwerUUID = EXPLOSION_THROWER.get(explosionId);

            // 过滤可能的目标
            List<LivingEntity> validTargets = new ArrayList<>();
            for (LivingEntity entity : targets) {
                if (!entity.isAlive()) continue;

                // 不攻击其他蜜蜂
                if (entity instanceof Bee) continue;

                // 处理玩家目标逻辑
                if (entity instanceof Player) {
                    // 如果配置为对玩家友好，则不攻击任何玩家
                    if (playerFriendly != null && playerFriendly) continue;

                    // 无论配置如何，都不攻击投掷者
                    if (throwerUUID != null && entity.getStringUUID().equals(throwerUUID.toString())) continue;
                }

                validTargets.add(entity);
            }

            if (!validTargets.isEmpty()) {
                // 选择最近的目标
                LivingEntity closestTarget = null;
                double closestDistSq = Double.MAX_VALUE;

                for (LivingEntity entity : validTargets) {
                    double distSq = entity.distanceToSqr(bee);
                    if (distSq < closestDistSq) {
                        closestDistSq = distSq;
                        closestTarget = entity;
                    }
                }

                if (closestTarget != null) {
                    LOGGER.debug("蜜蜂选择了新目标: {}", closestTarget.getName().getString());
                    return closestTarget;
                }
            }
        }

        return null;
    }

    /**
     * 清理超过一分钟未使用的目标列表
     */
    public static void cleanupOldTargets() {
        long currentTime = System.currentTimeMillis();
        BEE_TARGETS.entrySet().removeIf(entry -> {
            // 如果60秒内没有蜜蜂引用这个爆炸ID，则清理它
            return !BEE_TO_EXPLOSION.containsValue(entry.getKey());
        });

        // 同时清理相关的映射
        EXPLOSION_PLAYER_FRIENDLY.entrySet().removeIf(entry -> !BEE_TARGETS.containsKey(entry.getKey()));
        EXPLOSION_THROWER.entrySet().removeIf(entry -> !BEE_TARGETS.containsKey(entry.getKey()));
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BEE_GRENADE.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    /**
     * 实体碰撞后处理
     */
    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide) {
            // 创建爆炸效果
            boolean blockDamage = ModConfigManager.BEE_GRENADE_DESTROY_BLOCKS.get();

            // 根据配置决定是否破坏方块
            if (blockDamage) {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false,
                        Level.ExplosionInteraction.TNT);
            } else {
                // 创建不破坏方块的轻微爆炸
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.5F, false,
                        Level.ExplosionInteraction.NONE);
            }

            // 创建蜂蜜减速区域
            if (ModConfigManager.BEE_GRENADE_HONEY_SLOWNESS_AREA_ENABLED.get()) {
                createSlownessArea();
            }

            // 释放蜜蜂
            spawnBees();

            // 播放蜜蜂嗡嗡声
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BEE_LOOP_AGGRESSIVE, SoundSource.NEUTRAL, 1.0F, 1.0F);

            // 创建蜂蜜粒子效果
            ServerLevel serverLevel = (ServerLevel) this.level();
            for (int i = 0; i < 40; i++) {
                double offsetX = this.random.nextGaussian() * 0.2;
                double offsetY = this.random.nextGaussian() * 0.2;
                double offsetZ = this.random.nextGaussian() * 0.2;
                serverLevel.sendParticles(
                        ParticleTypes.FALLING_HONEY,
                        this.getX() + offsetX,
                        this.getY() + 0.5 + offsetY,
                        this.getZ() + offsetZ,
                        1, 0, 0, 0, 0
                );
            }

            // 粒子效果和声音
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    /**
     * 创建减速效果区域
     */
    private void createSlownessArea() {
        // 从配置中获取半径和持续时间
        float radius = ModConfigManager.BEE_GRENADE_HONEY_AREA_RADIUS.get().floatValue();
        int durationInSeconds = ModConfigManager.BEE_GRENADE_HONEY_AREA_DURATION.get();
        int durationInTicks = durationInSeconds * 20; // 转换为游戏刻

        // 创建更有效的区域效果云
        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        // 设置半径
        cloud.setRadius(radius);
        // 设置持续时间
        cloud.setDuration(durationInTicks);
        // 设置较慢的收缩率
        cloud.setRadiusPerTick(-radius / (float) durationInTicks); // 根据持续时间调整收缩率
        // 没有等待时间，立即生效
        cloud.setWaitTime(0);
        // 设置使用时的半径变化
        cloud.setRadiusOnUse(-0.05F);
        // 使用时不减少持续时间
        cloud.setDurationOnUse(0);
        // 设置粒子效果
        cloud.setParticle(ParticleTypes.FALLING_HONEY);
        // 设置所有者为手雷投掷者，防止自己被减速
        if (this.thrower != null) {
            cloud.setOwner(this.thrower);
        }
        // 添加较强的减速效果
        cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));

        // 立即影响区域内的实体
        double searchRadius = radius; // 使用配置的半径
        List<LivingEntity> entities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                        this.getX() - searchRadius, this.getY() - searchRadius, this.getZ() - searchRadius,
                        this.getX() + searchRadius, this.getY() + searchRadius, this.getZ() + searchRadius
                ),
                entity -> !(entity instanceof Bee) && entity.isAlive()
        );

        // 直接给区域内的实体添加减速效果
        for (LivingEntity entity : entities) {
            // 如果是投掷者，不给减速效果
            if (entity.equals(this.thrower)) continue;

            // 对所有其他生物添加减速效果
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2));
            LOGGER.debug("直接对实体 {} 应用减速效果", entity.getName().getString());
        }

        // 添加到世界
        this.level().addFreshEntity(cloud);
        LOGGER.debug("在坐标 ({}, {}, {}) 创建了蜂蜜减速区域，半径: {}，持续时间: {}秒",
                this.getX(), this.getY(), this.getZ(), radius, durationInSeconds);
    }

    /**
     * 生成愤怒的蜜蜂
     */
    private void spawnBees() {
        int beeCount = ModConfigManager.BEE_GRENADE_BEE_COUNT.get();
        boolean playerFriendly = ModConfigManager.BEE_GRENADE_PLAYER_FRIENDLY.get();

        // 为这次爆炸创建一个唯一ID
        UUID explosionId = UUID.randomUUID();

        // 收集区域内的所有潜在目标
        collectPotentialTargets(explosionId);

        // 存储投掷者信息和玩家友好设置
        if (this.thrower != null) {
            EXPLOSION_THROWER.put(explosionId, UUID.fromString(this.thrower.getStringUUID()));
        }
        EXPLOSION_PLAYER_FRIENDLY.put(explosionId, playerFriendly);

        for (int i = 0; i < beeCount; i++) {
            Bee bee = new Bee(EntityType.BEE, this.level());
            bee.setPos(this.getX(), this.getY(), this.getZ());

            // 设置最大的愤怒持续时间
            bee.setRemainingPersistentAngerTime(Integer.MAX_VALUE);

            // 启动愤怒计时器
            bee.startPersistentAngerTimer();

            // 确保蜜蜂不会因愤怒过度而死亡
            bee.setInvulnerable(true);
            bee.setCustomName(null);
            bee.setCustomNameVisible(false);
            bee.setAge(-24000); // 设置年龄防止自然消失

            // 添加标签标识这是蜜蜂手雷产生的蜜蜂
            // 用于Mixin识别并修改行为
            if (playerFriendly && this.thrower != null) {
                // 标记投掷者为友好实体
                bee.addTag("PlayerFriendly:" + this.thrower.getStringUUID());
            } else {
                // 即使不是对玩家友好，也添加标签以便Mixin识别
                bee.addTag("PlayerFriendly:none");
            }

            // 添加爆炸ID标签
            bee.addTag("ExplosionID:" + explosionId);

            // 设置蜜蜂为攻击状态
            bee.setAggressive(true);

            // 添加到世界
            this.level().addFreshEntity(bee);

            // 关联蜜蜂与爆炸ID
            BEE_TO_EXPLOSION.put(bee, explosionId);

            // 寻找并设置初始攻击目标
            findAndSetBeeTargets(bee, playerFriendly);
        }

        LOGGER.debug("在坐标 ({}, {}, {}) 生成了 {} 只愤怒的蜜蜂", this.getX(), this.getY(), this.getZ(), beeCount);

        // 每5分钟清理一次未使用的目标列表
        if (Math.random() < 0.01) {
            cleanupOldTargets();
        }
    }

    /**
     * 收集区域内的所有潜在目标
     *
     * @param explosionId 爆炸ID
     */
    private void collectPotentialTargets(UUID explosionId) {
        if (!(this.level() instanceof ServerLevel)) return;

        // 设置搜索范围
        double searchRadius = 32.0D; // 大范围搜索潜在目标
        AABB searchArea = new AABB(
                this.getX() - searchRadius, this.getY() - searchRadius, this.getZ() - searchRadius,
                this.getX() + searchRadius, this.getY() + searchRadius, this.getZ() + searchRadius
        );

        // 收集区域内的所有生物实体
        List<LivingEntity> potentialTargets = this.level().getEntitiesOfClass(
                LivingEntity.class, searchArea, entity ->
                        !(entity instanceof Bee) && // 不包括蜜蜂
                                entity.isAlive() // 确保实体存活
        );

        LOGGER.debug("收集到 {} 个潜在目标", potentialTargets.size());

        // 存储潜在目标
        BEE_TARGETS.put(explosionId, new ArrayList<>(potentialTargets));
    }

    /**
     * 寻找并设置蜜蜂的攻击目标
     *
     * @param bee            蜜蜂实体
     * @param playerFriendly 是否对其他玩家友好
     */
    private void findAndSetBeeTargets(Bee bee, boolean playerFriendly) {
        if (!(this.level() instanceof ServerLevel)) return;

        // 获取蜜蜂关联的爆炸ID
        UUID explosionId = BEE_TO_EXPLOSION.get(bee);
        if (explosionId == null) return;

        // 获取爆炸关联的目标列表
        List<LivingEntity> targets = BEE_TARGETS.get(explosionId);
        if (targets == null || targets.isEmpty()) return;

        // 过滤可能的目标
        List<LivingEntity> validTargets = new ArrayList<>();
        for (LivingEntity entity : targets) {
            if (!entity.isAlive()) continue;

            // 不攻击其他蜜蜂
            if (entity instanceof Bee) continue;

            // 处理玩家目标逻辑
            if (entity instanceof Player) {
                // 如果配置为对玩家友好，则不攻击任何玩家
                if (playerFriendly) continue;

                // 无论配置如何，都不攻击投掷者
                if (entity.equals(this.thrower)) continue;
            }

            // 确保目标可见
            if (bee.getSensing().hasLineOfSight(entity)) {
                validTargets.add(entity);
            }
        }

        // 选择最近的目标
        if (!validTargets.isEmpty()) {
            LivingEntity closestTarget = null;
            double closestDistSq = Double.MAX_VALUE;

            for (LivingEntity entity : validTargets) {
                double distSq = entity.distanceToSqr(bee);
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closestTarget = entity;
                }
            }

            if (closestTarget != null) {
                bee.setTarget(closestTarget);
                bee.setRemainingPersistentAngerTime(Integer.MAX_VALUE); // 保持持续愤怒
                LOGGER.debug("蜜蜂选择了 {} 作为攻击目标", closestTarget.getName().getString());
                return;
            }
        }

        // 如果没有找到目标，蜜蜂将在区域内徘徊，等待目标出现
        LOGGER.debug("蜜蜂没有找到攻击目标，将在区域内徘徊");
    }

    /**
     * 实体与方块碰撞
     */
    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        // 直接触发onHit方法
    }

    /**
     * 实体与实体碰撞
     */
    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);

        // 对实体也造成减速效果
        if (!this.level().isClientSide) {
            Entity entity = hitResult.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                // 如果是投掷者，不给减速效果
                if (entity.equals(this.thrower)) {
                    return;
                }

                // 跳过蜜蜂
                if (entity instanceof Bee) {
                    return;
                }

                // 应用更强的减速效果
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 3));
                LOGGER.debug("对碰撞实体 {} 应用强力减速效果", livingEntity.getName().getString());

                // 应用额外的眩晕效果
                livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
            }
        }
    }
} 