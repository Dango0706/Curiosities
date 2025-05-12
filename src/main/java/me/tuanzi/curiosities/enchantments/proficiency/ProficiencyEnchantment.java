package me.tuanzi.curiosities.enchantments.proficiency;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

/**
 * 熟练附魔
 * 每级增加一定百分比的攻击速度
 */
public class ProficiencyEnchantment extends Enchantment {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    // 攻击速度修饰符UUID，确保唯一性
    private static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("9c389a0d-92d8-4e67-b5e9-17e5f2b9f343");

    /**
     * 构造函数
     * 设置附魔稀有度、适用类别和装备槽位
     */
    public ProficiencyEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * 获取附魔最大等级
     *
     * @return 最大等级(4)
     */
    @Override
    public int getMaxLevel() {
        return 4;
    }

    /**
     * 获取最小附魔等级
     *
     * @param level 附魔等级
     * @return 最小附魔等级
     */
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 10; // 15级起步，每升一级+10
    }

    /**
     * 获取最大附魔等级
     *
     * @param level 附魔等级
     * @return 最大附魔等级
     */
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 15; // 最小等级+15
    }

    /**
     * 判断是否可以在附魔台上应用此附魔
     *
     * @param stack 物品堆
     * @return 是否可以应用
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        // 根据配置决定是否可以在附魔台应用此附魔
        if (!ModConfigManager.PROFICIENCY_ENABLED.get()) {
            return false;
        }
        // 只有武器类物品可以应用此附魔
        return EnchantmentCategory.WEAPON.canEnchant(stack.getItem());
    }

    /**
     * 是否为宝藏附魔
     *
     * @return 是否为宝藏附魔
     */
    @Override
    public boolean isTreasureOnly() {
        // 设为"宝藏附魔"，使其更加稀有
        return true;
    }

    /**
     * 是否可被发现
     *
     * @return 是否可被发现
     */
    @Override
    public boolean isDiscoverable() {
        // 当禁用时，将其设为不可发现，使其不出现在附魔台和战利品表中
        return ModConfigManager.PROFICIENCY_ENABLED.get();
    }

    /**
     * 是否可被交易
     *
     * @return 是否可被交易
     */
    @Override
    public boolean isTradeable() {
        // 当禁用时，村民不会提供这个附魔
        return ModConfigManager.PROFICIENCY_ENABLED.get();
    }

    /**
     * 检查与其他附魔的兼容性
     *
     * @param other 其他附魔
     * @return 是否兼容
     */
    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // 允许与大多数武器附魔兼容
        return super.checkCompatibility(other);
    }

    /**
     * 物品属性修饰符事件处理
     * 监听器类负责应用攻击速度加成
     */
    @Mod.EventBusSubscriber(modid = Curiosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class AttributeHandler {
        /**
         * 物品属性修饰符事件处理
         *
         * @param event 物品属性修饰符事件
         */
        @SubscribeEvent
        public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
            ItemStack stack = event.getItemStack();

            // 只处理主手装备的物品
            if (event.getSlotType() != EquipmentSlot.MAINHAND) {
                return;
            }

            // 检查物品是否有熟练附魔且当前是否启用
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PROFICIENCY.get(), stack);
            if (level > 0 && ModConfigManager.PROFICIENCY_ENABLED.get()) {
                // 计算攻击速度增加百分比
                double speedBonus = level * ModConfigManager.PROFICIENCY_ATTACK_SPEED_PERCENT.get() / 100.0;

                // 添加攻击速度修饰符
                event.addModifier(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                ATTACK_SPEED_MODIFIER_UUID,
                                "Proficiency attack speed bonus",
                                speedBonus,
                                AttributeModifier.Operation.MULTIPLY_TOTAL
                        )
                );
            }
        }
    }

    /**
     * 工具提示处理器
     * 为禁用的熟练附魔添加红色警告文字
     */
    @Mod.EventBusSubscriber(modid = Curiosities.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class TooltipHandler {
        /**
         * 物品提示事件处理
         *
         * @param event 物品提示事件
         */
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();

            // 检查物品是否有熟练附魔且当前是否禁用
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PROFICIENCY.get(), stack);
            if (level > 0 && !ModConfigManager.PROFICIENCY_ENABLED.get()) {
                // 添加红色文字提示
                List<Component> tooltip = event.getToolTip();

                // 寻找熟练附魔文本的位置
                for (int i = 1; i < tooltip.size(); i++) {
                    Component line = tooltip.get(i);
                    String plainText = line.getString();

                    // 如果找到熟练附魔的描述行
                    if (plainText.contains(Component.translatable("enchantment.curiosities.proficiency").getString())) {
                        // 在该行后添加红色警告文字
                        tooltip.add(i + 1, Component.literal("[已被禁用]").withStyle(ChatFormatting.RED));
                        break;
                    }
                }
            }
        }
    }
} 