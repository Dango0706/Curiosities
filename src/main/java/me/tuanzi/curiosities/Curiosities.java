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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

                        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                        net.minecraft.world.item.EnchantedBookItem.addEnchantment(
                                enchantedBook,
                                new EnchantmentInstance(ModEnchantments.MORAL_BALANCE.get(), 1)
                        );
                        output.accept(enchantedBook);


                        // 添加狼牙土豆

                        output.accept(ModItems.WOLF_FANG_POTATO.get());


                        // 添加火箭靴
                        output.accept(ModItems.ROCKET_BOOTS.get());


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


                        // 添加蝙蝠翅膀
                        output.accept(ModItems.BAT_WING.get());


                        // 添加尖叫派
                        output.accept(ModItems.SCREAMING_PIE.get());


                        // 添加蜜蜂手雷
                        output.accept(ModItems.BEE_GRENADE.get());

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
                
                // 配置加载完成后注册配置界面
                BiFunction<Minecraft, Screen, Screen> screenFactory = SimpleConfigScreen::create;
                ModLoadingContext.get().registerExtensionPoint(
                        ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory(screenFactory)
                );
                LOGGER.info("配置界面已注册");
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
            // 注册狼牙土豆合成配方条件
            registerRecipeConditions();

            // 注册魔法书材料类型
            registerIngredientTypes();

            // 注册狼牙土豆合成配方
            registerWolfFangPotatoRecipe();
        });
        LOGGER.info("通用设置完成");
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
        LOGGER.info("注册配方条件完成");
    }

    /**
     * 注册自定义材料类型
     */
    private void registerIngredientTypes() {
        // 注册附魔书材料类型
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
