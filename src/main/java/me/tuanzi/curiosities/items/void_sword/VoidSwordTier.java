package me.tuanzi.curiosities.items.void_sword;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

/**
 * 虚空吞噬之剑材质层级
 * 定义剑的基础属性
 */
public class VoidSwordTier implements Tier {
    public static final VoidSwordTier INSTANCE = new VoidSwordTier();

    @Override
    public int getUses() {
        return 2000; // 耐久度
    }

    @Override
    public float getSpeed() {
        return 9.0F; // 挖掘速度
    }

    @Override
    public float getAttackDamageBonus() {
        return 6.0F; // 将基础伤害加成调整为6，最终伤害 = 6(加成) + 1(基础) + 3(ModItems中设置) = 10
    }

    @Override
    public int getLevel() {
        return 4; // 挖掘等级，与下界合金相同
    }

    @Override
    public int getEnchantmentValue() {
        return 15; // 附魔值
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Blocks.OBSIDIAN); // 用黑曜石修复
    }
} 