package me.tuanzi.curiosities.effect;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

/**
 * 颠颠倒倒效果
 * 使玩家控制方向相反，增加游戏挑战性
 */
public class DizzyEffect extends MobEffect {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     */
    public DizzyEffect() {
        // 使用负面效果类别和橙色
        super(MobEffectCategory.HARMFUL, 0xFF9900);
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
        // 只对玩家生效
        if (entity instanceof Player player && ModConfigManager.DIZZY_EFFECT_ENABLED.get()) {
            // 颠倒控制方向的逻辑会在InputEvent中处理
            // 这里主要用于视觉效果或额外处理

            // 根据效果等级增加难度
            if (player.tickCount % 20 == 0) { // 每秒记录一次
                LOGGER.debug("玩家 {} 受到颠颠倒倒效果影响，等级: {}",
                        player.getName().getString(), amplifier + 1);
            }
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
        // 始终应用
        return true;
    }
} 