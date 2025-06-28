package me.tuanzi.curiosities.items.probability_holy_sword;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 概率圣剑
 * 基础攻击伤害6点，耐久度300点
 * 只能用铁锭修复，不能附魔（除了诅咒）
 * 20%概率触发随机特殊效果
 */
public class ProbabilityHolySwordItem extends SwordItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RandomSource RANDOM = RandomSource.create();

    public ProbabilityHolySwordItem() {
        super(
                Tiers.IRON, // 使用铁质等级作为基础
                0, // 基础攻击力通过属性修改器设置
                -2.4F, // 攻击速度
                new Item.Properties().durability(300) // 设置耐久度为300
        );
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 检查是否启用
        if (!ModConfigManager.PROBABILITY_HOLY_SWORD_ENABLED.get()) {
            return super.hurtEnemy(stack, target, attacker);
        }

        // 检查是否触发特殊效果
        if (attacker instanceof Player player && RANDOM.nextFloat() < ModConfigManager.PROBABILITY_HOLY_SWORD_EFFECT_CHANCE.get()) {
            triggerRandomEffect(player, target, attacker.level());
        }

        // 消耗耐久
        stack.hurtAndBreak(1, attacker, (entity) ->
                entity.broadcastBreakEvent(attacker instanceof Player ?
                        attacker.getUsedItemHand() : null)
        );

        return true;
    }

    /**
     * 触发随机特殊效果
     */
    private void triggerRandomEffect(Player player, LivingEntity target, Level level) {
        int effectType = RANDOM.nextInt(3); // 0-2随机选择效果

        switch (effectType) {
            case 0 -> triggerLuckyStrike(player, target, level);
            case 1 -> triggerCursedThrust(player, target, level);
            case 2 -> triggerSpatialChaos(player, target, level);
        }
    }

    /**
     * 幸运斩：瞬间击杀低血量上限的生物
     */
    private void triggerLuckyStrike(Player player, LivingEntity target, Level level) {
        // 检查目标生物的最大血量是否低于配置的阈值
        double maxHealthThreshold = ModConfigManager.PROBABILITY_HOLY_SWORD_LUCKY_STRIKE_MAX_HEALTH.get();
        if (target.getMaxHealth() <= maxHealthThreshold) {
            target.hurt(player.damageSources().playerAttack(player), Float.MAX_VALUE);

            // 播放音效和显示消息
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.5F);

            player.displayClientMessage(Component.translatable("item.curiosities.probability_holy_sword.lucky_strike")
                    .withStyle(ChatFormatting.GOLD), true);

            LOGGER.debug("概率圣剑触发幸运斩效果，目标最大血量: {}, 阈值: {}", target.getMaxHealth(), maxHealthThreshold);
        }
    }

    /**
     * 厄运突刺：对使用者造成3点伤害，对敌人造成20点暴击伤害
     */
    private void triggerCursedThrust(Player player, LivingEntity target, Level level) {
        // 对玩家造成3点伤害
        player.hurt(player.damageSources().magic(), 3.0F);

        // 对目标造成20点暴击伤害
        target.hurt(player.damageSources().playerAttack(player), 20.0F);

        // 播放音效和显示消息
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 0.8F);

        player.displayClientMessage(Component.translatable("item.curiosities.probability_holy_sword.cursed_thrust")
                .withStyle(ChatFormatting.RED), true);

        LOGGER.debug("概率圣剑触发厄运突刺效果");
    }

    /**
     * 时空错乱：将敌人传送到20格范围内的随机位置
     */
    private void triggerSpatialChaos(Player player, LivingEntity target, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            // 在20格范围内寻找随机位置
            BlockPos targetPos = target.blockPosition();
            int attempts = 10; // 最多尝试10次找到合适位置

            for (int i = 0; i < attempts; i++) {
                double x = targetPos.getX() + (RANDOM.nextDouble() - 0.5) * 40; // -20到+20范围
                double z = targetPos.getZ() + (RANDOM.nextDouble() - 0.5) * 40;
                double y = targetPos.getY() + RANDOM.nextInt(10) - 5; // -5到+5范围

                BlockPos newPos = new BlockPos((int) x, (int) y, (int) z);

                // 检查位置是否安全（不在方块内且下方有支撑）
                if (serverLevel.isEmptyBlock(newPos) &&
                        serverLevel.isEmptyBlock(newPos.above()) &&
                        !serverLevel.isEmptyBlock(newPos.below())) {

                    target.teleportTo(x, y, z);

                    // 播放音效和显示消息
                    level.playSound(null, x, y, z,
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    player.displayClientMessage(Component.translatable("item.curiosities.probability_holy_sword.spatial_chaos")
                            .withStyle(ChatFormatting.LIGHT_PURPLE), true);

                    LOGGER.debug("概率圣剑触发时空错乱效果，传送到: {}, {}, {}", x, y, z);
                    break;
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (ModConfigManager.PROBABILITY_HOLY_SWORD_ENABLED.get()) {
            // 添加物品描述
            tooltip.add(Component.translatable("item.curiosities.probability_holy_sword.desc")
                    .withStyle(ChatFormatting.GRAY));

            // 添加特殊效果说明
            int effectChance = (int) (ModConfigManager.PROBABILITY_HOLY_SWORD_EFFECT_CHANCE.get() * 100);
            tooltip.add(Component.translatable("item.curiosities.probability_holy_sword.effect_chance", effectChance)
                    .withStyle(ChatFormatting.YELLOW));

            // 添加修复材料说明
            tooltip.add(Component.translatable("item.curiosities.probability_holy_sword.repair")
                    .withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Component.translatable("item.curiosities.probability_holy_sword.tooltip.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        // 只允许诅咒类附魔
        return enchantment.isCurse();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        // 只能用铁锭修复
        return repair.getItem() == Items.IRON_INGOT;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot == EquipmentSlot.MAINHAND) {
            // 设置攻击伤害
            int baseDamage = ModConfigManager.PROBABILITY_HOLY_SWORD_BASE_DAMAGE.get();
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                    "Weapon modifier", baseDamage, AttributeModifier.Operation.ADDITION));

            // 设置攻击速度
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                    "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public Component getName(ItemStack stack) {
        // 设置物品名称为金色
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(ChatFormatting.GOLD);
    }
}
