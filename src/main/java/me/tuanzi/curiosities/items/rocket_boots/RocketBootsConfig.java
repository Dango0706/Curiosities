package me.tuanzi.curiosities.items.rocket_boots;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 火箭跳跃靴子配置类
 * 用于配置火箭跳跃靴子的各种属性
 */
public class RocketBootsConfig {
    // 日志记录器
    private static final Logger LOGGER = LogManager.getLogger();
    
    // 配置构建器
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 通用配置规格，用于注册
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // 配置项：火箭靴是否启用
    public static final ForgeConfigSpec.BooleanValue ROCKET_BOOTS_ENABLED;
    
    // 配置项：最大跳跃高度（方块数）
    public static final ForgeConfigSpec.DoubleValue MAX_JUMP_HEIGHT;
    
    // 配置项：每次跳跃消耗的燃素
    public static final ForgeConfigSpec.IntValue FUEL_CONSUMPTION;
    
    // 配置项：最大燃素存储量
    public static final ForgeConfigSpec.IntValue MAX_FUEL_STORAGE;
    
    // 静态初始化块，定义配置结构
    static {
        LOGGER.info("正在初始化火箭靴配置...");
        
        // 创建配置分类
        BUILDER.comment("火箭靴配置").push("rocket_boots");
        
        // 定义启用状态配置
        ROCKET_BOOTS_ENABLED = BUILDER
                .comment("是否启用火箭靴 (true/false)")
                .define("enabled", true);
        
        // 定义最大跳跃高度
        MAX_JUMP_HEIGHT = BUILDER
                .comment("火箭靴允许的最大跳跃高度 (以方块为单位)")
                .defineInRange("max_jump_height", 2.0, Double.MIN_VALUE, Double.MAX_VALUE);
        
        // 定义每次跳跃的燃料消耗
        FUEL_CONSUMPTION = BUILDER
                .comment("每次跳跃消耗的燃料量")
                .defineInRange("fuel_consumption", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        // 定义最大燃料存储量
        MAX_FUEL_STORAGE = BUILDER
                .comment("火箭靴最大燃料存储量")
                .defineInRange("max_fuel_storage", 100, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        // 结束配置分类
        BUILDER.pop();
        
        // 构建配置规格
        COMMON_SPEC = BUILDER.build();
        
        LOGGER.info("火箭靴配置初始化完成");
    }
    
    /**
     * 获取火箭靴是否被启用
     * @return 启用状态
     */
    public static boolean isRocketBootsEnabled() {
        try {
            boolean enabled = ROCKET_BOOTS_ENABLED.get();
            LOGGER.debug("获取火箭靴启用状态: {}", enabled);
            return enabled;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取火箭靴启用状态失败，使用默认值: true", e);
            return true;  // 配置未加载时使用默认值
        }
    }
    
    /**
     * 获取最大跳跃高度
     * @return 跳跃高度（方块数）
     */
    public static float getMaxJumpHeight() {
        try {
            float height = MAX_JUMP_HEIGHT.get().floatValue();
            LOGGER.debug("获取最大跳跃高度: {}", height);
            return height;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取最大跳跃高度失败，使用默认值: 2.0", e);
            return 2.0f;  // 默认值
        }
    }
    
    /**
     * 获取每次跳跃消耗的燃素
     * @return 燃素消耗量
     */
    public static int getFuelConsumption() {
        try {
            int consumption = FUEL_CONSUMPTION.get();
            LOGGER.debug("获取燃素消耗量: {}", consumption);
            return consumption;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取燃素消耗量失败，使用默认值: 10", e);
            return 10;  // 默认值
        }
    }
    
    /**
     * 获取最大燃素存储量
     * @return 最大燃素存储量
     */
    public static int getMaxFuelStorage() {
        try {
            int storage = MAX_FUEL_STORAGE.get();
            LOGGER.debug("获取最大燃素存储量: {}", storage);
            return storage;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取最大燃素存储量失败，使用默认值: 100", e);
            return 100;  // 默认值
        }
    }
} 