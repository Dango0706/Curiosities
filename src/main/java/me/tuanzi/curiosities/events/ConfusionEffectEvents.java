package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ConfusionEffect;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Random;

/**
 * 混乱效果事件处理类
 * 用于处理混乱效果导致的攻击目标转移
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class ConfusionEffectEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * 处理攻击事件
     * 当有混乱效果的实体攻击时，可能会将伤害转移到自己身上
     */
    @SubscribeEvent
    public static void onEntityAttack(LivingAttackEvent event) {
        // 获取伤害源
        DamageSource source = event.getSource();
        // 获取受伤实体
        LivingEntity target = event.getEntity();

        // 检查伤害源是否来自实体
        if (source.getEntity() instanceof LivingEntity attacker) {
            // 检查攻击者是否有混乱效果
            MobEffectInstance confusionEffect = attacker.getEffect(ModEffects.CONFUSION.get());

            // 如果攻击者有混乱效果并且功能已启用
            if (confusionEffect != null && ModConfigManager.CONFUSION_EFFECT_ENABLED.get()) {
                int amplifier = confusionEffect.getAmplifier();
                float chance = ConfusionEffect.getConfusionChance(amplifier);

                // 根据概率决定是否将攻击转移到自己身上
                if (RANDOM.nextFloat() < chance) {
                    // 取消原始攻击（不对目标造成伤害）
                    event.setCanceled(true);

                    // 计算应该造成的伤害百分比
                    float damagePercent = ConfusionEffect.getDamagePercent(amplifier);
                    float originalDamage = event.getAmount();
                    float selfDamage = originalDamage * damagePercent;

                    // 对自己造成伤害（不是原目标）
                    attacker.hurt(attacker.damageSources().generic(), selfDamage);

                    // 如果是玩家，发送消息提示
                    if (attacker instanceof Player player) {
                        player.displayClientMessage(
                                Component.translatable("effect.curiosities.confusion.self_attack",
                                        String.format("%.1f", selfDamage)),
                                true);
                    }

                    LOGGER.debug("混乱效果触发: {} 对自己造成了 {} 点伤害 (原伤害: {})",
                            attacker.getName().getString(), selfDamage, originalDamage);
                }
            }
        }
    }
}
