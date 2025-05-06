package me.tuanzi.curiosities.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 罪恶效果
 * 当玩家拥有此效果时，村民交易价格会提高
 * 这个效果和村庄英雄效果相反，村庄英雄减少价格，罪恶效果增加价格
 */
public class GuiltEffect extends MobEffect {
    /**
     * 价格提高的基础比例
     */
    private static final float BASE_PRICE_INCREASE = 0.75f; // 基础增加75%

    /**
     * 每级效果额外增加的比例
     */
    private static final float LEVEL_MULTIPLIER = 0.75f; // 每级额外增加75%

    /**
     * 构造函数
     */
    public GuiltEffect() {
        // 使用负面效果类别和红色效果颜色
        super(MobEffectCategory.HARMFUL, 0xAA0000);
    }

    /**
     * 获取价格增加的系数
     * <p>
     * 计算方式：基础价格 * (1 + BASE_PRICE_INCREASE + (amplifier * LEVEL_MULTIPLIER))
     * <p>
     * 例如：
     * - 等级0（I）：增加75%，系数为1.75
     * - 等级1（II）：增加150%，系数为2.5
     * - 等级2（III）：增加225%，系数为3.25
     * - 等级3（IV）：增加300%，系数为4.0
     * - 等级4（V）：增加375%，系数为4.75
     *
     * @param amplifier 效果等级（从0开始）
     * @return 价格增加的系数
     */
    public static float getPriceIncreaseFactor(int amplifier) {
        return 1.0f + BASE_PRICE_INCREASE + (amplifier * LEVEL_MULTIPLIER);
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
        // 罪恶效果不需要每刻应用效果，会在村民交易时处理
        super.applyEffectTick(entity, amplifier);
    }

    /**
     * 罪恶效果是否应该应用每刻效果
     *
     * @return 是否每刻应用
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 不需要每刻应用效果
        return false;
    }
} 