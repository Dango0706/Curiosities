package me.tuanzi.curiosities.enchantments.moral_balance;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 道德天平附魔配置类
 * 控制道德天平附魔的启用/禁用状态
 */
public class MoralBalanceConfig {
    // 配置构建器
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    // 公共配置规范
    public static final ForgeConfigSpec COMMON_SPEC;
    
    // 是否启用道德天平附魔
    public static final ForgeConfigSpec.BooleanValue MORAL_BALANCE_ENABLED;
    
    static {
        BUILDER.comment("道德天平附魔配置");
        BUILDER.push("general");
        
        // 配置道德天平附魔是否启用
        MORAL_BALANCE_ENABLED = BUILDER
                .comment("是否启用道德天平附魔")
                .define("moralBalanceEnabled", true);
        
        BUILDER.pop();
        
        // 构建配置规范
        COMMON_SPEC = BUILDER.build();
    }
    
    /**
     * 检查道德天平附魔是否启用
     * 
     * @return 是否启用
     */
    public static boolean isMoralBalanceEnabled() {
        return MORAL_BALANCE_ENABLED.get();
    }
} 