package me.tuanzi.curiosities.enchantments.chain_mining;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;

/**
 * 连锁挖掘附魔
 * 使工具能够连锁挖掘相同类型的方块
 */
public class ChainMiningEnchantment extends Enchantment {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     * 设置附魔稀有度、适用类别和装备槽位
     */
    public ChainMiningEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * 检查连锁挖掘在当前物品上是否可用
     *
     * @param stack 物品堆
     * @return 是否可用
     */
    public static boolean isChainMiningUsable(ItemStack stack) {
        // 检查配置是否启用
        if (!ModConfigManager.CHAIN_MINING_ENABLED.get()) {
            return false;
        }

        // 检查物品是否有连锁挖掘附魔
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CHAIN_MINING.get(), stack);
        return level > 0;
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
     * 判断是否可以在附魔台上应用此附魔
     *
     * @param stack 物品堆
     * @return 是否可以应用
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        // 根据配置决定是否可以在附魔台应用此附魔
        if (!ModConfigManager.CHAIN_MINING_ENABLED.get()) {
            return false;
        }
        // 只有工具类物品可以应用此附魔
        return stack.getItem() instanceof TieredItem;
    }

    /**
     * 是否为宝藏附魔
     *
     * @return 是否为宝藏附魔
     */
    @Override
    public boolean isTreasureOnly() {
        // 当禁用时，将其设为"宝藏附魔"，使其不出现在附魔台
        return !ModConfigManager.CHAIN_MINING_ENABLED.get();
    }

    /**
     * 是否可被发现
     *
     * @return 是否可被发现
     */
    @Override
    public boolean isDiscoverable() {
        // 当禁用时，将其设为不可发现，使其不出现在附魔台和战利品表中
        return ModConfigManager.CHAIN_MINING_ENABLED.get();
    }

    /**
     * 是否可被交易
     *
     * @return 是否可被交易
     */
    @Override
    public boolean isTradeable() {
        // 当禁用时，村民不会提供这个附魔
        return ModConfigManager.CHAIN_MINING_ENABLED.get();
    }

    /**
     * 检查与其他附魔的兼容性
     *
     * @param other 其他附魔
     * @return 是否兼容
     */
    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // 允许与精准采集、时运等兼容
        return super.checkCompatibility(other);
    }

    /**
     * 工具提示处理器
     * 为禁用的连锁挖掘附魔添加红色警告文字
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

            // 检查物品是否有连锁挖掘附魔且当前是否禁用
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CHAIN_MINING.get(), stack);
            if (level > 0 && !ModConfigManager.CHAIN_MINING_ENABLED.get()) {
                // 添加红色文字提示
                List<Component> tooltip = event.getToolTip();

                // 寻找连锁挖掘附魔文本的位置
                for (int i = 1; i < tooltip.size(); i++) {
                    Component line = tooltip.get(i);
                    String plainText = line.getString();

                    // 如果找到连锁挖掘附魔的描述行
                    if (plainText.contains(Component.translatable("enchantment.curiosities.chain_mining").getString())) {
                        // 在该行后添加红色警告文字
                        tooltip.add(i + 1, Component.literal("[已被禁用]").withStyle(ChatFormatting.RED));
                        break;
                    }
                }
            }
        }
    }
}