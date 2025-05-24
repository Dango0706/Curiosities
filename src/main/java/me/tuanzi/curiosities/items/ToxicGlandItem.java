package me.tuanzi.curiosities.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 涡毒腺体物品
 * 食用后补充4格饱食度与2的饱和度，并给予玩家多种负面效果
 * 包括1分钟中毒IV，1分钟反胃，1分钟虚弱II，15s失明
 */
public class ToxicGlandItem extends Item {

    /**
     * 构造函数
     */
    public ToxicGlandItem() {
        super(new Item.Properties()
                .food(new FoodProperties.Builder()
                        .nutrition(4)          // 提供4点饥饿值
                        .saturationMod(2.0f)   // 饱和度修正
                        .build())
        );
    }

    /**
     * 物品被使用(吃下)后触发
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        // 只在服务端应用效果
        if (!level.isClientSide) {
            // 应用1分钟中毒IV效果
            livingEntity.addEffect(new MobEffectInstance(
                    MobEffects.POISON,          // 中毒效果
                    1200,                       // 持续时间（1分钟 = 1200刻）
                    3,                          // 效果等级IV (从0开始计数，所以是3)
                    false,                      // 是否环境效果
                    true,                       // 是否显示粒子
                    true                        // 是否显示图标
            ));

            // 应用1分钟反胃效果
            livingEntity.addEffect(new MobEffectInstance(
                    MobEffects.CONFUSION,       // 反胃效果
                    1200,                       // 持续时间（1分钟 = 1200刻）
                    0,                          // 效果等级I
                    false,                      // 是否环境效果
                    true,                       // 是否显示粒子
                    true                        // 是否显示图标
            ));

            // 应用1分钟虚弱II效果
            livingEntity.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,        // 虚弱效果
                    1200,                       // 持续时间（1分钟 = 1200刻）
                    1,                          // 效果等级II (从0开始计数，所以是1)
                    false,                      // 是否环境效果
                    true,                       // 是否显示粒子
                    true                        // 是否显示图标
            ));

            // 应用15秒失明效果
            livingEntity.addEffect(new MobEffectInstance(
                    MobEffects.BLINDNESS,       // 失明效果
                    300,                        // 持续时间（15秒 = 300刻）
                    0,                          // 效果等级I
                    false,                      // 是否环境效果
                    true,                       // 是否显示粒子
                    true                        // 是否显示图标
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
        tooltip.add(Component.translatable("item.curiosities.toxic_gland.desc")
                .withStyle(ChatFormatting.GRAY));
    }
}
