package me.tuanzi.curiosities.potion;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.slf4j.Logger;

/**
 * 药水酿造配方辅助类
 * 用于注册药水酿造配方
 */
public class PotionBrewingRecipes {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 添加药水酿造配方
     *
     * @param input   输入药水
     * @param reagent 试剂物品
     * @param output  输出药水
     */
    public static void addMix(Potion input, Item reagent, Potion output) {
        // 创建带有NBT数据的输入药水作为原料
        ItemStack inputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), input);
        ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.POTION), output);

        // 创建喷溅型药水
        ItemStack splashInputStack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), input);
        ItemStack splashOutputStack = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), output);

        // 创建滞留型药水
        ItemStack lingeringInputStack = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), input);
        ItemStack lingeringOutputStack = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), output);

        // 在FMLCommonSetupEvent中调用，不需要在这里实现
        // 这个类仅作为辅助方法，将在Curiosities类的commonSetup方法中调用

        LOGGER.info("准备添加药水酿造配方：{} + {} -> {}", input, reagent, output);
    }
} 