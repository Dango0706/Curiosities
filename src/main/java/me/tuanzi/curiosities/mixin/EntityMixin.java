package me.tuanzi.curiosities.mixin;

import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 实体Mixin类
 * 用于修改实体的invulnerableTime字段
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public int invulnerableTime;

    /**
     * 注入到tick方法中，每tick检查并修改瓦解之躯效果的免疫时间
     *
     * @param ci 回调信息
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;

        // 只对LivingEntity实例进行处理
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        // 检查配置是否启用瓦解之躯效果
        if (!ModConfigManager.DISSOLVING_BODY_EFFECT_ENABLED.get()) {
            return;
        }

        // 检查实体是否有瓦解之躯效果
        if (livingEntity.hasEffect(ModEffects.DISSOLVING_BODY.get())) {
            // 如果实体当前处于免疫时间内
            if (this.invulnerableTime > 0) {
                // 获取效果等级
                MobEffectInstance effect = livingEntity.getEffect(ModEffects.DISSOLVING_BODY.get());
                int effectLevel = 0;
                if (effect != null) {
                    effectLevel = effect.getAmplifier();
                }

                // 每等级减少2游戏刻（0.1秒）伤害免疫时间
                int reductionAmount = (effectLevel + 1) * 2;

                // 减少当前刻的免疫时间
                this.invulnerableTime = Math.max(0, this.invulnerableTime - reductionAmount);
            }
        }
    }
} 