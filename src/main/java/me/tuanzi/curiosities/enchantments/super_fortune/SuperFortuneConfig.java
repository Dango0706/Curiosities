package me.tuanzi.curiosities.enchantments.super_fortune;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 超级时运配置类
 * 用于配置超级时运附魔的启用/禁用状态
 */
public class SuperFortuneConfig {
    // 配置构建器
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 通用配置规格，用于注册
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // 配置项：超级时运是否启用
    public static final ForgeConfigSpec.BooleanValue SUPER_FORTUNE_ENABLED;
    
    // 静态初始化块，定义配置结构
    static {
        // 创建配置分类
        BUILDER.comment("超级时运配置").push("super_fortune");
        
        // 定义超级时运启用状态配置
        SUPER_FORTUNE_ENABLED = BUILDER
                .comment("是否启用超级时运附魔 (true/false)")
                .define("enabled", true);
        
        // 结束配置分类
        BUILDER.pop();
        
        // 构建配置规格
        COMMON_SPEC = BUILDER.build();
    }
    
    /**
     * 获取超级时运是否被启用
     * @return 超级时运启用状态
     */
    public static boolean isSuperFortuneEnabled() {
        try {
            return SUPER_FORTUNE_ENABLED.get();
        } catch (IllegalStateException e) {
            return true;  // 配置未加载时使用默认值
        }
    }
} 