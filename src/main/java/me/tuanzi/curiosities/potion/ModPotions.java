package me.tuanzi.curiosities.potion;

import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组药水注册类
 * 负责注册所有模组自定义药水
 */
public class ModPotions {
    /**
     * 药水注册表
     */
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Curiosities.MODID);

    /**
     * 富有药水 - 普通版本
     * 效果：附近的村民会跟随玩家，并显示爱心和绿宝石粒子
     * 时长：3分钟 (3600 ticks)
     */
    public static final RegistryObject<Potion> RICH = POTIONS.register(
            "rich",
            () -> new Potion(new MobEffectInstance(ModEffects.RICH.get(), 3600))
    );

    /**
     * 富有药水 - 长效版本
     * 效果：附近的村民会跟随玩家，并显示爱心和绿宝石粒子
     * 时长：8分钟 (9600 ticks)
     */
    public static final RegistryObject<Potion> LONG_RICH = POTIONS.register(
            "long_rich",
            () -> new Potion(new MobEffectInstance(ModEffects.RICH.get(), 9600))
    );

    /**
     * 富有药水 - 强效版本
     * 效果：附近的村民会跟随玩家，并显示爱心和绿宝石粒子
     * 时长：1分30秒 (1800 ticks)，但效果等级更高
     */
    public static final RegistryObject<Potion> STRONG_RICH = POTIONS.register(
            "strong_rich",
            () -> new Potion(new MobEffectInstance(ModEffects.RICH.get(), 1800, 1))
    );

    /**
     * 注册所有药水
     *
     * @param eventBus Forge事件总线
     */
    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }

    /**
     * 获取药水配方
     * 此方法只返回配方数据，实际注册在Curiosities类的commonSetup中完成
     */
    public static void registerPotionRecipes() {
        // 普通版本：尴尬的药水 + 绿宝石块 -> 富有药水
        PotionBrewingRecipes.addMix(Potions.AWKWARD, Items.EMERALD_BLOCK, ModPotions.RICH.get());

        // 长效版本：普通富有药水 + 红石 -> 长效富有药水
        PotionBrewingRecipes.addMix(ModPotions.RICH.get(), Items.REDSTONE, ModPotions.LONG_RICH.get());

        // 强效版本：普通富有药水 + 荧石粉 -> 强效富有药水
        PotionBrewingRecipes.addMix(ModPotions.RICH.get(), Items.GLOWSTONE_DUST, ModPotions.STRONG_RICH.get());
    }
} 