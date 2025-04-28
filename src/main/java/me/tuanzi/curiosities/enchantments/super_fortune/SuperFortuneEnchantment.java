package me.tuanzi.curiosities.enchantments.super_fortune;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import me.tuanzi.curiosities.Curiosities;

import java.util.List;

/**
 * 超级时运附魔
 * 效果是原版时运的1.5倍，可以与精准采集兼容
 * 当同时有精准采集时，时运效果会应用在精准采集的方块上
 */
public class SuperFortuneEnchantment extends Enchantment {

    /**
     * 构造函数
     */
    public SuperFortuneEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * 获取附魔最大等级
     * @return 最大等级(3)
     */
    @Override
    public int getMaxLevel() {
        return 3;
    }

    /**
     * 获取附魔最小等级
     * @return 最小等级(1)
     */
    @Override
    public int getMinLevel() {
        return 1;
    }

    /**
     * 获取附魔最小消耗
     * @param level 附魔等级
     * @return 最小消耗
     */
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 9;
    }

    /**
     * 获取附魔最大消耗
     * @param level 附魔等级
     * @return 最大消耗
     */
    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 50;
    }

    /**
     * 检查与其他附魔的兼容性
     * 不与原版时运兼容，但可与精准采集兼容
     * @param other 其他附魔
     * @return 是否兼容
     */
    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // 不能与原版时运兼容
        if (other == Enchantments.BLOCK_FORTUNE) {
            return false;
        }
        // 可以与精准采集兼容
        return super.checkCompatibility(other);
    }
    
    /**
     * 判断是否可以在附魔台上应用此附魔
     * @param stack 物品堆
     * @return 是否可以应用
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        // 根据配置决定是否可以在附魔台应用此附魔
        if (!SuperFortuneConfig.isSuperFortuneEnabled()) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack);
    }
    
    /**
     * 是否为宝藏附魔
     * @return 是否为宝藏附魔
     */
    @Override
    public boolean isTreasureOnly() {
        // 当禁用时，将其设为"宝藏附魔"，使其不出现在附魔台
        return !SuperFortuneConfig.isSuperFortuneEnabled();
    }
    
    /**
     * 是否可被发现
     * @return 是否可被发现
     */
    @Override
    public boolean isDiscoverable() {
        // 当禁用时，将其设为不可发现，使其不出现在附魔台和战利品表中
        return SuperFortuneConfig.isSuperFortuneEnabled();
    }
    
    /**
     * 是否可被交易
     * @return 是否可被交易
     */
    @Override
    public boolean isTradeable() {
        // 当禁用时，村民不会提供这个附魔
        return SuperFortuneConfig.isSuperFortuneEnabled();
    }
    
    /**
     * 检查超级时运在当前物品上是否可用
     */
    public static boolean isSuperFortuneUsable(ItemStack stack) {
        // 检查配置是否启用
        if (!SuperFortuneConfig.isSuperFortuneEnabled()) {
            return false;
        }
        
        // 检查物品是否有超级时运附魔
        int level = EnchantmentHelper.getItemEnchantmentLevel(me.tuanzi.curiosities.enchantments.ModEnchantments.SUPER_FORTUNE.get(), stack);
        return level > 0;
    }
    
    /**
     * 工具提示处理器
     * 为禁用的超级时运附魔添加红色警告文字
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
            
            // 检查物品是否有超级时运附魔且当前是否禁用
            int level = EnchantmentHelper.getItemEnchantmentLevel(me.tuanzi.curiosities.enchantments.ModEnchantments.SUPER_FORTUNE.get(), stack);
            if (level > 0 && !SuperFortuneConfig.isSuperFortuneEnabled()) {
                // 添加红色文字提示
                List<Component> tooltip = event.getToolTip();
                
                // 寻找超级时运附魔文本的位置
                for (int i = 1; i < tooltip.size(); i++) {
                    Component line = tooltip.get(i);
                    String plainText = line.getString();
                    
                    // 如果找到超级时运附魔的描述行
                    if (plainText.contains(Component.translatable("enchantment.curiosities.super_fortune").getString())) {
                        // 在该行后添加红色警告文字
                        tooltip.add(i + 1, Component.literal("[已被禁用]").withStyle(ChatFormatting.RED));
                        break;
                    }
                }
            }
        }
    }
} 