package me.tuanzi.curiosities.mixin;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.items.bee_grenade.BeeGrenadeEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 蜜蜂行为修改Mixin
 * 用于修改蜜蜂的攻击行为，使其持续攻击目标
 */
@Mixin(Bee.class)
public abstract class BeeBehaviorMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 在蜜蜂的doHurtTarget方法中注入代码，阻止蜜蜂手雷生成的蜜蜂设置已蜇过状态
     * 原版蜜蜂在攻击后会设置hasStung为true并停止愤怒状态，这导致蜜蜂只攻击一次
     */
    @Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Bee;setHasStung(Z)V"), cancellable = true)
    private void onDoHurtTarget(Entity target, CallbackInfoReturnable<Boolean> cir) {
        Bee bee = (Bee) (Object) this;

        // 检查蜜蜂是否有标签表示它是由蜜蜂手雷生成的
        if (bee.getTags().stream().anyMatch(tag -> tag.startsWith("PlayerFriendly:"))) {
            // 不设置hasStung为true，也不停止愤怒状态，允许蜜蜂继续攻击
            LOGGER.debug("[Mixin]阻止蜜蜂手雷生成的蜜蜂设置已蜇过状态，允许其继续攻击");

            // 保持蜜蜂的愤怒状态
            bee.setRemainingPersistentAngerTime(600); // 重置为30秒

            // 取消原方法后续的设置hasStung为true和stopBeingAngry的调用
            cir.setReturnValue(true);
        }
    }

    /**
     * 在蜜蜂的tick方法中注入代码，检查目标是否存活，如果不存活则获取下一个目标
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Bee bee = (Bee) (Object) this;

        // 检查蜜蜂是否有标签表示它是由蜜蜂手雷生成的
        if (bee.getTags().stream().anyMatch(tag -> tag.startsWith("PlayerFriendly:"))) {
            LivingEntity currentTarget = bee.getTarget();

            // 如果当前目标不存在或已死亡，每5游戏刻尝试一次获取新目标
            if ((currentTarget == null || !currentTarget.isAlive()) && bee.tickCount % 5 == 0) {
                // 使用BeeGrenadeEntity中的静态方法获取下一个目标
                LivingEntity nextTarget = BeeGrenadeEntity.getNextTarget(bee, currentTarget);
                if (nextTarget != null) {
                    bee.setTarget(nextTarget);
                    bee.setRemainingPersistentAngerTime(600); // 重置为30秒
                    LOGGER.debug("蜜蜂更换了目标: {}", nextTarget.getName().getString());
                }
            }

            // 从配置获取蜜蜂存活时间（秒），并转换为游戏刻
            int beeLifetimeTicks = ModConfigManager.BEE_GRENADE_BEE_LIFETIME.get().intValue() * 20;

            // 如果蜜蜂已经存在超过配置的时间，则移除它
            if (bee.tickCount > beeLifetimeTicks) {
                bee.discard();
                LOGGER.debug("蜜蜂已存在超过{}秒，被移除", ModConfigManager.BEE_GRENADE_BEE_LIFETIME.get());
            }
        }
    }

    /**
     * 在蜜蜂的customServerAiStep方法中注入代码，确保蜜蜂持续攻击目标
     */
    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void onServerAiStep(CallbackInfo ci) {
        Bee bee = (Bee) (Object) this;

        // 检查蜜蜂是否有标签表示它是由蜜蜂手雷生成的
        if (bee.getTags().stream().anyMatch(tag -> tag.startsWith("PlayerFriendly:"))) {
            // 如果蜜蜂有目标，确保它持续保持愤怒状态
            if (bee.getTarget() != null && bee.getRemainingPersistentAngerTime() < 200) {
                bee.setRemainingPersistentAngerTime(600); // 重置为30秒
                LOGGER.debug("重置蜜蜂的愤怒状态，当前目标: {}", bee.getTarget().getName().getString());
            }
        }
    }
} 