package me.tuanzi.curiosities.enchantments.steel_covenant;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;

/**
 * 钢契附魔
 * 仅用于胸甲，限制玩家受到的最大伤害值
 * 最大等级为5，每级增加伤害抵抗
 */
public class SteelCovenantEnchantment extends Enchantment {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    // 装备槽位：仅胸甲
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{
            EquipmentSlot.CHEST
    };

    /**
     * 构造函数
     * 设置附魔稀有度、适用类别和装备槽位
     */
    public SteelCovenantEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_CHEST, ARMOR_SLOTS);
    }

    /**
     * 检查钢契附魔在当前物品上是否可用
     *
     * @param stack 物品堆
     * @return 是否可用
     */
    public static boolean isSteelCovenantUsable(ItemStack stack) {
        // 检查配置是否启用
        if (!ModConfigManager.STEEL_COVENANT_ENABLED.get()) {
            return false;
        }

        // 检查物品是否有钢契附魔
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.STEEL_COVENANT.get(), stack);
        return level > 0;
    }

    /**
     * 获取附魔最大等级
     *
     * @return 最大等级(5)
     */
    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * 获取附魔最小消耗
     *
     * @param level 附魔等级
     * @return 最小消耗值
     */
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 5;
    }

    /**
     * 获取附魔最大消耗
     *
     * @param level 附魔等级
     * @return 最大消耗值
     */
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 5;
    }

    /**
     * 是否可被交易
     *
     * @return 根据配置决定是否可被村民交易
     */
    @Override
    public boolean isTradeable() {
        return ModConfigManager.STEEL_COVENANT_TRADEABLE.get();
    }

    /**
     * 工具提示处理器
     * 为禁用的钢契附魔添加红色警告文字
     */
    @Mod.EventBusSubscriber
    public static class TooltipHandler {
        /**
         * 物品提示事件处理
         *
         * @param event 物品提示事件
         */
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();

            // 检查物品是否有钢契附魔且当前是否禁用
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.STEEL_COVENANT.get(), stack);
            if (level > 0 && !ModConfigManager.STEEL_COVENANT_ENABLED.get()) {
                // 添加红色文字提示
                List<Component> tooltip = event.getToolTip();

                // 寻找钢契附魔文本的位置
                for (int i = 1; i < tooltip.size(); i++) {
                    Component line = tooltip.get(i);
                    String plainText = line.getString();

                    // 如果找到钢契附魔的描述行
                    if (plainText.contains(Component.translatable("enchantment.curiosities.steel_covenant").getString())) {
                        // 在该行后添加红色警告文字
                        tooltip.add(i + 1, Component.literal("[已被禁用]").withStyle(ChatFormatting.RED));
                        break;
                    }
                }
            }
        }
    }
} 