package me.tuanzi.curiosities.config;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

/**
 * 配置管理器
 * 统一管理所有模组配置
 */
public class ModConfigManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    // 连锁挖矿配置
    public static ForgeConfigSpec.BooleanValue CHAIN_MINING_ENABLED;
    public static ForgeConfigSpec.IntValue CHAIN_MINING_MAX_BLOCKS;
    public static ForgeConfigSpec.IntValue CHAIN_MINING_BLOCKS_PER_LEVEL;
    public static ForgeConfigSpec.IntValue CHAIN_MINING_HARVEST_RANGE;
    // 超级时运配置
    public static ForgeConfigSpec.BooleanValue SUPER_FORTUNE_ENABLED;
    // 狼牙土豆配置
    public static ForgeConfigSpec.BooleanValue WOLF_FANG_POTATO_ENABLED;
    // 镰刀工具配置
    public static ForgeConfigSpec.BooleanValue SCYTHE_ENABLED;
    public static ForgeConfigSpec.DoubleValue SCYTHE_ATTACK_SPEED;
    public static ForgeConfigSpec.DoubleValue SCYTHE_DAMAGE_BONUS;
    public static ForgeConfigSpec.DoubleValue SCYTHE_HARVEST_RANGE;
    public static ForgeConfigSpec.DoubleValue SCYTHE_SWEEP_RANGE_BONUS;
    public static ForgeConfigSpec.DoubleValue SCYTHE_HARVEST_DANCE_CHANCE;
    public static ForgeConfigSpec.DoubleValue SCYTHE_HARVEST_DANCE_RANGE;
    // 火箭靴配置
    public static ForgeConfigSpec.BooleanValue ROCKET_BOOTS_ENABLED;
    public static ForgeConfigSpec.DoubleValue ROCKET_BOOTS_BOOST_POWER;
    public static ForgeConfigSpec.DoubleValue ROCKET_BOOTS_MAX_JUMP_HEIGHT;
    public static ForgeConfigSpec.IntValue ROCKET_BOOTS_FUEL_CONSUMPTION;
    public static ForgeConfigSpec.IntValue ROCKET_BOOTS_MAX_FUEL;
    // 道德天平配置
    public static ForgeConfigSpec.BooleanValue MORAL_BALANCE_ENABLED;
    // 假TNT配置
    public static ForgeConfigSpec.BooleanValue FAKE_TNT_ENABLED;
    // 幸运剑配置
    public static ForgeConfigSpec.BooleanValue LUCKY_SWORD_ENABLED;
    public static ForgeConfigSpec.DoubleValue LUCKY_SWORD_MIN_DAMAGE;
    public static ForgeConfigSpec.DoubleValue LUCKY_SWORD_MAX_DAMAGE;
    // 尖叫派配置
    public static ForgeConfigSpec.BooleanValue SCREAMING_PIE_ENABLED;
    public static ForgeConfigSpec.IntValue SCREAMING_PIE_SLOW_FALLING_DURATION;
    public static ForgeConfigSpec.IntValue SCREAMING_PIE_SCREAMING_DURATION;
    // 蝙蝠翅膀配置
    public static ForgeConfigSpec.BooleanValue BAT_WING_ENABLED;
    // 蜜蜂手雷配置
    public static ForgeConfigSpec.BooleanValue BEE_GRENADE_ENABLED;
    public static ForgeConfigSpec.IntValue BEE_GRENADE_BEE_COUNT;
    public static ForgeConfigSpec.IntValue BEE_GRENADE_BEE_LIFETIME;
    public static ForgeConfigSpec.BooleanValue BEE_GRENADE_PLAYER_FRIENDLY;
    public static ForgeConfigSpec.BooleanValue BEE_GRENADE_HONEY_SLOWNESS_AREA_ENABLED;
    public static ForgeConfigSpec.DoubleValue BEE_GRENADE_HONEY_AREA_RADIUS;
    public static ForgeConfigSpec.IntValue BEE_GRENADE_HONEY_AREA_DURATION;
    public static ForgeConfigSpec.BooleanValue BEE_GRENADE_DESTROY_BLOCKS;
    // 状态效果配置
    // 尖叫效果配置
    public static ForgeConfigSpec.BooleanValue SCREAMING_EFFECT_ENABLED;
    public static ForgeConfigSpec.IntValue SCREAMING_EFFECT_RANGE;
    // 颠颠倒倒效果配置
    public static ForgeConfigSpec.BooleanValue DIZZY_EFFECT_ENABLED;
    // 天旋地转效果配置
    public static ForgeConfigSpec.BooleanValue SPINNING_EFFECT_ENABLED;
    // 通用配置
    private static ForgeConfigSpec.Builder COMMON_BUILDER;
    private static ForgeConfigSpec COMMON_CONFIG;
    // 客户端配置
    private static ForgeConfigSpec.Builder CLIENT_BUILDER;
    private static ForgeConfigSpec CLIENT_CONFIG;
    // 服务器配置
    private static ForgeConfigSpec.Builder SERVER_BUILDER;
    private static ForgeConfigSpec SERVER_CONFIG;

    // 配置加载状态
    private static boolean configLoaded = false;

    /**
     * 检查配置是否已加载
     */
    public static boolean isConfigLoaded() {
        return configLoaded;
    }

    /**
     * 设置配置加载状态
     */
    public static void setConfigLoaded(boolean loaded) {
        configLoaded = loaded;
    }

    /**
     * 注册所有配置
     */
    public static void registerConfigs() {
        // 创建配置构建器
        COMMON_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        SERVER_BUILDER = new ForgeConfigSpec.Builder();

        // 构建通用配置
        buildCommonConfig();

        // 构建客户端配置
        buildClientConfig();

        // 构建服务器配置
        buildServerConfig();

        // 注册配置到Forge
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        context.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
        context.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);

        LOGGER.info("所有配置已注册");
    }

    /**
     * 构建通用配置
     */
    private static void buildCommonConfig() {
        COMMON_BUILDER.comment("通用配置").push("common");

        // 连锁挖矿配置
        COMMON_BUILDER.comment("连锁挖矿配置").push("chain_mining");
        CHAIN_MINING_ENABLED = COMMON_BUILDER
                .comment("是否启用连锁挖矿")
                .define("enabled", true);
        CHAIN_MINING_MAX_BLOCKS = COMMON_BUILDER
                .comment("最大连锁方块数量")
                .defineInRange("max_blocks", 64, 0, 256);
        CHAIN_MINING_BLOCKS_PER_LEVEL = COMMON_BUILDER
                .comment("每级附魔增加的方块数量")
                .defineInRange("blocks_per_level", 16, 0, 32);
        CHAIN_MINING_HARVEST_RANGE = COMMON_BUILDER
                .comment("挖掘检测范围")
                .defineInRange("harvest_range", 16, 1, 32);
        COMMON_BUILDER.pop();

        // 超级时运配置
        COMMON_BUILDER.comment("超级时运配置").push("super_fortune");
        SUPER_FORTUNE_ENABLED = COMMON_BUILDER
                .comment("是否启用超级时运附魔")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 狼牙土豆配置
        COMMON_BUILDER.comment("狼牙土豆配置").push("wolf_fang_potato");
        WOLF_FANG_POTATO_ENABLED = COMMON_BUILDER
                .comment("是否启用狼牙土豆")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 镰刀工具配置
        COMMON_BUILDER.comment("镰刀工具配置").push("scythe");
        SCYTHE_ENABLED = COMMON_BUILDER
                .comment("是否启用镰刀工具")
                .define("enabled", true);
        SCYTHE_ATTACK_SPEED = COMMON_BUILDER
                .comment("镰刀攻击速度")
                .defineInRange("attack_speed", 1.0, -4.0, 4.0);
        SCYTHE_DAMAGE_BONUS = COMMON_BUILDER
                .comment("镰刀伤害加成（相比于剑）")
                .defineInRange("damage_bonus", 1.0, 0.0, 10.0);
        SCYTHE_HARVEST_RANGE = COMMON_BUILDER
                .comment("镰刀收获范围")
                .defineInRange("harvest_range", 3.0, 1.0, 10.0);
        SCYTHE_SWEEP_RANGE_BONUS = COMMON_BUILDER
                .comment("镰刀横扫范围加成")
                .defineInRange("sweep_range_bonus", 1.0, 0.5, 5.0);
        SCYTHE_HARVEST_DANCE_CHANCE = COMMON_BUILDER
                .comment("丰收之舞触发概率")
                .defineInRange("harvest_dance_chance", 0.03, 0.0, 1.0);
        SCYTHE_HARVEST_DANCE_RANGE = COMMON_BUILDER
                .comment("丰收之舞触发范围")
                .defineInRange("harvest_dance_range", 5.0, 1.0, 20.0);
        COMMON_BUILDER.pop();

        // 火箭靴配置
        COMMON_BUILDER.comment("火箭靴配置").push("rocket_boots");
        ROCKET_BOOTS_ENABLED = COMMON_BUILDER
                .comment("是否启用火箭靴")
                .define("enabled", true);
        ROCKET_BOOTS_BOOST_POWER = COMMON_BUILDER
                .comment("火箭靴推进力")
                .defineInRange("boost_power", 1.0, 0.1, 5.0);
        ROCKET_BOOTS_MAX_JUMP_HEIGHT = COMMON_BUILDER
                .comment("最大跳跃高度（方块）")
                .defineInRange("max_jump_height", 10.0, 0.0, Double.MAX_VALUE);
        ROCKET_BOOTS_FUEL_CONSUMPTION = COMMON_BUILDER
                .comment("每次跳跃燃料消耗")
                .defineInRange("fuel_consumption", 10, 0, Integer.MAX_VALUE);
        ROCKET_BOOTS_MAX_FUEL = COMMON_BUILDER
                .comment("最大燃料储存量")
                .defineInRange("max_fuel", 200, 0, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        // 道德天平配置
        COMMON_BUILDER.comment("道德天平配置").push("moral_balance");
        MORAL_BALANCE_ENABLED = COMMON_BUILDER
                .comment("是否启用道德天平附魔")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 假TNT配置
        COMMON_BUILDER.comment("假TNT配置").push("fake_tnt");
        FAKE_TNT_ENABLED = COMMON_BUILDER
                .comment("是否启用假TNT")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 幸运剑配置
        COMMON_BUILDER.comment("幸运剑配置").push("lucky_sword");
        LUCKY_SWORD_ENABLED = COMMON_BUILDER
                .comment("是否启用幸运剑")
                .define("enabled", true);
        LUCKY_SWORD_MIN_DAMAGE = COMMON_BUILDER
                .comment("最低伤害值（可为负）")
                .defineInRange("min_damage", -15.0, -100.0, 100.0);
        LUCKY_SWORD_MAX_DAMAGE = COMMON_BUILDER
                .comment("最高伤害值")
                .defineInRange("max_damage", 30.0, -100.0, 100.0);
        COMMON_BUILDER.pop();

        // 尖叫派配置
        COMMON_BUILDER.comment("尖叫派配置").push("screaming");
        SCREAMING_PIE_ENABLED = COMMON_BUILDER
                .comment("是否启用尖叫派")
                .define("screaming_pie_enabled", true);
        BAT_WING_ENABLED = COMMON_BUILDER
                .comment("是否启用蝙蝠翅膀")
                .define("bat_wing_enabled", true);
        SCREAMING_PIE_SLOW_FALLING_DURATION = COMMON_BUILDER
                .comment("缓降效果持续时间（秒）")
                .defineInRange("slow_falling_duration", 120, 0, Integer.MAX_VALUE);
        SCREAMING_PIE_SCREAMING_DURATION = COMMON_BUILDER
                .comment("尖叫效果持续时间（秒）")
                .defineInRange("screaming_duration", 60, 0, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();

        // 蜜蜂手雷配置
        COMMON_BUILDER.comment("蜜蜂手雷配置").push("bee_grenade");
        BEE_GRENADE_ENABLED = COMMON_BUILDER
                .comment("是否启用蜜蜂手雷")
                .define("enabled", true);
        BEE_GRENADE_BEE_COUNT = COMMON_BUILDER
                .comment("蜜蜂数量")
                .defineInRange("bee_count", 5, 0, Integer.MAX_VALUE);
        BEE_GRENADE_BEE_LIFETIME = COMMON_BUILDER
                .comment("蜜蜂存活时间（秒）")
                .defineInRange("bee_lifetime", 30, 0, Integer.MAX_VALUE);
        BEE_GRENADE_PLAYER_FRIENDLY = COMMON_BUILDER
                .comment("是否对玩家友好")
                .define("player_friendly", true);
        BEE_GRENADE_HONEY_SLOWNESS_AREA_ENABLED = COMMON_BUILDER
                .comment("是否启用蜂蜜减速区域")
                .define("honey_slowness_area_enabled", true);
        BEE_GRENADE_HONEY_AREA_RADIUS = COMMON_BUILDER
                .comment("蜂蜜减速区域半径（格）")
                .defineInRange("honey_area_radius", 2.5, 0.0, Double.MAX_VALUE);
        BEE_GRENADE_HONEY_AREA_DURATION = COMMON_BUILDER
                .comment("蜂蜜减速区域持续时间（秒）")
                .defineInRange("honey_area_duration", 5, 0, Integer.MAX_VALUE);
        BEE_GRENADE_DESTROY_BLOCKS = COMMON_BUILDER
                .comment("爆炸是否破坏方块")
                .define("destroy_blocks", false);
        COMMON_BUILDER.pop();

        // 状态效果配置
        COMMON_BUILDER.comment("状态效果配置").push("effects");

        // 尖叫效果配置
        COMMON_BUILDER.comment("尖叫效果配置").push("screaming_effect");
        SCREAMING_EFFECT_ENABLED = COMMON_BUILDER
                .comment("是否启用尖叫效果")
                .define("enabled", true);
        SCREAMING_EFFECT_RANGE = COMMON_BUILDER
                .comment("尖叫效果吸引敌对生物范围（格）")
                .defineInRange("range", 30, 0, 255);
        COMMON_BUILDER.pop();

        // 颠颠倒倒效果配置
        COMMON_BUILDER.comment("颠颠倒倒效果配置").push("dizzy_effect");
        DIZZY_EFFECT_ENABLED = COMMON_BUILDER
                .comment("是否启用颠颠倒倒效果")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 天旋地转效果配置
        COMMON_BUILDER.comment("天旋地转效果配置").push("spinning_effect");
        SPINNING_EFFECT_ENABLED = COMMON_BUILDER
                .comment("是否启用天旋地转效果")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.pop(); // effects

        COMMON_BUILDER.pop(); // common
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    /**
     * 构建客户端配置
     */
    private static void buildClientConfig() {
        CLIENT_BUILDER.comment("客户端配置").push("client");

        // 客户端特有配置...

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    /**
     * 构建服务器配置
     */
    private static void buildServerConfig() {
        SERVER_BUILDER.comment("服务器配置").push("server");

        // ... 其他服务器配置 ...

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
} 