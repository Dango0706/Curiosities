package me.tuanzi.curiosities.config;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.Map;

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
    // 钢契附魔配置
    public static ForgeConfigSpec.BooleanValue STEEL_COVENANT_ENABLED;
    public static ForgeConfigSpec.BooleanValue STEEL_COVENANT_TRADEABLE;
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
    // 熟练附魔配置
    public static ForgeConfigSpec.BooleanValue PROFICIENCY_ENABLED;
    public static ForgeConfigSpec.DoubleValue PROFICIENCY_ATTACK_SPEED_PERCENT;
    // 时空卷轴配置
    public static ForgeConfigSpec.BooleanValue SCROLL_OF_SPACETIME_ENABLED;
    public static ForgeConfigSpec.IntValue SCROLL_OF_SPACETIME_MAX_DISTANCE;
    public static ForgeConfigSpec.IntValue SCROLL_OF_SPACETIME_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue SCROLL_OF_SPACETIME_TRADEABLE;
    public static ForgeConfigSpec.IntValue SCROLL_OF_SPACETIME_DURABILITY_COST;
    // 虚空吞噬之剑配置
    public static ForgeConfigSpec.BooleanValue VOID_SWORD_ENABLED;
    public static ForgeConfigSpec.IntValue VOID_SWORD_MAX_ENERGY;
    public static ForgeConfigSpec.DoubleValue VOID_SWORD_ENERGY_PERCENT;
    public static ForgeConfigSpec.DoubleValue VOID_SWORD_BLACK_HOLE_RANGE;

    public static ForgeConfigSpec.DoubleValue VOID_SWORD_BLACK_HOLE_DAMAGE;
    public static ForgeConfigSpec.IntValue VOID_SWORD_BLACK_HOLE_DURATION;
    public static ForgeConfigSpec.IntValue VOID_SWORD_BLACK_HOLE_DAMAGE_INTERVAL;
    public static ForgeConfigSpec.IntValue VOID_SWORD_COOLDOWN;
    public static ForgeConfigSpec.IntValue VOID_SWORD_MAX_CAST_DISTANCE;

    // 概率圣剑配置
    public static ForgeConfigSpec.BooleanValue PROBABILITY_HOLY_SWORD_ENABLED;
    public static ForgeConfigSpec.DoubleValue PROBABILITY_HOLY_SWORD_CHEST_SPAWN_CHANCE;
    public static ForgeConfigSpec.IntValue PROBABILITY_HOLY_SWORD_BASE_DAMAGE;
    public static ForgeConfigSpec.DoubleValue PROBABILITY_HOLY_SWORD_EFFECT_CHANCE;
    public static ForgeConfigSpec.DoubleValue PROBABILITY_HOLY_SWORD_LUCKY_STRIKE_MAX_HEALTH;
    // 无限水桶配置
    public static ForgeConfigSpec.BooleanValue INFINITE_WATER_BUCKET_ENABLED;

    // 控制之杖配置
    public static ForgeConfigSpec.BooleanValue CONTROL_STAFF_ENABLED;
    public static ForgeConfigSpec.BooleanValue CONTROL_STAFF_CRAFTABLE;

    // 创造模式村民交易自动填充配置
    public static ForgeConfigSpec.BooleanValue CREATIVE_TRADE_AUTO_FILL_ENABLED;
    
    // 状态效果配置
    // 尖叫效果配置
    public static ForgeConfigSpec.BooleanValue SCREAMING_EFFECT_ENABLED;
    public static ForgeConfigSpec.IntValue SCREAMING_EFFECT_RANGE;
    // 混乱效果配置
    public static ForgeConfigSpec.BooleanValue CONFUSION_EFFECT_ENABLED;
    public static ForgeConfigSpec.DoubleValue CONFUSION_CHANCE_PER_LEVEL;
    public static ForgeConfigSpec.DoubleValue CONFUSION_DAMAGE_PERCENT_PER_LEVEL;
    public static ForgeConfigSpec.DoubleValue CONFUSION_DAMAGE_PERCENT_MAX;
    // 颠颠倒倒效果配置
    public static ForgeConfigSpec.BooleanValue DIZZY_EFFECT_ENABLED;
    // 天旋地转效果配置
    public static ForgeConfigSpec.BooleanValue SPINNING_EFFECT_ENABLED;
    // 瓦解之躯效果配置
    public static ForgeConfigSpec.BooleanValue DISSOLVING_BODY_EFFECT_ENABLED;
    // 富有效果配置
    public static ForgeConfigSpec.BooleanValue RICH_EFFECT_ENABLED;
    public static ForgeConfigSpec.IntValue RICH_EFFECT_RANGE_PER_LEVEL;
    // 不死效果配置
    public static ForgeConfigSpec.BooleanValue UNDYING_EFFECT_ENABLED;

    // 原版修改配置
    // 一些配置
    public static ForgeConfigSpec.BooleanValue VANILLA_MODIFICATIONS_ENABLED;
    public static ForgeConfigSpec.BooleanValue IMPROVED_VILLAGER_TRADES_ENABLED;
    public static ForgeConfigSpec.BooleanValue ENHANCED_ANVIL_ENABLED;
    public static ForgeConfigSpec.IntValue ENHANCED_ANVIL_MAX_REPAIR_COST;
    public static ForgeConfigSpec.BooleanValue GLASS_BOTTLE_TO_WATER_BOTTLE_ENABLED;
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
                .comment("镰刀收获范围（NxN区域，如3表示3x3范围）")
                .defineInRange("harvest_range", 3.0, 1.0, 10.0);
        SCYTHE_SWEEP_RANGE_BONUS = COMMON_BUILDER
                .comment("镰刀横扫范围（方块数）")
                .defineInRange("sweep_range_bonus", 1.0, 0.5, 5.0);
        SCYTHE_HARVEST_DANCE_CHANCE = COMMON_BUILDER
                .comment("丰收之舞触发概率")
                .defineInRange("harvest_dance_chance", 0.03, 0.0, 1.0);
        SCYTHE_HARVEST_DANCE_RANGE = COMMON_BUILDER
                .comment("丰收之舞范围（NxN区域，如5表示5x5范围）")
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

        // 控制之杖配置
        COMMON_BUILDER.comment("控制之杖配置").push("control_staff");
        CONTROL_STAFF_ENABLED = COMMON_BUILDER
                .comment("是否启用控制之杖")
                .define("enabled", true);
        CONTROL_STAFF_CRAFTABLE = COMMON_BUILDER
                .comment("是否可以合成控制之杖")
                .define("craftable", true);
        COMMON_BUILDER.pop();

        // 创造模式村民交易自动填充配置
        COMMON_BUILDER.comment("村民交易配置").push("villager_trade");
        CREATIVE_TRADE_AUTO_FILL_ENABLED = COMMON_BUILDER
                .comment("是否在创造模式下自动填充村民交易所需物品")
                .define("creative_trade_auto_fill", true);
        COMMON_BUILDER.pop();

        // 道德天平配置
        COMMON_BUILDER.comment("道德天平配置").push("moral_balance");
        MORAL_BALANCE_ENABLED = COMMON_BUILDER
                .comment("是否启用道德天平附魔")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 钢契附魔配置
        COMMON_BUILDER.comment("钢契附魔配置").push("steel_covenant");
        STEEL_COVENANT_ENABLED = COMMON_BUILDER
                .comment("是否启用钢契附魔")
                .define("enabled", true);
        STEEL_COVENANT_TRADEABLE = COMMON_BUILDER
                .comment("是否可以被村民交易")
                .define("tradeable", false);
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

        // 熟练附魔配置
        COMMON_BUILDER.comment("熟练附魔配置").push("proficiency");
        PROFICIENCY_ENABLED = COMMON_BUILDER
                .comment("是否启用熟练附魔")
                .define("enabled", true);
        PROFICIENCY_ATTACK_SPEED_PERCENT = COMMON_BUILDER
                .comment("每级附魔增加的攻击速度百分比")
                .defineInRange("attack_speed_percent", 15.0, 1.0, 50.0);
        COMMON_BUILDER.pop();

        // 时空卷轴配置
        COMMON_BUILDER.comment("时空卷轴配置").push("scroll_of_spacetime");
        SCROLL_OF_SPACETIME_ENABLED = COMMON_BUILDER
                .comment("是否启用时空卷轴物品")
                .define("enabled", true);
        SCROLL_OF_SPACETIME_MAX_DISTANCE = COMMON_BUILDER
                .comment("最大传送距离（方块）")
                .defineInRange("max_distance", 1000, 10, 10000);
        SCROLL_OF_SPACETIME_COOLDOWN = COMMON_BUILDER
                .comment("冷却时间（秒）")
                .defineInRange("cooldown", 60, 0, 3600);
        SCROLL_OF_SPACETIME_TRADEABLE = COMMON_BUILDER
                .comment("是否可以从村民处购买")
                .define("tradeable", true);
        SCROLL_OF_SPACETIME_DURABILITY_COST = COMMON_BUILDER
                .comment("每次使用消耗的耐久度")
                .defineInRange("durability_cost", 10, 1, 300);
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

        // 瓦解之躯效果配置
        COMMON_BUILDER.comment("瓦解之躯效果配置").push("dissolving_body_effect");
        DISSOLVING_BODY_EFFECT_ENABLED = COMMON_BUILDER
                .comment("是否启用瓦解之躯效果")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 富有效果配置
        COMMON_BUILDER.comment("富有效果配置").push("rich_effect");
        RICH_EFFECT_ENABLED = COMMON_BUILDER
                .comment("是否启用富有效果")
                .define("enabled", true);
        RICH_EFFECT_RANGE_PER_LEVEL = COMMON_BUILDER
                .comment("每级富有效果的影响范围（格）")
                .defineInRange("range_per_level", 16, 4, 32);
        COMMON_BUILDER.pop();

        // 混乱效果配置
        COMMON_BUILDER.comment("混乱效果配置").push("confusion_effect");
        CONFUSION_EFFECT_ENABLED = COMMON_BUILDER
                .comment("是否启用混乱效果")
                .define("enabled", true);
        CONFUSION_CHANCE_PER_LEVEL = COMMON_BUILDER
                .comment("每级目标转移概率（0.15表示15%）")
                .defineInRange("chance_per_level", 0.15, 0.01, 1.0);
        CONFUSION_DAMAGE_PERCENT_PER_LEVEL = COMMON_BUILDER
                .comment("每级造成原本伤害百分比（0.3表示30%）")
                .defineInRange("damage_percent_per_level", 0.3, 0.01, 1.0);
        CONFUSION_DAMAGE_PERCENT_MAX = COMMON_BUILDER
                .comment("造成伤害百分比上限（设置为0则为无上限）")
                .defineInRange("damage_percent_max", 1.0, 0.0, 10.0);
        COMMON_BUILDER.pop();

        // 不死效果配置
        COMMON_BUILDER.comment("不死效果配置").push("undying_effect");
        UNDYING_EFFECT_ENABLED = COMMON_BUILDER
                .comment("是否启用不死效果")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 无限水桶配置
        COMMON_BUILDER.comment("无限水桶配置").push("infinite_water_bucket");
        INFINITE_WATER_BUCKET_ENABLED = COMMON_BUILDER
                .comment("是否启用无限水桶")
                .define("enabled", true);
        COMMON_BUILDER.pop();
        
        // 虚空吞噬之剑配置
        COMMON_BUILDER.comment("虚空吞噬之剑配置").push("void_sword");
        VOID_SWORD_ENABLED = COMMON_BUILDER
                .comment("是否启用虚空吞噬之剑")
                .define("enabled", true);
        VOID_SWORD_MAX_ENERGY = COMMON_BUILDER
                .comment("虚空能量最大存储量")
                .defineInRange("max_energy", 1000, 100, 10000);
        VOID_SWORD_ENERGY_PERCENT = COMMON_BUILDER
                .comment("击杀生物获得的生命值上限百分比")
                .defineInRange("energy_percent", 10.0, 1.0, 100.0);
        VOID_SWORD_BLACK_HOLE_RANGE = COMMON_BUILDER
                .comment("黑洞吸附范围（方块）")
                .defineInRange("black_hole_range", 10.0, 1.0, 50.0);
        VOID_SWORD_BLACK_HOLE_DAMAGE = COMMON_BUILDER
                .comment("黑洞造成的伤害值（设为0则使用玩家攻击力）")
                .defineInRange("black_hole_damage", 0.0, 0.0, 100.0);
        VOID_SWORD_BLACK_HOLE_DURATION = COMMON_BUILDER
                .comment("黑洞持续时间（秒）")
                .defineInRange("black_hole_duration", 10, 1, 60);
        VOID_SWORD_BLACK_HOLE_DAMAGE_INTERVAL = COMMON_BUILDER
                .comment("黑洞伤害间隔（秒）")
                .defineInRange("black_hole_damage_interval", 1, 0, 10);
        VOID_SWORD_COOLDOWN = COMMON_BUILDER
                .comment("黑洞冷却时间（秒）")
                .defineInRange("cooldown", 10, 0, 3600);
        VOID_SWORD_MAX_CAST_DISTANCE = COMMON_BUILDER
                .comment("最大施法距离（方块），设置为0则无限制")
                .defineInRange("max_cast_distance", 10, 0, 100);
        COMMON_BUILDER.pop();

        // 概率圣剑配置
        COMMON_BUILDER.comment("概率圣剑配置").push("probability_holy_sword");
        PROBABILITY_HOLY_SWORD_ENABLED = COMMON_BUILDER
                .comment("是否启用概率圣剑")
                .define("enabled", true);
        PROBABILITY_HOLY_SWORD_CHEST_SPAWN_CHANCE = COMMON_BUILDER
                .comment("沙漠神殿宝箱生成概率（0.0-1.0）")
                .defineInRange("chest_spawn_chance", 0.15, 0.0, 1.0);
        PROBABILITY_HOLY_SWORD_BASE_DAMAGE = COMMON_BUILDER
                .comment("基础攻击伤害")
                .defineInRange("base_damage", 6, 1, 20);
        PROBABILITY_HOLY_SWORD_EFFECT_CHANCE = COMMON_BUILDER
                .comment("特殊效果触发概率（0.0-1.0）")
                .defineInRange("effect_chance", 0.2, 0.0, 1.0);
        PROBABILITY_HOLY_SWORD_LUCKY_STRIKE_MAX_HEALTH = COMMON_BUILDER
                .comment("幸运斩生效的目标最大血量上限")
                .defineInRange("lucky_strike_max_health", 25.0, 1.0, 100.0);
        COMMON_BUILDER.pop();

        // 原版修改配置
        COMMON_BUILDER.comment("原版修改配置").push("vanilla_modifications");
        VANILLA_MODIFICATIONS_ENABLED = COMMON_BUILDER
                .comment("是否启用原版修改功能")
                .define("enabled", true);

        // 村民交易改进
        COMMON_BUILDER.comment("村民交易改进").push("improved_villager_trades");
        IMPROVED_VILLAGER_TRADES_ENABLED = COMMON_BUILDER
                .comment("是否启用村民交易改进（增加稀有物品交易概率）")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        // 铁砧改进
        COMMON_BUILDER.comment("铁砧改进").push("enhanced_anvil");
        ENHANCED_ANVIL_ENABLED = COMMON_BUILDER
                .comment("是否启用铁砧改进（提高铁砧使用次数上限）")
                .define("enabled", true);
        ENHANCED_ANVIL_MAX_REPAIR_COST = COMMON_BUILDER
                .comment("铁砧最大修复成本（原版为40）")
                .defineInRange("max_repair_cost", 100, 40, 1000);
        COMMON_BUILDER.pop();

        // 玻璃瓶转水瓶
        COMMON_BUILDER.comment("玻璃瓶转水瓶").push("glass_bottle_to_water_bottle");
        GLASS_BOTTLE_TO_WATER_BOTTLE_ENABLED = COMMON_BUILDER
                .comment("是否启用玻璃瓶投掷到水中自动转换为水瓶")
                .define("enabled", true);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.pop(); // vanilla_modifications

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

    /**
     * 在客户端应用从服务器接收的配置
     *
     * @param booleanConfigs 布尔值配置映射
     * @param intConfigs     整数配置映射
     * @param doubleConfigs  浮点数配置映射
     */
    public static void applyServerConfig(Map<String, Boolean> booleanConfigs, Map<String, Integer> intConfigs, Map<String, Double> doubleConfigs) {
        LOGGER.info("应用服务端配置到客户端...");

        // 应用布尔值配置
        if (booleanConfigs.containsKey("chain_mining_enabled")) {
            CHAIN_MINING_ENABLED.set(booleanConfigs.get("chain_mining_enabled"));
        }
        if (booleanConfigs.containsKey("super_fortune_enabled")) {
            SUPER_FORTUNE_ENABLED.set(booleanConfigs.get("super_fortune_enabled"));
        }
        if (booleanConfigs.containsKey("wolf_fang_potato_enabled")) {
            WOLF_FANG_POTATO_ENABLED.set(booleanConfigs.get("wolf_fang_potato_enabled"));
        }
        if (booleanConfigs.containsKey("scythe_enabled")) {
            SCYTHE_ENABLED.set(booleanConfigs.get("scythe_enabled"));
            LOGGER.info("镰刀已{}启用", SCYTHE_ENABLED.get() ? "" : "禁");
        }
        if (booleanConfigs.containsKey("rich_effect_enabled")) {
            RICH_EFFECT_ENABLED.set(booleanConfigs.get("rich_effect_enabled"));
        }
        if (booleanConfigs.containsKey("confusion_effect_enabled")) {
            CONFUSION_EFFECT_ENABLED.set(booleanConfigs.get("confusion_effect_enabled"));
        }
        if (booleanConfigs.containsKey("rocket_boots_enabled")) {
            ROCKET_BOOTS_ENABLED.set(booleanConfigs.get("rocket_boots_enabled"));
        }
        if (booleanConfigs.containsKey("moral_balance_enabled")) {
            MORAL_BALANCE_ENABLED.set(booleanConfigs.get("moral_balance_enabled"));
        }
        if (booleanConfigs.containsKey("steel_covenant_enabled")) {
            STEEL_COVENANT_ENABLED.set(booleanConfigs.get("steel_covenant_enabled"));
        }
        if (booleanConfigs.containsKey("steel_covenant_tradeable")) {
            STEEL_COVENANT_TRADEABLE.set(booleanConfigs.get("steel_covenant_tradeable"));
        }
        if (booleanConfigs.containsKey("fake_tnt_enabled")) {
            FAKE_TNT_ENABLED.set(booleanConfigs.get("fake_tnt_enabled"));
        }
        if (booleanConfigs.containsKey("lucky_sword_enabled")) {
            LUCKY_SWORD_ENABLED.set(booleanConfigs.get("lucky_sword_enabled"));
        }
        if (booleanConfigs.containsKey("screaming_pie_enabled")) {
            SCREAMING_PIE_ENABLED.set(booleanConfigs.get("screaming_pie_enabled"));
        }
        if (booleanConfigs.containsKey("bat_wing_enabled")) {
            BAT_WING_ENABLED.set(booleanConfigs.get("bat_wing_enabled"));
        }
        if (booleanConfigs.containsKey("bee_grenade_enabled")) {
            BEE_GRENADE_ENABLED.set(booleanConfigs.get("bee_grenade_enabled"));
        }
        if (booleanConfigs.containsKey("bee_grenade_player_friendly")) {
            BEE_GRENADE_PLAYER_FRIENDLY.set(booleanConfigs.get("bee_grenade_player_friendly"));
        }
        if (booleanConfigs.containsKey("bee_grenade_honey_slowness_area_enabled")) {
            BEE_GRENADE_HONEY_SLOWNESS_AREA_ENABLED.set(booleanConfigs.get("bee_grenade_honey_slowness_area_enabled"));
        }
        if (booleanConfigs.containsKey("bee_grenade_destroy_blocks")) {
            BEE_GRENADE_DESTROY_BLOCKS.set(booleanConfigs.get("bee_grenade_destroy_blocks"));
        }
        if (booleanConfigs.containsKey("proficiency_enabled")) {
            PROFICIENCY_ENABLED.set(booleanConfigs.get("proficiency_enabled"));
        }
        if (booleanConfigs.containsKey("scroll_of_spacetime_enabled")) {
            SCROLL_OF_SPACETIME_ENABLED.set(booleanConfigs.get("scroll_of_spacetime_enabled"));
        }
        if (booleanConfigs.containsKey("scroll_of_spacetime_tradeable")) {
            SCROLL_OF_SPACETIME_TRADEABLE.set(booleanConfigs.get("scroll_of_spacetime_tradeable"));
        }
        if (booleanConfigs.containsKey("void_sword_enabled")) {
            VOID_SWORD_ENABLED.set(booleanConfigs.get("void_sword_enabled"));
        }
        if (booleanConfigs.containsKey("infinite_water_bucket_enabled")) {
            INFINITE_WATER_BUCKET_ENABLED.set(booleanConfigs.get("infinite_water_bucket_enabled"));
        }
        if (booleanConfigs.containsKey("creative_trade_auto_fill")) {
            CREATIVE_TRADE_AUTO_FILL_ENABLED.set(booleanConfigs.get("creative_trade_auto_fill"));
        }
        if (booleanConfigs.containsKey("undying_effect_enabled")) {
            UNDYING_EFFECT_ENABLED.set(booleanConfigs.get("undying_effect_enabled"));
        }
        if (booleanConfigs.containsKey("glass_bottle_to_water_bottle_enabled")) {
            GLASS_BOTTLE_TO_WATER_BOTTLE_ENABLED.set(booleanConfigs.get("glass_bottle_to_water_bottle_enabled"));
        }

        // 应用整数配置
        if (intConfigs.containsKey("chain_mining_max_blocks")) {
            CHAIN_MINING_MAX_BLOCKS.set(intConfigs.get("chain_mining_max_blocks"));
        }
        if (intConfigs.containsKey("chain_mining_blocks_per_level")) {
            CHAIN_MINING_BLOCKS_PER_LEVEL.set(intConfigs.get("chain_mining_blocks_per_level"));
        }
        if (intConfigs.containsKey("chain_mining_harvest_range")) {
            CHAIN_MINING_HARVEST_RANGE.set(intConfigs.get("chain_mining_harvest_range"));
        }
        if (intConfigs.containsKey("rocket_boots_fuel_consumption")) {
            ROCKET_BOOTS_FUEL_CONSUMPTION.set(intConfigs.get("rocket_boots_fuel_consumption"));
        }
        if (intConfigs.containsKey("rocket_boots_max_fuel")) {
            ROCKET_BOOTS_MAX_FUEL.set(intConfigs.get("rocket_boots_max_fuel"));
        }
        if (intConfigs.containsKey("screaming_pie_slow_falling_duration")) {
            SCREAMING_PIE_SLOW_FALLING_DURATION.set(intConfigs.get("screaming_pie_slow_falling_duration"));
        }
        if (intConfigs.containsKey("screaming_pie_screaming_duration")) {
            SCREAMING_PIE_SCREAMING_DURATION.set(intConfigs.get("screaming_pie_screaming_duration"));
        }
        if (intConfigs.containsKey("bee_grenade_bee_count")) {
            BEE_GRENADE_BEE_COUNT.set(intConfigs.get("bee_grenade_bee_count"));
        }
        if (intConfigs.containsKey("bee_grenade_bee_lifetime")) {
            BEE_GRENADE_BEE_LIFETIME.set(intConfigs.get("bee_grenade_bee_lifetime"));
        }
        if (intConfigs.containsKey("bee_grenade_honey_area_duration")) {
            BEE_GRENADE_HONEY_AREA_DURATION.set(intConfigs.get("bee_grenade_honey_area_duration"));
        }
        if (intConfigs.containsKey("scroll_of_spacetime_max_distance")) {
            SCROLL_OF_SPACETIME_MAX_DISTANCE.set(intConfigs.get("scroll_of_spacetime_max_distance"));
        }
        if (intConfigs.containsKey("scroll_of_spacetime_cooldown")) {
            SCROLL_OF_SPACETIME_COOLDOWN.set(intConfigs.get("scroll_of_spacetime_cooldown"));
        }
        if (intConfigs.containsKey("scroll_of_spacetime_durability_cost")) {
            SCROLL_OF_SPACETIME_DURABILITY_COST.set(intConfigs.get("scroll_of_spacetime_durability_cost"));
        }
        if (intConfigs.containsKey("void_sword_max_energy")) {
            VOID_SWORD_MAX_ENERGY.set(intConfigs.get("void_sword_max_energy"));
        }
        if (intConfigs.containsKey("void_sword_black_hole_duration")) {
            VOID_SWORD_BLACK_HOLE_DURATION.set(intConfigs.get("void_sword_black_hole_duration"));
        }
        if (intConfigs.containsKey("void_sword_black_hole_damage_interval")) {
            VOID_SWORD_BLACK_HOLE_DAMAGE_INTERVAL.set(intConfigs.get("void_sword_black_hole_damage_interval"));
        }
        if (intConfigs.containsKey("void_sword_cooldown")) {
            VOID_SWORD_COOLDOWN.set(intConfigs.get("void_sword_cooldown"));
        }
        if (intConfigs.containsKey("void_sword_max_cast_distance")) {
            VOID_SWORD_MAX_CAST_DISTANCE.set(intConfigs.get("void_sword_max_cast_distance"));
        }

        // 应用浮点数配置
        if (doubleConfigs.containsKey("scythe_attack_speed")) {
            SCYTHE_ATTACK_SPEED.set(doubleConfigs.get("scythe_attack_speed"));
        }
        if (doubleConfigs.containsKey("scythe_damage_bonus")) {
            SCYTHE_DAMAGE_BONUS.set(doubleConfigs.get("scythe_damage_bonus"));
        }
        if (doubleConfigs.containsKey("scythe_harvest_range")) {
            SCYTHE_HARVEST_RANGE.set(doubleConfigs.get("scythe_harvest_range"));
        }
        if (doubleConfigs.containsKey("scythe_sweep_range_bonus")) {
            SCYTHE_SWEEP_RANGE_BONUS.set(doubleConfigs.get("scythe_sweep_range_bonus"));
        }
        if (doubleConfigs.containsKey("scythe_harvest_dance_chance")) {
            SCYTHE_HARVEST_DANCE_CHANCE.set(doubleConfigs.get("scythe_harvest_dance_chance"));
        }
        if (doubleConfigs.containsKey("scythe_harvest_dance_range")) {
            SCYTHE_HARVEST_DANCE_RANGE.set(doubleConfigs.get("scythe_harvest_dance_range"));
        }
        if (doubleConfigs.containsKey("rocket_boots_boost_power")) {
            ROCKET_BOOTS_BOOST_POWER.set(doubleConfigs.get("rocket_boots_boost_power"));
        }
        if (doubleConfigs.containsKey("rocket_boots_max_jump_height")) {
            ROCKET_BOOTS_MAX_JUMP_HEIGHT.set(doubleConfigs.get("rocket_boots_max_jump_height"));
        }
        if (doubleConfigs.containsKey("lucky_sword_min_damage")) {
            LUCKY_SWORD_MIN_DAMAGE.set(doubleConfigs.get("lucky_sword_min_damage"));
        }
        if (doubleConfigs.containsKey("lucky_sword_max_damage")) {
            LUCKY_SWORD_MAX_DAMAGE.set(doubleConfigs.get("lucky_sword_max_damage"));
        }
        if (doubleConfigs.containsKey("bee_grenade_honey_area_radius")) {
            BEE_GRENADE_HONEY_AREA_RADIUS.set(doubleConfigs.get("bee_grenade_honey_area_radius"));
        }
        if (doubleConfigs.containsKey("proficiency_attack_speed_percent")) {
            PROFICIENCY_ATTACK_SPEED_PERCENT.set(doubleConfigs.get("proficiency_attack_speed_percent"));
        }
        if (doubleConfigs.containsKey("void_sword_energy_percent")) {
            VOID_SWORD_ENERGY_PERCENT.set(doubleConfigs.get("void_sword_energy_percent"));
        }
        if (doubleConfigs.containsKey("void_sword_black_hole_range")) {
            VOID_SWORD_BLACK_HOLE_RANGE.set(doubleConfigs.get("void_sword_black_hole_range"));
        }
        if (doubleConfigs.containsKey("void_sword_black_hole_damage")) {
            VOID_SWORD_BLACK_HOLE_DAMAGE.set(doubleConfigs.get("void_sword_black_hole_damage"));
        }
        if (doubleConfigs.containsKey("confusion_chance_per_level")) {
            CONFUSION_CHANCE_PER_LEVEL.set(doubleConfigs.get("confusion_chance_per_level"));
        }
        if (doubleConfigs.containsKey("confusion_damage_percent_per_level")) {
            CONFUSION_DAMAGE_PERCENT_PER_LEVEL.set(doubleConfigs.get("confusion_damage_percent_per_level"));
        }
        if (doubleConfigs.containsKey("confusion_damage_percent_max")) {
            CONFUSION_DAMAGE_PERCENT_MAX.set(doubleConfigs.get("confusion_damage_percent_max"));
        }

        LOGGER.info("服务端配置已成功应用到客户端");
    }
} 