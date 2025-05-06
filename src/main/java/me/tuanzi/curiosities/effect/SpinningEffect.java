package me.tuanzi.curiosities.effect;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

/**
 * 天旋地转效果
 * 使玩家视角随机旋转，等级越高效果越强
 */
public class SpinningEffect extends MobEffect {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     */
    public SpinningEffect() {
        // 使用负面效果类别，设置效果颜色为淡黄色
        super(MobEffectCategory.HARMFUL, 0xFFFF99);
        LOGGER.debug("天旋地转效果已创建");
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
        if (!ModConfigManager.SPINNING_EFFECT_ENABLED.get()) {
            return;
        }

        // 注意：真正的旋转效果是在CameraEvents类中处理的
        // 这里只是一个占位，可能会添加其他效果（如虚弱、恶心等）

        // 记录效果应用，每20ticks记录一次
        if (entity.level().getGameTime() % 20 == 0) {
            LOGGER.debug("天旋地转效果作用于 {}, 等级: {}", entity.getName().getString(), amplifier);
        }
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