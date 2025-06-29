package me.tuanzi.curiosities.items.rose_gold_tools;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 玫瑰金工具材质层级
 * 定义玫瑰金工具的基础属性
 */
public class RoseGoldTier implements Tier {
    public static final RoseGoldTier INSTANCE = new RoseGoldTier();

    @Override
    public int getUses() {
        return 256; // 固定耐久度
    }

    @Override
    public float getSpeed() {
        return 12.0F; // 挖掘速度（黄金工具速度）
    }

    @Override
    public float getAttackDamageBonus() {
        return 0.0F; // 攻击伤害加成，具体值在各工具中设置
    }

    @Override
    public int getLevel() {
        return 2; // 挖掘等级（铁工具等级）
    }

    @Override
    public int getEnchantmentValue() {
        return 22; // 附魔能力（黄金工具等级）
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.GOLD_INGOT); // 使用金锭修复
    }
}
