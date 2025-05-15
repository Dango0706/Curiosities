package me.tuanzi.curiosities.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 生物实体Mixin类
 * 用于在受伤时应用瓦解之躯效果的空检查
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /**
     * 注入到hurt方法中，检查瓦解之躯效果是否存在
     * 实际的免疫时间减少逻辑在EntityMixin中处理
     *
     * @param source 伤害来源
     * @param amount 伤害量
     * @param cir    回调信息
     */
    @Inject(method = "hurt", at = @At("RETURN"))
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // 由于我们已经在EntityMixin中处理了免疫时间逻辑，
        // 这里只需确保效果正确应用，不需要进行额外操作
    }
} 