package me.tuanzi.curiosities.items;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 狼牙土豆配置类
 * 用于配置狼牙土豆物品的启用/禁用状态
 */
public class WolfFangPotatoConfig {
    // 配置构建器
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 通用配置规格，用于注册
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // 配置项：狼牙土豆是否启用
    public static final ForgeConfigSpec.BooleanValue WOLF_FANG_POTATO_ENABLED;
    
    // 静态初始化块，定义配置结构
    static {
        // 创建配置分类
        BUILDER.comment("狼牙土豆配置").push("wolf_fang_potato");
        
        // 定义启用状态配置
        WOLF_FANG_POTATO_ENABLED = BUILDER
                .comment("是否启用狼牙土豆物品 (true/false)")
                .define("enabled", true);
        
        // 结束配置分类
        BUILDER.pop();
        
        // 构建配置规格
        COMMON_SPEC = BUILDER.build();
    }
    
    /**
     * 获取狼牙土豆是否被启用
     * @return 狼牙土豆启用状态
     */
    public static boolean isWolfFangPotatoEnabled() {
        try {
            return WOLF_FANG_POTATO_ENABLED.get();
        } catch (IllegalStateException e) {
            return true;  // 配置未加载时使用默认值
        }
    }
} 