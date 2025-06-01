package me.tuanzi.curiosities.potion;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.List;

/**
 * 不死药水工具提示处理器
 * 当不死效果被禁用时，在药水tooltip中添加警告文字
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class UndyingPotionTooltipHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // 检查物品是否为药水
        List<MobEffectInstance> effects = PotionUtils.getMobEffects(stack);
        if (effects.isEmpty()) {
            return;
        }

        // 检查是否含有不死效果
        boolean hasUndyingEffect = effects.stream()
                .anyMatch(effect -> effect.getEffect() == ModEffects.UNDYING.get());

        // 如果含有不死效果且当前禁用该效果
        if (hasUndyingEffect && !ModConfigManager.UNDYING_EFFECT_ENABLED.get()) {
            // 添加红色警告文字
            List<Component> tooltip = event.getToolTip();
            tooltip.add(Component.literal("[此效果已禁用]").withStyle(ChatFormatting.RED));

            LOGGER.debug("为不死药水添加禁用警告文字: {}", stack.getHoverName().getString());
        }
    }
} 