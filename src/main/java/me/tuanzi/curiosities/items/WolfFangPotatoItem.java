package me.tuanzi.curiosities.items;

import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 狼牙土豆物品
 * 食用后获得"狼群领袖"效果——半径30格内狼/狗会协助攻击你锁定的目标
 * 但期间会被熊猫敌视，效果持续2分钟(2400刻)
 */
public class WolfFangPotatoItem extends Item {
    // 效果持续时间(2分钟 = 2400刻)
    private static final int EFFECT_DURATION = 2400;

    /**
     * 构造函数
     */
    public WolfFangPotatoItem() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(8)          // 提供8点饥饿值
                        .saturationMod(0.8f)   // 饱食度修正
                        .meat()                // 算作肉类食物
                        .build())
        );
    }

    /**
     * 物品被使用(吃下)后触发
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        // 只在物品启用并且是服务端时应用效果
        if (!level.isClientSide && livingEntity instanceof Player && ModConfigManager.WOLF_FANG_POTATO_ENABLED.get()) {
            // 应用狼群领袖效果
            livingEntity.addEffect(new MobEffectInstance(
                    ModEffects.WOLF_PACK_LEADER.get(),  // 效果类型
                    EFFECT_DURATION,                     // 持续时间
                    0,                                   // 效果等级
                    false,                               // 是否环境效果
                    true,                                // 是否显示粒子
                    true                                 // 是否显示图标
            ));
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

        // 添加物品描述
        tooltip.add(Component.translatable("item.curiosities.wolf_fang_potato.desc")
                .withStyle(ChatFormatting.GRAY));

        // 如果物品被禁用，添加禁用提示
        if (!ModConfigManager.WOLF_FANG_POTATO_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }

    /**
     * 检查物品是否可食用
     */
    @Override
    public boolean isEdible() {
        // 根据配置决定是否可食用
        return super.isEdible() && ModConfigManager.WOLF_FANG_POTATO_ENABLED.get();
    }
} 