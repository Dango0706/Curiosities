package me.tuanzi.curiosities.enchantments.moral_balance;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.util.DebugLogger;
import me.tuanzi.curiosities.effect.ModEffects;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 道德天平附魔事件处理器
 * 处理道德天平附魔的攻击效果和掉落物翻倍
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class MoralBalanceEventHandler {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    // 罪恶效果持续时间（秒）
    private static final int GUILT_EFFECT_DURATION = 15; // 15s

    /**
     * 处理生物受伤事件，应用道德天平附魔效果
     *
     * @param event 生物受伤事件
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        // 检查是否启用附魔
        if (!ModConfigManager.MORAL_BALANCE_ENABLED.get()) {
            return;
        }

        // 获取伤害来源实体
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof Player attacker)) {
            return;
        }

        // 获取被攻击的实体
        LivingEntity target = event.getEntity();

        // 获取攻击者的主手物品
        ItemStack weapon = attacker.getMainHandItem();

        // 检查是否有道德天平附魔
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MORAL_BALANCE.get(), weapon);
        if (enchantLevel <= 0) {
            return;
        }

        float originalDamage = event.getAmount();
        float newDamage = originalDamage;

        // 判断目标类型
        boolean isPeaceful = isPeacefulEntity(target);

        if (isPeaceful) {
            // 对和平生物造成伤害时，武器攻击力x2，但玩家获得"罪恶"debuff
            newDamage = originalDamage * 2.0f;

            // 施加罪恶效果（增加村民交易价格）
            applyGuiltEffect(attacker);

            // 通知玩家
            attacker.displayClientMessage(
                    Component.translatable("message.curiosities.moral_balance.peaceful")
                            .withStyle(ChatFormatting.RED),
                    true
            );

            DebugLogger.debugDetail("道德天平对和平生物造成双倍伤害: {} -> {}", originalDamage, newDamage);
        } else if (isHostileEntity(target)) {
            // 对敌对生物造成伤害时，攻击力减半
            newDamage = originalDamage * 0.5f;

            // 通知玩家
            attacker.displayClientMessage(
                    Component.translatable("message.curiosities.moral_balance.hostile")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );

            DebugLogger.debugDetail("道德天平对敌对生物造成减半伤害: {} -> {}", originalDamage, newDamage);
        }

        // 更新伤害值
        event.setAmount(newDamage);
    }

    /**
     * 处理生物掉落物事件，增加敌对生物的掉落物
     *
     * @param event 生物掉落物事件
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDrops(LivingDropsEvent event) {
        // 检查是否启用附魔
        if (!ModConfigManager.MORAL_BALANCE_ENABLED.get()) {
            return;
        }

        // 获取伤害来源实体
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof Player attacker)) {
            return;
        }

        // 获取死亡的实体
        LivingEntity target = event.getEntity();

        // 只对敌对生物处理
        if (!isHostileEntity(target)) {
            return;
        }

        // 获取攻击者的主手物品
        ItemStack weapon = attacker.getMainHandItem();

        // 检查是否有道德天平附魔
        int enchantLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MORAL_BALANCE.get(), weapon);
        if (enchantLevel <= 0) {
            return;
        }

        // 获取原始掉落物
        Collection<ItemEntity> drops = event.getDrops();
        if (drops.isEmpty()) {
            return;
        }

        // 创建新的掉落物列表
        List<ItemEntity> newDrops = new ArrayList<>();

        // 对每个掉落物进行复制
        for (ItemEntity item : drops) {
            // 复制原始掉落物两次，总共3倍掉落物
            for (int i = 0; i < 2; i++) {
                ItemStack originalStack = item.getItem().copy();

                // 创建新的掉落物实体
                ItemEntity newItem = new ItemEntity(
                        target.level(),
                        item.getX(), item.getY(), item.getZ(),
                        originalStack
                );

                // 设置新掉落物的速度为原始掉落物的速度
                newItem.setDeltaMovement(item.getDeltaMovement());

                // 设置掉落物的拾取延迟和生命周期
                newItem.setPickUpDelay(10);

                // 添加到新掉落物列表
                newDrops.add(newItem);
            }
        }

        // 将新掉落物添加到游戏世界中
        for (ItemEntity newItem : newDrops) {
            target.level().addFreshEntity(newItem);
        }

        DebugLogger.debugDetail("道德天平增加了{}个额外掉落物", newDrops.size());
    }

    /**
     * 判断实体是否为和平生物
     *
     * @param entity 要检查的实体
     * @return 是否为和平生物
     */
    private static boolean isPeacefulEntity(Entity entity) {
        return entity instanceof Animal ||
                entity instanceof AbstractVillager ||
                (entity instanceof LivingEntity living && living.getMobType() == MobType.UNDEAD && !(entity instanceof Enemy));
    }

    /**
     * 判断实体是否为敌对生物
     *
     * @param entity 要检查的实体
     * @return 是否为敌对生物
     */
    private static boolean isHostileEntity(Entity entity) {
        return entity instanceof Enemy;
    }

    /**
     * 应用罪恶效果
     *
     * @param player 玩家
     */
    private static void applyGuiltEffect(Player player) {
        // 使用自定义的罪恶效果，会增加村民交易价格
        player.addEffect(new MobEffectInstance(
                ModEffects.GUILT.get(),
                GUILT_EFFECT_DURATION * 20, // 转换为游戏刻
                0, // 等级0
                false, // 是否环境效果
                true, // 是否显示图标
                true  // 是否显示粒子
        ));

        // 在玩家周围创建红色粒子效果
        if (player.level().isClientSide()) {
            for (int i = 0; i < 10; i++) {
                player.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.CRIMSON_SPORE,
                        player.getRandomX(1.0D),
                        player.getRandomY() + 0.5D,
                        player.getRandomZ(1.0D),
                        0, 0, 0
                );
            }
        }
    }
} 