package me.tuanzi.curiosities.effect;

import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 富有效果
 * 使附近的村民跟随玩家，并在村民头上显示爱心粒子效果
 */
public class RichEffect extends MobEffect {

    /**
     * 构造函数
     */
    public RichEffect() {
        // 使用中性效果类别 (NEUTRAL)
        super(MobEffectCategory.NEUTRAL, 0x27AE60); // 绿宝石绿色
    }

    /**
     * 每个游戏刻应用效果
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 仅对玩家生效
        if (!(entity instanceof Player player)) {
            return;
        }

        Level level = player.level();
        boolean isClientSide = level.isClientSide();

        // 客户端侧不处理村民AI逻辑，只处理服务端
        if (isClientSide) {
            return;
        }

        // 检查是否启用富有效果
        if (!ModConfigManager.RICH_EFFECT_ENABLED.get()) {
            return;
        }

        // 计算作用范围 (基础范围 * (效果等级 + 1))
        int range = ModConfigManager.RICH_EFFECT_RANGE_PER_LEVEL.get() * (amplifier + 1);

        // 寻找范围内的村民
        AABB searchBox = player.getBoundingBox().inflate(range, range, range);
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, searchBox);

        // 对每个村民应用效果
        for (Villager villager : villagers) {
            // 让村民跟随玩家
            makeVillagerFollowPlayer(villager, player);

            // 生成爱心粒子效果 - 使用服务端方法发送粒子效果到客户端
            if (level instanceof ServerLevel serverLevel) {
                showHeartParticles(serverLevel, villager);
            }
        }
    }

    /**
     * 让村民跟随玩家
     */
    private void makeVillagerFollowPlayer(Villager villager, Player player) {
        // 确保村民的寻路导航系统可用
        PathNavigation navigation = villager.getNavigation();

        // 无论村民当前是否有路径，都强制跟随玩家
        // 停止当前的AI任务并清除现有路径
        navigation.stop();

        // 设置村民以较快速度移动到玩家位置
        double speed = 0.6D; // 提高跟随速度

        // 确保每次都尝试移动，更强烈地跟随
        navigation.moveTo(player, speed);

        // 如果村民离玩家很近，让他们看向玩家（表示关注）
        double distanceToPlayer = villager.distanceToSqr(player);
        // 让村民转向面对玩家
        villager.getLookControl().setLookAt(player, 30.0F, 30.0F);
    }

    /**
     * 显示爱心粒子效果 - 使用服务端方法发送粒子到客户端
     */
    private void showHeartParticles(ServerLevel level, Villager villager) {
        // 在村民头顶生成爱心粒子（使用服务端方法sendParticles）
        for (int i = 0; i < 1; i++) {
            level.sendParticles(
                    ParticleTypes.HEART,
                    villager.getX() + (Math.random() - 0.5) * 0.3,
                    villager.getY() + villager.getBbHeight() + 0.5D,
                    villager.getZ() + (Math.random() - 0.5) * 0.3,
                    3, // 粒子数量
                    0, 0.05, 0, // 速度
                    0 // 额外数据
            );
        }

    }

    /**
     * 检查效果是否需要每刻更新
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每10刻（0.5秒）应用一次效果，更频繁地更新村民跟随状态
        return duration % 10 == 0;
    }
} 