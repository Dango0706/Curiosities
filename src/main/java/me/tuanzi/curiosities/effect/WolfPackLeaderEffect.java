package me.tuanzi.curiosities.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 狼群领袖效果
 * 使半径30格内的狼/狗会协助攻击持有者锁定的目标
 * 同时会被熊猫敌视
 */
public class WolfPackLeaderEffect extends MobEffect {
    // 效果影响半径
    private static final float EFFECT_RADIUS = 30.0F;

    /**
     * 构造函数
     */
    public WolfPackLeaderEffect() {
        // 使用中性效果类别和棕色
        super(MobEffectCategory.NEUTRAL, 0xA06540);
    }

    /**
     * 效果应用时的刻度处理
     */
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return; // 客户端不处理
        }

        Level level = entity.level();

        // 获取实体周围30格内的所有狼
        AABB boundingBox = entity.getBoundingBox().inflate(EFFECT_RADIUS);
        List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, boundingBox);

        // 如果实体有目标，让附近所有狼也攻击该目标
        LivingEntity target = entity.getLastHurtMob();
        if (target != null && !target.isDeadOrDying()) {
            for (Wolf wolf : wolves) {
                // 对于驯服的狼，只影响不是坐下状态的
                // 对于野生狼，直接设置目标
                if ((wolf.isTame() && !wolf.isOrderedToSit()) || !wolf.isTame()) {
                    // 确保狼不会攻击自己的主人
                    if (wolf.isTame() && wolf.getOwner() == target) {
                        continue;
                    }
                    wolf.setTarget(target);
                }
            }
        }

        // 获取附近的熊猫并让它们敌视玩家
        List<Panda> pandas = level.getEntitiesOfClass(Panda.class, boundingBox);
        for (Panda panda : pandas) {
            if (!panda.isBaby() && !panda.isAggressive()) {
                // 让熊猫注意并可能攻击玩家
                panda.lookAt(entity, 30.0F, 30.0F);
                if (panda.distanceTo(entity) < 10.0 && panda.getRandom().nextInt(20) == 0) {
                    panda.setTarget(entity);
                }
            }
        }
    }

    /**
     * 检查效果是否需要每刻都应用
     */
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每10刻执行一次效果检查（约0.5秒）
        return duration % 10 == 0;
    }
} 