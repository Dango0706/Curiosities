package me.tuanzi.curiosities.items.void_sword;

import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 虚空吞噬之剑
 * 击杀生物时收集生物生命值上限的一部分作为虚空能量
 * 积累足够能量后可释放黑洞效果
 */
public class VoidSwordItem extends SwordItem {
    private static final String VOID_ENERGY_TAG = "VoidEnergy";

    // 用于存储当前活跃的黑洞信息
    private static final Map<UUID, BlackHoleData> activeBlackHoles = new HashMap<>();

    public VoidSwordItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        // 设置物品名称为自定义紫色 (#AB85C0)
        // 将十六进制颜色转换为RGB整数值
        int r = 0xAB; // 171
        int g = 0x85; // 133
        int b = 0xC0; // 192
        int colorInt = (r << 16) | (g << 8) | b;

        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(style -> style.withColor(colorInt));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        // 检查目标是否已死亡
        if (target.isDeadOrDying() && attacker instanceof Player player) {
            // 计算获得的虚空能量，为目标最大生命值的百分比
            float maxHealth = target.getMaxHealth();
            int energyGain = Math.round(maxHealth * (ModConfigManager.VOID_SWORD_ENERGY_PERCENT.get().floatValue() / 100f));

            // 存储能量到NBT
            CompoundTag tag = stack.getOrCreateTag();
            int currentEnergy = tag.getInt(VOID_ENERGY_TAG);
            int maxEnergy = ModConfigManager.VOID_SWORD_MAX_ENERGY.get();

            // 确保不超过最大能量
            int newEnergy = Math.min(currentEnergy + energyGain, maxEnergy);
            tag.putInt(VOID_ENERGY_TAG, newEnergy);

            // 通知玩家获得了能量
            if (newEnergy > currentEnergy) {
                player.displayClientMessage(Component.translatable("item.curiosities.void_sword.energy_gained", energyGain, newEnergy, maxEnergy)
                        .withStyle(ChatFormatting.DARK_PURPLE), true);
            }
        }

        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 使用原版物品冷却系统检查冷却
        if (player.getCooldowns().isOnCooldown(this)) {
            // 计算剩余冷却时间（刻）
            int remainingCooldownTicks = player.getCooldowns().getCooldownPercent(this, 0) > 0 ?
                    Math.round(player.getCooldowns().getCooldownPercent(this, 0) * ModConfigManager.VOID_SWORD_COOLDOWN.get() * 20) : 0;

            // 通知玩家冷却中
            player.displayClientMessage(Component.translatable("item.curiosities.void_sword.cooldown", remainingCooldownTicks)
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        // 获取当前能量
        CompoundTag tag = stack.getOrCreateTag();
        int currentEnergy = tag.getInt(VOID_ENERGY_TAG);
        int requiredEnergy = 100; // 需要至少100点能量释放黑洞

        if (currentEnergy >= requiredEnergy) {
            // 获取最大施法距离（0表示无限制）
            int maxCastDistance = ModConfigManager.VOID_SWORD_MAX_CAST_DISTANCE.get();

            // 检查玩家指向的方块位置
            HitResult hitResult = player.pick(maxCastDistance == 0 ? 100 : maxCastDistance, 0, false);

            // 确定黑洞位置
            Vec3 blackHolePos;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // 如果命中方块，在方块上生成黑洞
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                BlockPos blockPos = blockHitResult.getBlockPos();
                blackHolePos = new Vec3(
                        blockPos.getX() + 0.5,
                        blockPos.getY() + 1.0, // 放在方块上方1格位置
                        blockPos.getZ() + 0.5
                );
            } else {
                // 如果超出最大射程或未命中方块，在最大射程的位置生成黑洞
                if (maxCastDistance == 0) {
                    // 如果配置为无限距离但未命中方块，提示玩家
                    player.displayClientMessage(Component.translatable("item.curiosities.void_sword.no_target_block")
                            .withStyle(ChatFormatting.RED), true);
                    return InteractionResultHolder.fail(stack);
                }

                // 在玩家视线方向上最大射程处创建黑洞
                Vec3 playerLook = player.getLookAngle();
                Vec3 playerPos = player.getEyePosition();
                blackHolePos = playerPos.add(
                        playerLook.x * maxCastDistance,
                        playerLook.y * maxCastDistance,
                        playerLook.z * maxCastDistance
                );

                // 通知玩家黑洞在最大射程处生成
                player.displayClientMessage(Component.translatable("item.curiosities.void_sword.max_distance_cast")
                        .withStyle(ChatFormatting.GOLD), true);
            }

            // 消耗能量释放黑洞
            if (!level.isClientSide) {
                createBlackHole((ServerLevel) level, player, blackHolePos);
                tag.putInt(VOID_ENERGY_TAG, currentEnergy - requiredEnergy);

                // 设置物品冷却
                int cooldownTicks = ModConfigManager.VOID_SWORD_COOLDOWN.get() * 20; // 秒转换为刻
                player.getCooldowns().addCooldown(this, cooldownTicks);
            }

            // 播放音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 0.5F);

            return InteractionResultHolder.success(stack);
        } else {
            // 能量不足
            player.displayClientMessage(Component.translatable("item.curiosities.void_sword.not_enough_energy",
                    currentEnergy, requiredEnergy).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }
    }

    /**
     * 创建黑洞效果
     *
     * @param level        服务器世界
     * @param player       玩家
     * @param blackHolePos 黑洞位置
     */
    private void createBlackHole(ServerLevel level, Player player, Vec3 blackHolePos) {
        // 获取黑洞参数
        double range = ModConfigManager.VOID_SWORD_BLACK_HOLE_RANGE.get();
        float damage = ModConfigManager.VOID_SWORD_BLACK_HOLE_DAMAGE.get().floatValue();
        if (damage <= 0) {
            // 如果配置为0，则使用玩家的攻击力
            damage = (float) player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        }

        // 获取黑洞持续时间和伤害间隔（从刻转换为秒）
        int durationTicks = ModConfigManager.VOID_SWORD_BLACK_HOLE_DURATION.get() * 20;
        int damageIntervalTicks = ModConfigManager.VOID_SWORD_BLACK_HOLE_DAMAGE_INTERVAL.get() * 20;

        // 创建初始爆炸粒子效果
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                blackHolePos.x, blackHolePos.y, blackHolePos.z,
                3, 0.3, 0.3, 0.3, 0.1);

        // 创建持久的黑洞效果
        UUID blackHoleId = UUID.randomUUID();
        BlackHoleData blackHole = new BlackHoleData(
                blackHoleId, level, player, blackHolePos, range, damage, durationTicks, damageIntervalTicks
        );

        // 存储黑洞数据
        activeBlackHoles.put(blackHoleId, blackHole);

        // 播放黑洞创建音效
        level.playSound(null, blackHolePos.x, blackHolePos.y, blackHolePos.z,
                SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.0F, 0.3F);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // 在服务器上更新所有活跃的黑洞
        if (!level.isClientSide && level instanceof ServerLevel) {
            updateActiveBlackHoles((ServerLevel) level);
        }
    }

