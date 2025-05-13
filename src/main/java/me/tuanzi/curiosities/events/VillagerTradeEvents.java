package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.items.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;

/**
 * 村民交易事件处理类
 * 添加自定义物品到村民交易列表
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class VillagerTradeEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 处理村民交易生成事件
     * 向牧师村民添加时空卷轴的交易选项
     */
    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        // 检查是否开启了时空卷轴的村民交易
        if (!ModConfigManager.SCROLL_OF_SPACETIME_TRADEABLE.get()) {
            return;
        }

        // 只给牧师村民添加交易
        if (event.getType() == VillagerProfession.CLERIC) {
            // 添加到大师级（4级）交易列表
            List<VillagerTrades.ItemListing> masterTrades = event.getTrades().get(4);
            if (masterTrades != null) {
                // 添加自定义交易
                masterTrades.add(new ScrollOfSpacetimeTrade());
                LOGGER.debug("已向牧师村民添加时空卷轴交易选项");
            }
        }
    }

    /**
     * 时空卷轴交易类
     * 村民用6个绿宝石和1个末影珍珠交换1个时空卷轴
     */
    static class ScrollOfSpacetimeTrade implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource rand) {
            // 创建交易项：6个绿宝石+1个末影珍珠 换取 1个时空卷轴
            ItemStack emeralds = new ItemStack(Items.EMERALD, 6);
            ItemStack enderPearl = new ItemStack(Items.ENDER_PEARL, 1);
            ItemStack scrollOfSpacetime = new ItemStack(ModItems.SCROLL_OF_SPACETIME.get(), 1);

            // 使用次数2-6次，价格乘数0.05（经验奖励）
            int uses = 2 + rand.nextInt(5);
            float priceMultiplier = 0.05f;

            return new MerchantOffer(
                    emeralds,           // 第一个物品（绿宝石）
                    enderPearl,         // 第二个物品（末影珍珠）
                    scrollOfSpacetime,  // 结果物品（时空卷轴）
                    uses,               // 最大使用次数
                    10,                 // 村民经验点数
                    priceMultiplier     // 价格乘数
            );
        }
    }
} 