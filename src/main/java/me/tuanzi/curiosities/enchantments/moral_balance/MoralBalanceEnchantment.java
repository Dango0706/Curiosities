package me.tuanzi.curiosities.enchantments.moral_balance;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
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
 * 道德天平附魔
 * 对和平生物造成伤害时，武器攻击力x2但玩家获得"罪恶"debuff（村民交易价格暴涨）
 * 对敌对生物造成伤害时，攻击力减半但掉落物×3
 */
public class MoralBalanceEnchantment extends Enchantment {
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    
    /**
     * 构造函数
     * 设置附魔稀有度、适用类别和装备槽位
     */
    public MoralBalanceEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * 获取附魔最大等级
     * @return 最大等级(1)
     */
    @Override
    public int getMaxLevel() {
        return 1;
    }

    /**
     * 判断是否可以在附魔台上应用此附魔
     * @param stack 物品堆
     * @return 始终返回false，因为该附魔只能通过合成获得
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        // 无法在附魔台获得此附魔
        return false;
    }
    
    /**
     * 是否为宝藏附魔
     * @return 始终为true，使其不出现在附魔台
     */
    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    
    /**
     * 是否可被发现（如钓鱼、村民交易等）
     * @return 始终为false，使其不出现在战利品表中
     */
    @Override
    public boolean isDiscoverable() {
        return false;
    }
    
    /**
     * 是否可被交易
     * @return 始终为false，村民不会提供这个附魔
     */
    @Override
    public boolean isTradeable() {
        return false;
    }
    
    /**
     * 是否是诅咒附魔
     * @return 不是诅咒附魔
     */
    @Override
    public boolean isCurse() {
        return false;
    }

    /**
     * 检查与其他附魔的兼容性
     * @param other 其他附魔
     * @return 是否兼容
     */
    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // 允许与大多数附魔兼容
        return super.checkCompatibility(other);
    }
    
    /**
     * 检查道德天平在当前物品上是否可用
     * 
     * @param stack 物品堆
     * @return 是否可用
     */
    public static boolean isMoralBalanceUsable(ItemStack stack) {
        // 检查配置是否启用
        if (!MoralBalanceConfig.isMoralBalanceEnabled()) {
            return false;
        }
        
        // 检查物品是否有道德天平附魔
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MORAL_BALANCE.get(), stack);
        return level > 0;
    }
    
    /**
     * 工具提示处理器
     * 为禁用的道德天平附魔添加红色警告文字
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
            
            // 检查物品是否有道德天平附魔且当前是否禁用
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.MORAL_BALANCE.get(), stack);
            if (level > 0 && !MoralBalanceConfig.isMoralBalanceEnabled()) {
                // 添加红色文字提示
                List<Component> tooltip = event.getToolTip();
                
                // 寻找道德天平附魔文本的位置
                for (int i = 1; i < tooltip.size(); i++) {
                    Component line = tooltip.get(i);
                    String plainText = line.getString();
                    
                    // 如果找到道德天平附魔的描述行
                    if (plainText.contains(Component.translatable("enchantment.curiosities.moral_balance").getString())) {
                        // 在该行后添加红色警告文字
                        tooltip.add(i + 1, Component.literal("[已被禁用]").withStyle(ChatFormatting.RED));
                        break;
                    }
                }
            }
        }
    }
} 