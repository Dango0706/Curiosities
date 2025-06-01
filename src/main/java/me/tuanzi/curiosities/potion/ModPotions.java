package me.tuanzi.curiosities.potion;

import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.effect.ModEffects;
import me.tuanzi.curiosities.items.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.crafting.Ingredient;

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
     * 混乱药水 - 普通版本
     * 效果：攻击时有一定概率将目标转移为自己，并造成部分伤害
     * 时长：1分30秒 (1800 ticks)
     */
    public static final RegistryObject<Potion> CONFUSION = POTIONS.register(
            "confusion",
            () -> new Potion(new MobEffectInstance(ModEffects.CONFUSION.get(), 1800))
    );

    /**
     * 混乱药水 - 长效版本
     * 效果：攻击时有一定概率将目标转移为自己，并造成部分伤害
     * 时长：3分钟 (3600 ticks)
     */
    public static final RegistryObject<Potion> LONG_CONFUSION = POTIONS.register(
            "long_confusion",
            () -> new Potion(new MobEffectInstance(ModEffects.CONFUSION.get(), 3600))
    );

    /**
     * 混乱药水 - 强效版本
     * 效果：攻击时有一定概率将目标转移为自己，并造成部分伤害
     * 时长：45秒 (900 ticks)，但效果等级更高
     */
    public static final RegistryObject<Potion> STRONG_CONFUSION = POTIONS.register(
            "strong_confusion",
            () -> new Potion(new MobEffectInstance(ModEffects.CONFUSION.get(), 900, 1))
    );

    /**
     * 不死药水 - 普通版本
     * 效果：当受到致命伤害时，触发不死图腾的效果，并移除本效果
     * 时长：3分钟 (3600 ticks)
     */
    public static final RegistryObject<Potion> UNDYING = POTIONS.register(
            "undying",
            () -> new Potion(new MobEffectInstance(ModEffects.UNDYING.get(), 3600))
    );

    /**
     * 不死药水 - 长效版本
     * 效果：当受到致命伤害时，触发不死图腾的效果，并移除本效果
     * 时长：8分钟 (9600 ticks)
     */
    public static final RegistryObject<Potion> LONG_UNDYING = POTIONS.register(
            "long_undying",
            () -> new Potion(new MobEffectInstance(ModEffects.UNDYING.get(), 9600))
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
        // 创建各种药水的ItemStack
        ItemStack awkwardInput = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);
        
        // 富有药水配方
        // 普通版本：尴尬的药水 + 绿宝石块 -> 富有药水
        ItemStack richOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.RICH.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(awkwardInput),
                Ingredient.of(Items.EMERALD_BLOCK),
                richOutput
        );

        // 长效版本：普通富有药水 + 红石 -> 长效富有药水
        ItemStack richInput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.RICH.get());
        ItemStack longRichOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_RICH.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(richInput),
                Ingredient.of(Items.REDSTONE),
                longRichOutput
        );

        // 强效版本：普通富有药水 + 荧石粉 -> 强效富有药水
        ItemStack strongRichOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_RICH.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(richInput),
                Ingredient.of(Items.GLOWSTONE_DUST),
                strongRichOutput
        );

        // 混乱药水配方
        // 普通版本：尴尬的药水 + 涡毒腺体 -> 混乱药水
        ItemStack confusionOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CONFUSION.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(awkwardInput),
                Ingredient.of(ModItems.TOXIC_GLAND.get()),
                confusionOutput
        );

        // 长效版本：普通混乱药水 + 红石 -> 长效混乱药水
        ItemStack confusionInput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CONFUSION.get());
        ItemStack longConfusionOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_CONFUSION.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(confusionInput),
                Ingredient.of(Items.REDSTONE),
                longConfusionOutput
        );

        // 强效版本：普通混乱药水 + 荧石粉 -> 强效混乱药水
        ItemStack strongConfusionOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_CONFUSION.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(confusionInput),
                Ingredient.of(Items.GLOWSTONE_DUST),
                strongConfusionOutput
        );
        
        // 不死药水配方
        // 普通版本：尴尬的药水 + 不死图腾 -> 不死药水
        ItemStack undyingOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.UNDYING.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(awkwardInput),
                Ingredient.of(Items.TOTEM_OF_UNDYING),
                undyingOutput
        );
        
        // 长效版本：普通不死药水 + 红石 -> 长效不死药水
        ItemStack undyingInput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.UNDYING.get());
        ItemStack longUndyingOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_UNDYING.get());
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(undyingInput),
                Ingredient.of(Items.REDSTONE),
                longUndyingOutput
        );
    }
} 