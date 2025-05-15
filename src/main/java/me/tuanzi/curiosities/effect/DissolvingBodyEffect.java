package me.tuanzi.curiosities.effect;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

/**
 * 瓦解之躯效果
 * 每等级减少2游戏刻（0.1秒）受击后伤害免疫时间
 */
public class DissolvingBodyEffect extends MobEffect {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     */
    public DissolvingBodyEffect() {
        // 使用负面效果类别，设置效果颜色为暗灰色（表示身体瓦解）
        super(MobEffectCategory.HARMFUL, 0x555555);
        LOGGER.debug("瓦解之躯效果已创建");
    }

    /**
     * 效果应用时的处理
     *
     * @param entity    被应用效果的实体
     * @param amplifier 效果等级
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 检查配置是否启用该效果
        if (!ModConfigManager.DISSOLVING_BODY_EFFECT_ENABLED.get()) {
        }

        // 注意：真正的伤害免疫时间减少效果是在LivingEntity的hurt方法中处理的
        // 这里只是一个占位，记录效果应用情况

        // 记录效果应用，每20ticks记录一次
//        if (entity.level().getGameTime() % 20 == 0) {
//            LOGGER.debug("瓦解之躯效果作用于 {}, 等级: {}", entity.getName().getString(), amplifier);
//        }
    }

    /**
     * 判断效果是否需要每tick更新
     *
     * @return 始终返回true，以便每tick更新
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
} 