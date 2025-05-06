package me.tuanzi.curiosities.items.screaming_pie;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 尖叫派
 * 食用后恢复全部饱食度，获得尖叫和缓降效果
 */
public class ScreamingPieItem extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造函数
     */
    public ScreamingPieItem() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(20)          // 提供20点饥饿值（满值）
                        .saturationMod(1.0f)    // 最高的饱食度修正
                        .alwaysEat()            // 即使不饿也可以吃
                        .build())
        );
    }

    /**
     * 物品被使用(吃下)后触发
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        // 只在服务端且配置启用时应用效果
        if (!level.isClientSide && livingEntity instanceof Player player && ModConfigManager.SCREAMING_PIE_ENABLED.get()) {
            // 获取效果持续时间(秒转为刻)
            int slowFallingDuration = ModConfigManager.SCREAMING_PIE_SLOW_FALLING_DURATION.get() * 20;
            int screamingDuration = ModConfigManager.SCREAMING_PIE_SCREAMING_DURATION.get() * 20;

            // 应用缓降效果
            player.addEffect(new MobEffectInstance(
                    MobEffects.SLOW_FALLING,        // 缓降效果
                    slowFallingDuration,            // 持续时间
                    0,                              // 效果等级
                    false,                          // 是否环境效果
                    true,                           // 是否显示粒子
                    true                            // 是否显示图标
            ));

            // 只有在尖叫效果已启用时添加尖叫效果
            if (ModConfigManager.SCREAMING_EFFECT_ENABLED.get()) {
                player.addEffect(new MobEffectInstance(
                        ModEffects.SCREAMING.get(),     // 尖叫效果
                        screamingDuration,              // 持续时间
                        0,                              // 效果等级
                        false,                          // 是否环境效果
                        true,                           // 是否显示粒子
                        true                            // 是否显示图标
                ));
            }

            LOGGER.info("玩家 {} 食用尖叫派，获得 {}s 缓降效果和 {}s 尖叫效果",
                    player.getName().getString(),
                    ModConfigManager.SCREAMING_PIE_SLOW_FALLING_DURATION.get(),
                    ModConfigManager.SCREAMING_PIE_SCREAMING_DURATION.get());
        }

        // 调用父类方法完成食物使用
        return super.finishUsingItem(stack, level, livingEntity);
    }

    /**
     * 添加物品工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 检查尖叫派是否启用
        if (ModConfigManager.SCREAMING_PIE_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.screaming_pie.tooltip.effect"));

            if (ModConfigManager.SCREAMING_EFFECT_ENABLED.get()) {
                tooltip.add(Component.translatable("item.curiosities.screaming_pie.tooltip.screaming",
                        ModConfigManager.SCREAMING_PIE_SCREAMING_DURATION.get()));
            }

            tooltip.add(Component.translatable("item.curiosities.screaming_pie.tooltip.slow_falling",
                    ModConfigManager.SCREAMING_PIE_SLOW_FALLING_DURATION.get()));
        } else {
            // 当尖叫派被禁用时显示禁用信息
            tooltip.add(Component.translatable("item.curiosities.screaming_pie.tooltip.disabled")
                    .withStyle(net.minecraft.ChatFormatting.RED));
        }
    }
    
    /**
     * 检查物品是否可食用
     */
    @Override
    public boolean isEdible() {
        // 根据配置决定是否可食用
        return super.isEdible() && ModConfigManager.SCREAMING_PIE_ENABLED.get();
    }
} 