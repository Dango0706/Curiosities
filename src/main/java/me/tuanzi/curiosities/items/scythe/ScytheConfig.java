package me.tuanzi.curiosities.items.scythe;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 镰刀工具配置类
 * 用于配置镰刀的各种属性
 */
public class ScytheConfig {
    // 日志记录器
    private static final Logger LOGGER = LogManager.getLogger();
    
    // 配置构建器
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 通用配置规格，用于注册
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // 配置项：镰刀是否启用
    public static final ForgeConfigSpec.BooleanValue SCYTHE_ENABLED;
    
    // 配置项：镰刀攻击速度
    public static final ForgeConfigSpec.DoubleValue ATTACK_SPEED;
    
    // 配置项：镰刀攻击伤害比剑高的数值
    public static final ForgeConfigSpec.DoubleValue DAMAGE_BONUS;
    
    // 配置项：横扫范围比剑多的数值
    public static final ForgeConfigSpec.DoubleValue SWEEP_RANGE_BONUS;
    
    // 配置项：丰收之舞触发概率
    public static final ForgeConfigSpec.DoubleValue HARVEST_DANCE_CHANCE;
    
    // 配置项：丰收之舞范围
    public static final ForgeConfigSpec.IntValue HARVEST_DANCE_RANGE;
    
    // 配置项：右键收获农作物范围
    public static final ForgeConfigSpec.IntValue HARVEST_RANGE;
    
    // 静态初始化块，定义配置结构
    static {
        LOGGER.info("正在初始化镰刀配置...");
        
        // 创建配置分类
        BUILDER.comment("镰刀工具配置").push("scythe");
        
        // 定义启用状态配置
        SCYTHE_ENABLED = BUILDER
                .comment("是否启用镰刀工具 (true/false)")
                .define("enabled", true);
        
        // 定义攻击速度
        ATTACK_SPEED = BUILDER
                .comment("镰刀的攻击速度 (值越大攻击速度越快)")
                .defineInRange("attack_speed", 1.0, Double.MIN_VALUE, Double.MAX_VALUE);
        
        // 定义攻击伤害加成
        DAMAGE_BONUS = BUILDER
                .comment("镰刀比同级别剑多的伤害值")
                .defineInRange("damage_bonus", 1.0, Double.MIN_VALUE, Double.MAX_VALUE);
        
        // 定义横扫范围加成
        SWEEP_RANGE_BONUS = BUILDER
                .comment("镰刀比剑多的横扫范围")
                .defineInRange("sweep_range_bonus", 0.5, Double.MIN_VALUE, Double.MAX_VALUE);
        
        // 定义丰收之舞触发概率
        HARVEST_DANCE_CHANCE = BUILDER
                .comment("触发丰收之舞的概率 (0.0-1.0)")
                .defineInRange("harvest_dance_chance", 0.1, Double.MIN_VALUE, Double.MAX_VALUE);
        
        // 定义丰收之舞范围
        HARVEST_DANCE_RANGE = BUILDER
                .comment("丰收之舞影响的范围 (以方块为单位的半径)")
                .defineInRange("harvest_dance_range", 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        // 定义收获范围
        HARVEST_RANGE = BUILDER
                .comment("右键收获农作物的范围 (以方块为单位的半径)")
                .defineInRange("harvest_range", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
        // 结束配置分类
        BUILDER.pop();
        
        // 构建配置规格
        COMMON_SPEC = BUILDER.build();
        
        LOGGER.info("镰刀配置初始化完成");
    }
    
    /**
     * 获取镰刀是否被启用
     * @return 镰刀启用状态
     */
    public static boolean isScytheEnabled() {
        try {
            boolean enabled = SCYTHE_ENABLED.get();
            LOGGER.debug("获取镰刀启用状态: {}", enabled);
            return enabled;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取镰刀启用状态失败，使用默认值: true", e);
            return true;  // 配置未加载时使用默认值
        }
    }
    
    /**
     * 获取镰刀攻击速度
     * @return 攻击速度值
     */
    public static double getAttackSpeed() {
        try {
            double speed = ATTACK_SPEED.get();
            LOGGER.debug("获取镰刀攻击速度: {}", speed);
            return speed;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取镰刀攻击速度失败，使用默认值: 1.0", e);
            return 1.0;  // 默认值
        }
    }
    
    /**
     * 获取镰刀攻击伤害加成
     * @return 攻击伤害加成值
     */
    public static double getDamageBonus() {
        try {
            double bonus = DAMAGE_BONUS.get();
            LOGGER.debug("获取镰刀伤害加成: {}", bonus);
            return bonus;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取镰刀伤害加成失败，使用默认值: 1.0", e);
            return 1.0;  // 默认值
        }
    }
    
    /**
     * 获取镰刀横扫范围加成
     * @return 横扫范围加成值
     */
    public static double getSweepRangeBonus() {
        try {
            double bonus = SWEEP_RANGE_BONUS.get();
            LOGGER.debug("获取镰刀横扫范围加成: {}", bonus);
            return bonus;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取镰刀横扫范围加成失败，使用默认值: 0.5", e);
            return 0.5;  // 默认值
        }
    }
    
    /**
     * 获取丰收之舞触发概率
     * @return 触发概率值
     */
    public static double getHarvestDanceChance() {
        try {
            double chance = HARVEST_DANCE_CHANCE.get();
            LOGGER.debug("获取丰收之舞触发概率: {}", chance);
            return chance;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取丰收之舞触发概率失败，使用默认值: 0.05", e);
            return 0.05;  // 默认值
        }
    }
    
    /**
     * 获取丰收之舞范围
     * @return 范围值
     */
    public static int getHarvestDanceRange() {
        try {
            int range = HARVEST_DANCE_RANGE.get();
            LOGGER.debug("获取丰收之舞范围: {}", range);
            return range;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取丰收之舞范围失败，使用默认值: 2", e);
            return 2;  // 默认值
        }
    }
    
    /**
     * 获取收获范围
     * @return 范围值
     */
    public static int getHarvestRange() {
        try {
            int range = HARVEST_RANGE.get();
            LOGGER.debug("获取收获范围: {}", range);
            return range;
        } catch (IllegalStateException e) {
            LOGGER.warn("获取收获范围失败，使用默认值: 1", e);
            return 1;  // 默认值
        }
    }
} 