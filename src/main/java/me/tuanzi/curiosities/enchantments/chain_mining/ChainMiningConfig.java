package me.tuanzi.curiosities.enchantments.chain_mining;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 连锁挖掘配置类
 * 使用Forge配置系统管理模组配置
 */
public class ChainMiningConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // 配置构建器
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 通用配置规格，用于注册
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // 配置项：连锁挖掘是否启用
    public static final ForgeConfigSpec.BooleanValue CHAIN_MINING_ENABLED;
    
    // 配置项：最大连锁挖掘方块数
    public static final ForgeConfigSpec.IntValue MAX_BLOCKS;
    
    // 配置项：每级连锁挖掘附魔方块数
    public static final ForgeConfigSpec.IntValue BLOCKS_PER_LEVEL;
    
    // 静态初始化块，定义配置结构
    static {
        LOGGER.info("正在初始化连锁挖矿配置...");
        
        // 创建配置分类
        BUILDER.comment("连锁挖矿配置").push("chain_mining");
        
        // 定义启用状态配置
        CHAIN_MINING_ENABLED = BUILDER
                .comment("是否启用连锁挖矿 (true/false)")
                .define("enabled", true);
        
        // 定义最大连锁方块数
        MAX_BLOCKS = BUILDER
                .comment("一次可以连锁挖掘的最大方块数")
                .defineInRange("max_blocks", 64, Integer.MIN_VALUE, Integer.MAX_VALUE);
                
        // 定义每级附魔增加的方块数
        BLOCKS_PER_LEVEL = BUILDER
                .comment("每级连锁挖矿附魔增加的可挖掘方块数")
                .defineInRange("blocks_per_level", 16, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        // 结束配置分类
        BUILDER.pop();
        
        // 构建配置规格
        COMMON_SPEC = BUILDER.build();
        
        LOGGER.info("连锁挖矿配置初始化完成");
    }
    
    /**
     * 获取连锁挖掘是否被启用
     * @return 连锁挖掘启用状态
     */
    public static boolean isChainMiningEnabled() {
        try {
            return CHAIN_MINING_ENABLED.get();
        } catch (IllegalStateException e) {
            return true;  // 配置未加载时使用默认值
        }
    }
    
    /**
     * 获取连锁挖掘最大块数
     * @return 最大连锁方块数
     */
    public static int getMaxChainBlocks() {
        try {
            return MAX_BLOCKS.get();
        } catch (IllegalStateException e) {
            return 64;  // 配置未加载时使用默认值
        }
    }
    
    /**
     * 获取每级连锁挖掘方块数
     * @return 每级连锁方块数
     */
    public static int getBlocksPerLevel() {
        try {
            return BLOCKS_PER_LEVEL.get();
        } catch (IllegalStateException e) {
            return 16;  // 配置未加载时使用默认值
        }
    }
}