package me.tuanzi.curiosities.effect;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

/**
 * 不死效果
 * 当受到致命伤害时，触发不死图腾的效果，并移除本效果
 */
public class UndyingEffect extends MobEffect {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     */
    public UndyingEffect() {
        // 使用中性效果类别，设置效果颜色为金色（类似不死图腾）
        super(MobEffectCategory.BENEFICIAL, 0xFFD700);
        LOGGER.debug("不死效果已创建");
    }

    /**
     * 效果应用时的处理
     * 注意：真正的效果是在LivingDeathEvent中处理的
     *
     * @param entity    被应用效果的实体
     * @param amplifier 效果等级
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 检查配置是否启用该效果
        if (!ModConfigManager.UNDYING_EFFECT_ENABLED.get()) {
        }

        // 不需要在这里实现具体逻辑，主要功能在事件处理中实现
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