    /**
     * 更新所有活跃的黑洞
     */
    private void updateActiveBlackHoles(ServerLevel level) {
        // 使用迭代器以便安全移除
        activeBlackHoles.entrySet().removeIf(entry -> {
            BlackHoleData blackHole = entry.getValue();
            if (blackHole.level.equals(level)) {
                return !blackHole.update();
            }
            return false;
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        // 添加描述文本
        tooltipComponents.add(Component.translatable("item.curiosities.void_sword.desc.1")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltipComponents.add(Component.translatable("item.curiosities.void_sword.desc.2")
                .withStyle(ChatFormatting.DARK_PURPLE));

        // 显示当前能量
        CompoundTag tag = stack.getOrCreateTag();
        int currentEnergy = tag.getInt(VOID_ENERGY_TAG);
        int maxEnergy = ModConfigManager.VOID_SWORD_MAX_ENERGY.get();

        tooltipComponents.add(Component.translatable("item.curiosities.void_sword.energy",
                currentEnergy, maxEnergy).withStyle(ChatFormatting.GOLD));

        // 如果玩家持有武器，显示冷却时间
        if (level != null && level.players() != null) {
            for (Player player : level.players()) {
                if (player.getMainHandItem() == stack || player.getOffhandItem() == stack) {
                    if (player.getCooldowns().isOnCooldown(this)) {
                        // 计算剩余冷却时间（秒）
                        float cooldownPercent = player.getCooldowns().getCooldownPercent(this, 0);
                        int remainingCooldownSeconds = Math.round(cooldownPercent * ModConfigManager.VOID_SWORD_COOLDOWN.get());

                        tooltipComponents.add(Component.translatable("item.curiosities.void_sword.cooldown.tooltip",
                                remainingCooldownSeconds).withStyle(ChatFormatting.RED));
                        break;
                    }
                }
            }
        }
    }

    /**
     * 黑洞效果数据类
     * 存储黑洞的所有相关信息
     */
    private static class BlackHoleData {
        private final UUID id;
        private final ServerLevel level;
        private final Player owner;
        private final Vec3 position;
        private final double range;
        private final float damage;
        private final int damageIntervalTicks;
        private int remainingTicks;
        private int ticksUntilNextDamage;
        private int animationTick;

        public BlackHoleData(UUID id, ServerLevel level, Player owner, Vec3 position,
                             double range, float damage, int durationTicks, int damageIntervalTicks) {
            this.id = id;
            this.level = level;
            this.owner = owner;
            this.position = position;
            this.range = range;
            this.damage = damage;
            this.remainingTicks = durationTicks;
            this.damageIntervalTicks = damageIntervalTicks;
            this.ticksUntilNextDamage = 0; // 立即造成第一次伤害
            this.animationTick = 0;
        }

        /**
         * 更新黑洞效果
         *
         * @return 黑洞是否还存在
         */
        public boolean update() {
            remainingTicks--;
            animationTick++;

            if (remainingTicks <= 0) {
                // 黑洞消失，创建消失粒子效果
                createDisappearanceEffect();
                return false;
            }

            // 创建持续粒子效果
            createContinuousEffect();

            // 每刻对实体施加吸引力
            applyPull();

            // 计算是否到达伤害间隔
            ticksUntilNextDamage--;
            if (ticksUntilNextDamage <= 0) {
                applyDamage();
                ticksUntilNextDamage = damageIntervalTicks;
            }

            return true;
        }

        /**
         * 创建持续粒子效果
         */
        private void createContinuousEffect() {
            // 黑洞核心
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,
                    position.x, position.y, position.z,
                    10, 0.3, 0.3, 0.3, 0.02);

            // 黑洞脉冲（根据动画tick变化）
            double pulseSize = 0.5 + 0.2 * Math.sin(animationTick * 0.1);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                    position.x, position.y, position.z,
                    5, pulseSize, pulseSize, pulseSize, 0.01);

            // 黑洞外围旋转效果
            double angle = animationTick * 0.1;
            double radius = range / 4;
            for (int i = 0; i < 3; i++) {
                double offsetAngle = angle + (Math.PI * 2 / 3) * i;
                double x = position.x + Math.cos(offsetAngle) * radius;
                double z = position.z + Math.sin(offsetAngle) * radius;
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
                        x, position.y, z,
                        2, 0.1, 0.1, 0.1, 0.01);
            }

            // 随机能量散射粒子
            if (animationTick % 5 == 0) {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,
                        position.x, position.y, position.z,
                        15, range / 5, range / 5, range / 5, 0.1);
            }

            // 如果是5的倍数tick，添加额外效果
            if (animationTick % 20 == 0) {
                // 脉冲环效果
                double ringRadius = range / 3;
                int ringParticles = 16;
                for (int i = 0; i < ringParticles; i++) {
                    double ringAngle = (Math.PI * 2 / ringParticles) * i;
                    double x = position.x + Math.cos(ringAngle) * ringRadius;
                    double z = position.z + Math.sin(ringAngle) * ringRadius;
                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL,
                            x, position.y, z,
                            1, 0, 0, 0, 0.05);
                }
            }

