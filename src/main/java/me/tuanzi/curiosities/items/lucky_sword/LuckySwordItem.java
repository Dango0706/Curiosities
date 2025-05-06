package me.tuanzi.curiosities.items.lucky_sword;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 幸运剑
 * 每次攻击造成随机伤害，伤害范围可配置
 * 负数伤害会治疗目标
 */
public class LuckySwordItem extends SwordItem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final RandomSource RANDOM = RandomSource.create();

    /**
     * 构造函数
     */
    public LuckySwordItem() {
        super(
                Tiers.IRON, // 使用铁质等级作为基础
                0, // 基础攻击力为0，使用随机值
                -2.4F, // 攻击速度
                new Item.Properties().durability(555) // 设置耐久度为555
        );
    }

    /**
     * 重写攻击实体方法，应用随机伤害
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        // 检查是否启用以及目标是否为生物实体
        if (!ModConfigManager.LUCKY_SWORD_ENABLED.get() || !(entity instanceof LivingEntity target)) {
            return false; // 使用原版逻辑
        }

        // 获取随机伤害值
        float damage = getRandomDamage();

        // 获取攻击冷却恢复比例
        float attackStrength = player.getAttackStrengthScale(0.5F);

        // 将伤害与攻击冷却恢复比例相乘
        damage = damage * attackStrength;

        LOGGER.info("幸运剑触发随机伤害: {}, 攻击强度: {}, 最终伤害: {}",
                getRandomDamage(), attackStrength, damage);

        // 处理负数伤害(治疗)
        if (damage < 0 && target.isAlive()) {
            target.hurt(player.damageSources().playerAttack(player), 1);
            target.heal(-damage); // 转为正数进行治疗
            LOGGER.info("幸运剑治疗目标: {}", -damage);
        }
        // 处理正数伤害(攻击)
        else if (damage > 0 && target.isAlive()) {
            // 直接应用伤害
            target.hurt(player.damageSources().playerAttack(player), damage);
            LOGGER.info("幸运剑对目标造成伤害: {}", damage);
        }

        // 消耗耐久
        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

        return true; // 拦截原版攻击逻辑
    }

    /**
     * 重写原始伤害方法，我们将在onLeftClickEntity中应用伤害
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 如果是通过其他方式调用的，仍然消耗耐久
        stack.hurtAndBreak(1, attacker, (entity) ->
                entity.broadcastBreakEvent(attacker instanceof Player ?
                        attacker.getUsedItemHand() : null)
        );

        return true;
    }

    /**
     * 获取随机伤害值
     */
    private float getRandomDamage() {
        int min = ModConfigManager.LUCKY_SWORD_MIN_DAMAGE.get().intValue();
        int max = ModConfigManager.LUCKY_SWORD_MAX_DAMAGE.get().intValue();

        // 确保最小值小于最大值
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }

        // 生成随机伤害
        return RANDOM.nextIntBetweenInclusive(min, max);
    }

    /**
     * 自定义工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 检查幸运剑是否启用
        if (ModConfigManager.LUCKY_SWORD_ENABLED.get()) {
            // 添加伤害范围提示
            int min = ModConfigManager.LUCKY_SWORD_MIN_DAMAGE.get().intValue();
            int max = ModConfigManager.LUCKY_SWORD_MAX_DAMAGE.get().intValue();

            tooltip.add(Component.translatable("item.curiosities.lucky_sword.tooltip.damage_range",
                    min, max).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.translatable("item.curiosities.lucky_sword.tooltip.negative_heal")
                    .withStyle(ChatFormatting.BLUE));
        } else {
            // 当幸运剑被禁用时显示禁用信息
            tooltip.add(Component.translatable("item.curiosities.lucky_sword.tooltip.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }

    /**
     * 限制允许的附魔
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        // 禁止增加攻击力的附魔
        if (isAttackEnchantment(enchantment)) {
            return false;
        }

        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    /**
     * 判断是否是攻击类附魔
     */
    private boolean isAttackEnchantment(Enchantment enchantment) {
        return enchantment == Enchantments.SHARPNESS ||
                enchantment == Enchantments.SMITE ||
                enchantment == Enchantments.BANE_OF_ARTHROPODS ||
                enchantment == Enchantments.FIRE_ASPECT ||
                enchantment == Enchantments.KNOCKBACK;
    }

    /**
     * 设置物品属性
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot == EquipmentSlot.MAINHAND) {
            // 只添加攻击速度，不添加攻击伤害（由随机值决定）
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                    "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    /**
     * 获取物品名称，添加随机彩色效果
     */
    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(ChatFormatting.RESET)
                .withStyle(style -> style.withColor(getRandomNameColor()));
    }

    /**
     * 获取随机名称颜色
     */
    private int getRandomNameColor() {
        // 生成柔和的随机颜色
        int r = 100 + RANDOM.nextInt(155);
        int g = 100 + RANDOM.nextInt(155);
        int b = 100 + RANDOM.nextInt(155);

        return (r << 16) | (g << 8) | b;
    }
} 