package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * 不死效果事件处理器
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class UndyingEffectEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 处理实体死亡事件
     * 当实体有不死效果时，触发不死图腾效果并阻止死亡
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        // 检查配置是否启用
        if (!ModConfigManager.UNDYING_EFFECT_ENABLED.get()) {
            return;
        }

        LivingEntity entity = event.getEntity();

        // 检查实体是否有不死效果
        if (entity.hasEffect(ModEffects.UNDYING.get())) {
            // 取消死亡事件
            event.setCanceled(true);

            // 移除不死效果
            entity.removeEffect(ModEffects.UNDYING.get());

            // 模拟不死图腾效果
            // 设置生命值
            entity.setHealth(1.0F);

            // 清除负面效果
            entity.removeAllEffects();

            // 添加常见的复活后保护效果
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

            // 播放声音和粒子效果
            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.TOTEM_USE,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );

                // 创建粒子效果
                serverLevel.sendParticles(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        entity.getX(),
                        entity.getY() + 1.0D,
                        entity.getZ(),
                        50,  // 粒子数量
                        0.3D,  // X方向扩散
                        0.5D,  // Y方向扩散
                        0.3D,  // Z方向扩散
                        0.3D   // 速度
                );
            }

            LOGGER.debug("不死效果触发：{} 避免了死亡", entity.getName().getString());
        }
    }
} 