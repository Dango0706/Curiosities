package me.tuanzi.curiosities.effect;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

/**
 * 混乱效果
 * 攻击时有一定概率将目标转移为自己，并造成部分伤害
 */
public class ConfusionEffect extends MobEffect {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     */
    public ConfusionEffect() {
        // 使用负面效果类别和紫色
        super(MobEffectCategory.HARMFUL, 0xA020F0);
    }

    /**
     * 计算当前等级下的混乱概率
     *
     * @param amplifier 效果等级
     * @return 混乱概率（0.0-1.0）
     */
    public static float getConfusionChance(int amplifier) {
        if (!ModConfigManager.CONFUSION_EFFECT_ENABLED.get()) {
            return 0.0F;
        }

        float chancePerLevel = ModConfigManager.CONFUSION_CHANCE_PER_LEVEL.get().floatValue();
        float chance = chancePerLevel * (amplifier + 1);

        // 限制最大概率为100%
        return Math.min(chance, 1.0F);
    }

    /**
     * 计算当前等级下造成的伤害百分比
     *
     * @param amplifier 效果等级
     * @return 伤害百分比（0.0-上限或无上限）
     */
    public static float getDamagePercent(int amplifier) {
        if (!ModConfigManager.CONFUSION_EFFECT_ENABLED.get()) {
            return 0.0F;
        }

        float percentPerLevel = ModConfigManager.CONFUSION_DAMAGE_PERCENT_PER_LEVEL.get().floatValue();
        float damagePercent = percentPerLevel * (amplifier + 1);

        // 如果上限为0，则无上限；否则应用上限
        float maxPercent = ModConfigManager.CONFUSION_DAMAGE_PERCENT_MAX.get().floatValue();
        return maxPercent > 0 ? Math.min(damagePercent, maxPercent) : damagePercent;
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
        // 混乱效果的主要逻辑将在事件处理器中实现
        // 这里主要用于视觉效果或日志记录

        if (entity instanceof Player player && entity.tickCount % 60 == 0) {
            // 每3秒记录一次日志（仅调试用）
            LOGGER.debug("实体 {} 受到混乱效果影响，等级: {}",
                    entity.getName().getString(), amplifier + 1);
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
        // 每刻检查，主要用于粒子效果
        return true;
    }
}
