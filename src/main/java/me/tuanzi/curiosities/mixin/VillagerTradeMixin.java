package me.tuanzi.curiosities.mixin;

import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 村民价格调整Mixin
 * <p>
 * 拦截Villager.updateSpecialPrices方法
 * 在这个方法中考虑玩家的罪恶效果，增加交易价格
 */
@Mixin(Villager.class)
public class VillagerTradeMixin {

    /**
     * 在村民计算特殊价格时，考虑玩家的罪恶效果
     * <p>
     * 该方法在村民交易界面打开时调用，计算所有特殊价格
     * 我们在这里注入罪恶效果的价格增加逻辑
     *
     * @param player 交易的玩家
     * @param ci     回调信息
     */
    @Inject(method = "updateSpecialPrices", at = @At("TAIL"))
    private void applyGuiltEffect(Player player, CallbackInfo ci) {
        // 检查玩家是否有罪恶效果
        if (player.hasEffect(ModEffects.GUILT.get())) {
            // 获取效果等级
            int amplifier = player.getEffect(ModEffects.GUILT.get()).getAmplifier();

            // 获取村民实例和交易列表
            Villager villager = (Villager) (Object) this;
            MerchantOffers offers = villager.getOffers();

            // 应用价格增加
            for (MerchantOffer offer : offers) {
                // 计算价格增加系数，与原版村庄英雄效果对应
                // 原版村庄英雄是 0.3D + 0.0625D * amplifier
                // 我们的罪恶效果是增加75%每级，换算成小数是0.75
                double priceIncreaseFactor = 0.75D + 0.75D * (double) amplifier;

                // 计算价格增加量，使用Math.floor保持与原版一致
                int priceIncrease = (int) Math.floor(priceIncreaseFactor * (double) offer.getBaseCostA().getCount());

                // 确保至少增加1点价格
                priceIncrease = Math.max(priceIncrease, 1);

                // 添加到特殊价格差异中，正值代表价格增加（与村庄英雄效果相反）
                offer.addToSpecialPriceDiff(priceIncrease);
            }

            // 通知玩家价格增加
            int percentIncrease = Math.round((float) ((0.75D + 0.75D * (double) amplifier) * 100.0D));
            player.displayClientMessage(
                    Component.translatable(
                            "message.curiosities.guilt.trading_price_increase",
                            percentIncrease
                    ).withStyle(ChatFormatting.RED),
                    true
            );
        }
    }
} 