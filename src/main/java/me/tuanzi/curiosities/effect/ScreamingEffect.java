package me.tuanzi.curiosities.effect;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.List;

/**
 * 尖叫效果
 * 每5秒发出恶魂尖叫声，吸引周围指定范围内的敌对生物
 */
public class ScreamingEffect extends MobEffect {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 效果触发间隔(每100刻触发一次，即5秒)
    private static final int EFFECT_INTERVAL = 100;

    /**
     * 构造函数
     */
    public ScreamingEffect() {
        // 使用负面效果类别和紫色
        super(MobEffectCategory.HARMFUL, 0xAA00AA);
    }

    /**
     * 应用效果
     * 在游戏每个刻对实体应用效果
     *
     * @param entity    实体
     * @param amplifier 效果等级
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 只在服务端且实体是玩家时处理
        if (!(entity.level() instanceof ServerLevel level) || !(entity instanceof Player player)) {
            return; 
        }

        // 检查是否为触发时间点(每5秒)
        if (entity.tickCount % EFFECT_INTERVAL == 0) {
            // 播放恶魂尖叫声
            level.playSound(null, entity.blockPosition(), SoundEvents.GHAST_HURT, SoundSource.PLAYERS, 1.0F, 0.5F);
            LOGGER.debug("玩家 {} 触发尖叫效果，播放恶魂尖叫声", player.getName().getString());

            // 获取范围配置
            int range = ModConfigManager.SCREAMING_EFFECT_RANGE.get();

            // 计算搜索范围
            BlockPos pos = entity.blockPosition();
            AABB searchArea = new AABB(
                    pos.getX() - range, pos.getY() - range, pos.getZ() - range,
                    pos.getX() + range, pos.getY() + range, pos.getZ() + range
            );

            // 寻找周围的敌对生物
            List<Mob> enemies = level.getEntitiesOfClass(Mob.class, searchArea,
                    mob -> mob instanceof Enemy && mob.isAlive());

            // 吸引敌对生物
            for (Mob enemy : enemies) {
                if (enemy.getTarget() == null || enemy.getRandom().nextInt(3) == 0) {
                    enemy.setTarget(player);
                    LOGGER.debug("尖叫效果引起 {} 对玩家 {} 的仇恨", enemy.getName().getString(), player.getName().getString());
                }
            }

            LOGGER.info("玩家 {} 的尖叫效果吸引了 {} 个敌对生物", player.getName().getString(), enemies.size());
        }
    }

    /**
     * 效果是否应该每刻应用
     *
     * @param duration  持续时间
     * @param amplifier 效果等级
     * @return 是否每刻应用
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 始终应用，但在应用方法内检查触发条件
        return true;
    }
} 