            // 新增：从黑洞边缘向中心的粒子流效果（每10tick触发一次）
            if (animationTick % 10 == 0) {
                // 创建从黑洞吸附范围边缘向中心的粒子流
                int streamLines = 8; // 8条粒子流线
                for (int line = 0; line < streamLines; line++) {
                    double lineAngle = (Math.PI * 2 / streamLines) * line + (animationTick * 0.05); // 旋转角度

                    // 从黑洞范围边缘向中心创建粒子
                    int particlesPerLine = 6; // 每条线上的粒子数
                    for (int p = 0; p < particlesPerLine; p++) {
                        // 计算粒子位置（从边缘向中心）
                        double distanceFromCenter = range * (1.0 - ((double) p / particlesPerLine));
                        double x = position.x + Math.cos(lineAngle) * distanceFromCenter;
                        double y = position.y + (Math.sin(animationTick * 0.05) * 0.3); // 轻微的上下波动
                        double z = position.z + Math.sin(lineAngle) * distanceFromCenter;

                        // 交替使用不同的粒子类型
                        if (p % 2 == 0) {
                            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                                    x, y, z, 1, 0, 0, 0, 0.02);
                        } else {
                            level.sendParticles(net.minecraft.core.particles.ParticleTypes.WITCH,
                                    x, y, z, 1, 0, 0, 0, 0.001);
                        }
                    }
                }
            }

            // 新增：黑洞核心的强化粒子效果（每15tick触发一次）
            if (animationTick % 15 == 0) {

                // 黑洞核心螺旋效果
                double spiralRadius = 0.8;
                int spiralPoints = 12;
                for (int i = 0; i < spiralPoints; i++) {
                    double spiralAngle = (Math.PI * 2 / spiralPoints) * i + animationTick * 0.2;
                    double heightOffset = (i % 3 - 1) * 0.2; // 创建上中下三层效果
                    double x = position.x + Math.cos(spiralAngle) * spiralRadius;
                    double y = position.y + heightOffset;
                    double z = position.z + Math.sin(spiralAngle) * spiralRadius;

                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            x, y, z, 1, 0, 0, 0, 0.01);
                }
            }

            // 新增：黑洞动态边界效果（每7tick触发一次）
            if (animationTick % 7 == 0) {
                double boundaryRadius = range * 0.8;
                int boundaryPoints = 6;
                // 创建一个不规则的边界效果
                for (int i = 0; i < boundaryPoints; i++) {
                    double boundaryAngle = (Math.PI * 2 / boundaryPoints) * i + animationTick * 0.03;
                    // 添加一些随机性使边界看起来不规则
                    double radiusVariation = boundaryRadius * (0.9 + Math.random() * 0.2);
                    double x = position.x + Math.cos(boundaryAngle) * radiusVariation;
                    double y = position.y + (Math.random() * 2 - 1); // 上下1格范围内随机
                    double z = position.z + Math.sin(boundaryAngle) * radiusVariation;

                    // 使用暗色调粒子营造黑洞边界感
                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                            x, y, z, 1, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }

        /**
         * 创建消失效果
         */
        private void createDisappearanceEffect() {
            // 爆炸粒子
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
                    position.x, position.y, position.z,
                    1, 0, 0, 0, 0);

            // 消散粒子
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.REVERSE_PORTAL,
                    position.x, position.y, position.z,
                    50, 1.0, 1.0, 1.0, 0.5);

            // 最终光芒
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                    position.x, position.y, position.z,
                    30, 1.5, 1.5, 1.5, 0.1);

            // 播放消失音效
            level.playSound(null, position.x, position.y, position.z,
                    SoundEvents.WARDEN_DEATH, SoundSource.PLAYERS, 1.0F, 1.5F);
        }

        /**
         * 对区域内实体施加吸引力
         */
        private void applyPull() {
            // 计算影响区域
            AABB affectArea = new AABB(
                    position.x - range, position.y - range, position.z - range,
                    position.x + range, position.y + range, position.z + range
            );

            // 获取区域内的所有生物
            List<Entity> entities = level.getEntities(owner, affectArea,
                    entity -> entity instanceof LivingEntity && !(entity instanceof Player) && entity.isAlive());

            // 影响所有实体
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    // 计算实体到黑洞中心的矢量
                    Vec3 pullVector = position.subtract(living.position());
                    double distance = pullVector.length();

                    // 拉力与距离成反比，越近拉力越小（避免实体穿过中心点被弹飞）
                    double pullStrength = Math.max(0.1, Math.min(0.25, 1.0 - (distance / (range * 1.2))));
                    Vec3 normalizedPull = pullVector.normalize().scale(pullStrength);

                    // 添加缓慢效果防止实体逃跑
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, false, false));

                    // 向黑洞中心方向拉动实体
                    living.setDeltaMovement(living.getDeltaMovement().add(normalizedPull));

                    // 如果实体非常接近中心点，添加悬浮效果防止它们下落
                    if (distance < 2.0) {
                        living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 0, false, false));
                    }

                    // 为被吸引的实体创建粒子效果
                    if (animationTick % 10 == 0 && owner instanceof ServerPlayer serverPlayer) {
                        // 实体到黑洞的粒子路径
                        Vec3 path = position.subtract(living.position());
                        Vec3 step = path.normalize().scale(0.8);
                        Vec3 currentPos = living.position().add(0, living.getBbHeight() / 2, 0);

                        // 每3个粒子点创建一个粒子
                        int steps = Math.min(6, (int) (path.length() / 0.8));
                        for (int i = 0; i < steps; i++) {
                            currentPos = currentPos.add(step);
                            if (i % 3 == 0) {
                                level.sendParticles(serverPlayer,
                                        net.minecraft.core.particles.ParticleTypes.PORTAL, true,
                                        currentPos.x, currentPos.y, currentPos.z,
                                        1, 0, 0, 0, 0.01);
                            }
                        }
                    }

                    // 新增：被吸引实体的额外粒子效果
                    if (animationTick % 5 == 0) {
                        // 被吸引的实体周围产生能量散逸粒子
                        level.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT,
                                living.getX(), living.getY() + living.getBbHeight() / 2, living.getZ(),
                                3, 0.2, 0.2, 0.2, 0.1);

                        // 连接实体与黑洞的直线粒子（只在距离较近时显示）
                        if (distance < range * 0.6) {
                            Vec3 start = living.position().add(0, living.getBbHeight() / 2, 0);
                            Vec3 direction = position.subtract(start).normalize();

                            // 沿着实体到黑洞的方向创建粒子束
                            int lineParticles = (int) (distance / 0.7); // 距离决定粒子数量
                            for (int i = 0; i < lineParticles; i++) {
                                double progress = (double) i / lineParticles;
                                Vec3 particlePos = start.add(
                                        direction.x * distance * progress,
                                        direction.y * distance * progress,
                                        direction.z * distance * progress
                                );

                                // 根据到黑洞的距离变化粒子类型
                                if (progress < 0.3) {
                                    // 靠近实体的粒子
                                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.WITCH,
                                            particlePos.x, particlePos.y, particlePos.z,
                                            1, 0, 0, 0, 0.01);
                                } else if (progress > 0.7) {
                                    // 靠近黑洞的粒子
                                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL,
                                            particlePos.x, particlePos.y, particlePos.z,
                                            1, 0, 0, 0, 0.01);
                                } else if (i % 2 == 0) { // 中间部分交替使用两种粒子
                                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.DRAGON_BREATH,
                                            particlePos.x, particlePos.y, particlePos.z,
                                            1, 0, 0, 0, 0.01);
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * 对区域内实体造成伤害
         */
        private void applyDamage() {
            // 计算影响区域
            AABB affectArea = new AABB(
                    position.x - range, position.y - range, position.z - range,
                    position.x + range, position.y + range, position.z + range
            );

            // 定义黑洞中心伤害范围（半径2格）
            double damageRadius = 2.0;

            // 获取区域内的所有生物
            List<Entity> entities = level.getEntities(owner, affectArea,
                    entity -> entity instanceof LivingEntity && !(entity instanceof Player) && entity.isAlive());

            // 伤害计数（用于反馈）
            int damagedCount = 0;

            // 造成伤害并创建视觉效果
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    // 计算实体中心位置到黑洞中心的距离
                    Vec3 entityPos = living.position().add(0, living.getBbHeight() / 2, 0);
                    double distance = position.distanceTo(entityPos);

                    // 判断实体是否在黑洞中心伤害范围内
                    if (distance <= damageRadius) {
                        // 造成伤害
                        living.hurt(level.damageSources().playerAttack(owner), damage);
                        damagedCount++;

                        // 创建伤害粒子效果 - 更加集中在实体周围
                        level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT,
                                living.getX(), living.getY() + living.getBbHeight() / 2, living.getZ(),
                                15, 0.2, 0.2, 0.2, 0.3);

                        level.sendParticles(net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                                living.getX(), living.getY() + living.getBbHeight() / 3, living.getZ(),
                                1, 0.0, 0.0, 0.0, 0.0);

                        // 播放伤害音效
                        level.playSound(null, living.getX(), living.getY(), living.getZ(),
                                SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 0.3F, 1.2F);
                    }
                }
            }

            // 只有在实际造成伤害时才播放额外效果
            if (damagedCount > 0) {
                // 伤害音效
                level.playSound(null, position.x, position.y, position.z,
                        SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 0.5F, 1.5F);
            }

            // 无论是否造成伤害，都显示黑洞中心的伤害区域效果
            // 伤害中心区域粒子效果
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME,
                    position.x, position.y, position.z,
                    20, damageRadius / 2, damageRadius / 2, damageRadius / 2, 0.01);

            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIMSON_SPORE,
                    position.x, position.y, position.z,
                    30, damageRadius / 1.5, damageRadius / 1.5, damageRadius / 1.5, 0.01);
        }
    }
} 