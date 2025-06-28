package me.tuanzi.curiosities;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.blocks.ModBlocks;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.config.SimpleConfigScreen;
import me.tuanzi.curiosities.crafting.EnchantedBookIngredient;
import me.tuanzi.curiosities.effect.ModEffects;
import me.tuanzi.curiosities.enchantments.ModEnchantments;
import me.tuanzi.curiosities.enchantments.chain_mining.ChainMiningEventHandler;
import me.tuanzi.curiosities.enchantments.chain_mining.ChainMiningState;
import me.tuanzi.curiosities.entities.ModEntities;
import me.tuanzi.curiosities.items.ModItems;
import me.tuanzi.curiosities.loot.ModLootModifiers;
import me.tuanzi.curiosities.potion.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.function.BiFunction;

/**
 * 趣味物品模组主类
 * 包含连锁挖掘功能和相关附魔
 */
@Mod(Curiosities.MODID)
public class Curiosities {
    // 模组ID
    public static final String MODID = "curiosities";
    // 物品注册表 (现在从ModItems导入)
    public static final DeferredRegister<Item> ITEMS = ModItems.ITEMS;
    // 创造模式标签页注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    /**
     * 趣味物品创造标签页
     * 包含连锁挖掘附魔书（1-4级）和其他模组物品
     */
    public static final RegistryObject<CreativeModeTab> FUN_ITEMS_TAB = CREATIVE_MODE_TABS.register(
            "fun_items_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.curiosities.fun_items"))
                    .icon(() -> new ItemStack(ModItems.WOLF_FANG_POTATO.get()))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .displayItems((parameters, output) -> {
                        // 添加超级时运附魔书

                        // 添加所有等级的超级时运附魔书（1-3级）
                        for (int level = 1; level <= 3; level++) {
                            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantedBookItem.addEnchantment(
                                    enchantedBook,
                                    new EnchantmentInstance(ModEnchantments.SUPER_FORTUNE.get(), level)
                            );
                            output.accept(enchantedBook);
                        }


                        // 添加连锁挖矿附魔书
                        // 添加所有等级的连锁挖矿附魔书（1-4级）
                        for (int level = 1; level <= 4; level++) {
                            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantedBookItem.addEnchantment(
                                    enchantedBook,
                                    new EnchantmentInstance(ModEnchantments.CHAIN_MINING.get(), level)
                            );
                            output.accept(enchantedBook);
                        }


                        // 添加道德天平附魔书
                        ItemStack moralBalanceBook = new ItemStack(Items.ENCHANTED_BOOK);
                        net.minecraft.world.item.EnchantedBookItem.addEnchantment(
                                moralBalanceBook,
                                new EnchantmentInstance(ModEnchantments.MORAL_BALANCE.get(), 1)
                        );
                        output.accept(moralBalanceBook);


                        // 添加熟练附魔书
                        // 添加所有等级的熟练附魔书（1-4级）
                        for (int level = 1; level <= 4; level++) {
                            ItemStack proficiencyBook = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantedBookItem.addEnchantment(
                                    proficiencyBook,
                                    new EnchantmentInstance(ModEnchantments.PROFICIENCY.get(), level)
                            );
                            output.accept(proficiencyBook);
                        }

                        // 添加钢契附魔书
                        // 添加所有等级的钢契附魔书（1-5级）
                        for (int level = 1; level <= 5; level++) {
                            ItemStack steelCovenantBook = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantedBookItem.addEnchantment(
                                    steelCovenantBook,
                                    new EnchantmentInstance(ModEnchantments.STEEL_COVENANT.get(), level)
                            );
                            output.accept(steelCovenantBook);
                        }

                        // 添加狼牙土豆
                        output.accept(ModItems.WOLF_FANG_POTATO.get());


                        // 添加火箭靴
                        output.accept(ModItems.ROCKET_BOOTS.get());

                        //添加满燃料的火箭靴
                        ItemStack fullFuelRocketBoots = new ItemStack(ModItems.ROCKET_BOOTS.get());
                        CompoundTag rocketBootsTag = fullFuelRocketBoots.getOrCreateTag();
                        rocketBootsTag.putInt("RocketFuel", ModConfigManager.ROCKET_BOOTS_MAX_FUEL.get()); // 设置满燃料
                        output.accept(fullFuelRocketBoots);

                        // 添加镰刀物品

                        output.accept(ModItems.WOODEN_SCYTHE.get());
                        output.accept(ModItems.STONE_SCYTHE.get());
                        output.accept(ModItems.IRON_SCYTHE.get());
                        output.accept(ModItems.GOLDEN_SCYTHE.get());
                        output.accept(ModItems.DIAMOND_SCYTHE.get());
                        output.accept(ModItems.NETHERITE_SCYTHE.get());


                        // 添加假TNT方块

                        output.accept(ModBlocks.FAKE_TNT_ITEM.get());


                        // 添加幸运剑
                        output.accept(ModItems.LUCKY_SWORD.get());


                        // 添加虚空吞噬之剑
                        output.accept(ModItems.VOID_SWORD.get());

                        // 添加满能量的虚空吞噬之剑用于测试
                        ItemStack fullEnergySword = new ItemStack(ModItems.VOID_SWORD.get());
                        CompoundTag tag = fullEnergySword.getOrCreateTag();
                        tag.putInt("VoidEnergy", 1000); // 设置满能量
                        output.accept(fullEnergySword);


                        // 添加蝙蝠翅膀
                        output.accept(ModItems.BAT_WING.get());


                        // 添加尖叫派
                        output.accept(ModItems.SCREAMING_PIE.get());


                        // 添加蜜蜂手雷
                        output.accept(ModItems.BEE_GRENADE.get());

                        // 添加控制之杖
                        output.accept(ModItems.CONTROL_STAFF.get());

                        //添加时空卷轴
                        output.accept(ModItems.SCROLL_OF_SPACETIME.get());

                        //添加无限水桶
                        output.accept(ModItems.INFINITE_WATER_BUCKET.get());

                        //添加涡毒腺体
                        output.accept(ModItems.TOXIC_GLAND.get());

                        //添加概率圣剑
                        output.accept(ModItems.PROBABILITY_HOLY_SWORD.get());

                        // 添加富有药水
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_RICH.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.LONG_RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.STRONG_RICH.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.LONG_RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.STRONG_RICH.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.LONG_RICH.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.STRONG_RICH.get()));


                        // 添加混乱药水
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_CONFUSION.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.LONG_CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.STRONG_CONFUSION.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.LONG_CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.STRONG_CONFUSION.get()));

                        // 添加混乱箭
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.LONG_CONFUSION.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.STRONG_CONFUSION.get()));

                        // 添加不死药水
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.UNDYING.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_UNDYING.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.UNDYING.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.LONG_UNDYING.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.UNDYING.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.LONG_UNDYING.get()));

                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.UNDYING.get()));
                        output.accept(PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), ModPotions.LONG_UNDYING.get()));

                    })
                    .build()
    );
    // 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 模组初始化
     */
    public Curiosities() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册配置
        ModConfigManager.registerConfigs();

        // 等待配置加载完成
        modEventBus.addListener(this::onConfigLoad);

        // 注册网络处理器
        me.tuanzi.curiosities.network.PacketHandler.register();

        // 注册通用初始化事件
        modEventBus.addListener(this::commonSetup);

        // 注册物品和创造标签页
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // 注册方块和方块物品
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ITEMS.register(modEventBus);

        // 注册实体类型
        ModEntities.ENTITIES.register(modEventBus);

        // 注册附魔
        ModEnchantments.ENCHANTMENTS.register(modEventBus);

        // 注册状态效果
        ModEffects.MOB_EFFECTS.register(modEventBus);

        // 注册药水
        ModPotions.POTIONS.register(modEventBus);

        // 注册战利品修改器
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);

        // 注册事件监听器
        registerEventListeners();

        LOGGER.info("mod初始化完成");
    }

    /**
     * 配置加载完成事件
     */
    private void onConfigLoad(FMLCommonSetupEvent event) {
        // 等待配置加载完成
        event.enqueueWork(() -> {
            try {
                // 尝试访问配置值，确保配置已加载
                ModConfigManager.SCYTHE_ATTACK_SPEED.get();
                ModConfigManager.SCYTHE_DAMAGE_BONUS.get();

                // 标记配置已加载
                ModConfigManager.setConfigLoaded(true);

                // 配置加载完成后，仅在物理客户端注册配置界面
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    // 客户端代码
                    BiFunction<Minecraft, Screen, Screen> screenFactory = SimpleConfigScreen::create;
                    ModLoadingContext.get().registerExtensionPoint(
                            ConfigScreenHandler.ConfigScreenFactory.class,
                            () -> new ConfigScreenHandler.ConfigScreenFactory(screenFactory)
                    );
                    LOGGER.info("配置界面已注册");
                });
            } catch (IllegalStateException e) {
                LOGGER.error("配置加载失败，无法注册配置界面", e);
            }
        });
    }

    /**
     * 注册事件监听器
     */
    private void registerEventListeners() {
        // 注册通用事件监听器
        MinecraftForge.EVENT_BUS.register(this);

        // 注册连锁挖掘事件处理器
        MinecraftForge.EVENT_BUS.register(ChainMiningEventHandler.class);

        // 注册道德天平事件处理器
        MinecraftForge.EVENT_BUS.register(me.tuanzi.curiosities.enchantments.moral_balance.MoralBalanceEventHandler.class);
    }

    /**
     * 通用初始化
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 注册合成配方条件
            registerRecipeConditions();

            // 注册魔法书材料类型
            registerIngredientTypes();

            // 注册狼牙土豆合成配方
            registerWolfFangPotatoRecipe();

            // 注册药水酿造配方
            registerBrewingRecipes();
        });
        LOGGER.info("通用设置完成");
    }

    /**
     * 注册药水酿造配方
     */
    private void registerBrewingRecipes() {
        // 富有药水酿造配方
        // 普通版本：尴尬的药水 + 绿宝石块 -> 富有药水
        ItemStack awkwardInput = PotionUtils.setPotion(new ItemStack(Items.POTION), net.minecraft.world.item.alchemy.Potions.AWKWARD);
        ItemStack richOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.RICH.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(awkwardInput), Ingredient.of(Items.EMERALD_BLOCK), richOutput);

        // 长效版本：普通富有药水 + 红石 -> 长效富有药水
        ItemStack richInput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.RICH.get());
        ItemStack longRichOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_RICH.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(richInput), Ingredient.of(Items.REDSTONE), longRichOutput);

        // 强效版本：普通富有药水 + 荧石粉 -> 强效富有药水
        ItemStack strongRichOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_RICH.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(richInput), Ingredient.of(Items.GLOWSTONE_DUST), strongRichOutput);

        // 喷溅型药水配方
        ItemStack richSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.RICH.get());
        ItemStack longRichSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.LONG_RICH.get());
        ItemStack strongRichSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.STRONG_RICH.get());

        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), net.minecraft.world.item.alchemy.Potions.AWKWARD)),
                Ingredient.of(Items.EMERALD_BLOCK), richSplashOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.RICH.get())),
                Ingredient.of(Items.REDSTONE), longRichSplashOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.RICH.get())),
                Ingredient.of(Items.GLOWSTONE_DUST), strongRichSplashOutput);

        // 滞留型药水配方
        ItemStack richLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.RICH.get());
        ItemStack longRichLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.LONG_RICH.get());
        ItemStack strongRichLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.STRONG_RICH.get());

        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), net.minecraft.world.item.alchemy.Potions.AWKWARD)),
                Ingredient.of(Items.EMERALD_BLOCK), richLingeringOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.RICH.get())),
                Ingredient.of(Items.REDSTONE), longRichLingeringOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.RICH.get())),
                Ingredient.of(Items.GLOWSTONE_DUST), strongRichLingeringOutput);

        LOGGER.info("已注册富有药水的酿造配方");

        // 混乱药水酿造配方
        // 普通版本：尴尬的药水 + 涡毒腺体 -> 混乱药水
        ItemStack confusionOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CONFUSION.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(awkwardInput), Ingredient.of(ModItems.TOXIC_GLAND.get()), confusionOutput);

        // 长效版本：普通混乱药水 + 红石 -> 长效混乱药水
        ItemStack confusionInput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CONFUSION.get());
        ItemStack longConfusionOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_CONFUSION.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(confusionInput), Ingredient.of(Items.REDSTONE), longConfusionOutput);

        // 强效版本：普通混乱药水 + 荧石粉 -> 强效混乱药水
        ItemStack strongConfusionOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_CONFUSION.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(confusionInput), Ingredient.of(Items.GLOWSTONE_DUST), strongConfusionOutput);

        // 噴溅型药水配方
        ItemStack confusionSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.CONFUSION.get());
        ItemStack longConfusionSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.LONG_CONFUSION.get());
        ItemStack strongConfusionSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.STRONG_CONFUSION.get());

        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), net.minecraft.world.item.alchemy.Potions.AWKWARD)),
                Ingredient.of(ModItems.TOXIC_GLAND.get()), confusionSplashOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.CONFUSION.get())),
                Ingredient.of(Items.REDSTONE), longConfusionSplashOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.CONFUSION.get())),
                Ingredient.of(Items.GLOWSTONE_DUST), strongConfusionSplashOutput);

        // 滞留型药水配方
        ItemStack confusionLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.CONFUSION.get());
        ItemStack longConfusionLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.LONG_CONFUSION.get());
        ItemStack strongConfusionLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.STRONG_CONFUSION.get());

        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), net.minecraft.world.item.alchemy.Potions.AWKWARD)),
                Ingredient.of(ModItems.TOXIC_GLAND.get()), confusionLingeringOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.CONFUSION.get())),
                Ingredient.of(Items.REDSTONE), longConfusionLingeringOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.CONFUSION.get())),
                Ingredient.of(Items.GLOWSTONE_DUST), strongConfusionLingeringOutput);

        LOGGER.info("已注册混乱药水的酿造配方");

        // 不死药水酿造配方
        // 普通版本：尴尬的药水 + 不死图腾 -> 不死药水
        ItemStack undyingOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.UNDYING.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(awkwardInput), Ingredient.of(Items.TOTEM_OF_UNDYING), undyingOutput);

        // 长效版本：普通不死药水 + 红石 -> 长效不死药水
        ItemStack undyingInput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.UNDYING.get());
        ItemStack longUndyingOutput = PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_UNDYING.get());
        BrewingRecipeRegistry.addRecipe(Ingredient.of(undyingInput), Ingredient.of(Items.REDSTONE), longUndyingOutput);

        // 喷溅型药水配方
        ItemStack undyingSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.UNDYING.get());
        ItemStack longUndyingSplashOutput = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.LONG_UNDYING.get());

        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), net.minecraft.world.item.alchemy.Potions.AWKWARD)),
                Ingredient.of(Items.TOTEM_OF_UNDYING), undyingSplashOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), ModPotions.UNDYING.get())),
                Ingredient.of(Items.REDSTONE), longUndyingSplashOutput);

        // 滞留型药水配方
        ItemStack undyingLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.UNDYING.get());
        ItemStack longUndyingLingeringOutput = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.LONG_UNDYING.get());

        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), net.minecraft.world.item.alchemy.Potions.AWKWARD)),
                Ingredient.of(Items.TOTEM_OF_UNDYING), undyingLingeringOutput);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), ModPotions.UNDYING.get())),
                Ingredient.of(Items.REDSTONE), longUndyingLingeringOutput);

        LOGGER.info("已注册不死药水的酿造配方");
    }

    /**
     * 注册配方条件
     */
    private void registerRecipeConditions() {
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.WolfFangPotatoEnabledCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.RocketBootsEnabledCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.MoralBalanceEnabledCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.ScytheEnabledCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.FakeTntEnabledCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.LuckySwordEnabledCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.ScreamingPieEnabledCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.ControlStaffCraftableCondition.Serializer());
        CraftingHelper.register(new me.tuanzi.curiosities.crafting.ProbabilityHolySwordEnabledCondition.Serializer());
        LOGGER.info("注册配方条件完成");
    }

    /**
     * 注册自定义材料类型
     */
    private void registerIngredientTypes() {
        CraftingHelper.register(
                EnchantedBookIngredient.TYPE,
                EnchantedBookIngredient.Serializer.INSTANCE
        );

        LOGGER.info("附魔书材料类型已注册");
    }

    /**
     * 注册狼牙土豆合成配方
     * 配方：烤土豆×1 + 骨头×2 + 岩浆膏×1
     */
    private void registerWolfFangPotatoRecipe() {
        // 这个方法不直接实现具体配方，仅做标记
        // 实际配方通过数据包中的JSON配方文件实现
        LOGGER.info("狼牙土豆合成配方已设置");
    }

    /**
     * 服务器启动事件处理
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("服务器启动中，模组功能已启用");
    }

    /**
     * 玩家登出事件处理
     * 清除玩家的连锁挖掘状态
     */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ChainMiningState.clearPlayerChainMiningState(event.getEntity().getUUID());
    }

    /**
     * 玩家登录事件处理
     * 向玩家发送服务器配置
     */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 确保在服务器端
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        // 获取服务器玩家对象
        net.minecraft.server.level.ServerPlayer serverPlayer = (net.minecraft.server.level.ServerPlayer) event.getEntity();
        LOGGER.info("玩家 {} 登录，发送配置同步数据包", serverPlayer.getName().getString());

        // 创建配置同步数据包并发送
        me.tuanzi.curiosities.network.PacketSyncConfig configPacket = new me.tuanzi.curiosities.network.PacketSyncConfig();
        me.tuanzi.curiosities.network.PacketHandler.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                configPacket
        );
    }

    /**
     * 服务器停止事件处理
     * 清除所有玩家的连锁挖掘状态
     */
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ChainMiningState.clearAllPlayerChainMiningState();
    }

    /**
     * 客户端事件处理器
     */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        /**
         * 客户端初始化
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("客户端设置完成，按键已注册");
        }
    }
}
