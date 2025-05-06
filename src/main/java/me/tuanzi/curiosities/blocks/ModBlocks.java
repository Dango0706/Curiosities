package me.tuanzi.curiosities.blocks;

import me.tuanzi.curiosities.Curiosities;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组方块注册类
 * 负责注册所有模组自定义方块
 */
public class ModBlocks {
    /**
     * 方块注册表
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Curiosities.MODID);

    /**
     * 方块物品注册表
     */
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Curiosities.MODID);

    /**
     * 假TNT方块
     * 外观与普通TNT相似，但行为不同：
     * - 左键点击点燃TNT
     * - 右键点击直接破坏方块
     */
    public static final RegistryObject<Block> FAKE_TNT = BLOCKS.register(
            "fake_tnt",
            FakeTntBlock::new
    );

    /**
     * 假TNT方块物品
     */
    public static final RegistryObject<Item> FAKE_TNT_ITEM = BLOCK_ITEMS.register(
            "fake_tnt",
            () -> new BlockItem(FAKE_TNT.get(), new Item.Properties())
    );
} 