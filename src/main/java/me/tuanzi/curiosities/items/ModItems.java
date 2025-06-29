package me.tuanzi.curiosities.items;

import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.items.bee_grenade.BeeGrenadeItem;
import me.tuanzi.curiosities.items.control_staff.ControlStaffItem;
import me.tuanzi.curiosities.items.entity_compass.EntityCompassItem;
import me.tuanzi.curiosities.items.lucky_sword.LuckySwordItem;
import me.tuanzi.curiosities.items.probability_holy_sword.ProbabilityHolySwordItem;
import me.tuanzi.curiosities.items.rocket_boots.RocketBootsItem;
import me.tuanzi.curiosities.items.rose_gold_tools.RoseGoldTier;
import me.tuanzi.curiosities.items.screaming_pie.ScreamingPieItem;
import me.tuanzi.curiosities.items.scroll_of_spacetime.ScrollOfSpacetimeItem;
import me.tuanzi.curiosities.items.scythe.ScytheItem;
import me.tuanzi.curiosities.items.void_sword.VoidSwordItem;
import me.tuanzi.curiosities.items.void_sword.VoidSwordTier;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 模组物品注册类
 * 负责注册所有模组自定义物品
 */
public class ModItems {
    /**
     * 物品注册表
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Curiosities.MODID);
    /**
     * 狼牙土豆
     * 食用后获得"狼群领袖"效果，使狼群帮助攻击目标
     */
    public static final RegistryObject<Item> WOLF_FANG_POTATO = ITEMS.register(
            "wolf_fang_potato",
            WolfFangPotatoItem::new
    );
    /**
     * 火箭靴
     * 允许玩家蓄力跳跃，实现高跳和缓降效果
     */
    public static final RegistryObject<Item> ROCKET_BOOTS = ITEMS.register(
            "rocket_boots",
            () -> new RocketBootsItem(new Item.Properties())
    );
    /**
     * 幸运剑
     * 每次攻击造成随机伤害，负数则为治疗
     */
    public static final RegistryObject<Item> LUCKY_SWORD = ITEMS.register(
            "lucky_sword",
            LuckySwordItem::new
    );
    /**
     * 蝙蝠翅膀
     * 蝙蝠掉落物，用于合成尖叫派
     */
    public static final RegistryObject<Item> BAT_WING = ITEMS.register(
            "bat_wing",
            BatWingItem::new
    );
    /**
     * 尖叫派
     * 食用后恢复全部饱食度，获得尖叫和缓降效果
     */
    public static final RegistryObject<Item> SCREAMING_PIE = ITEMS.register(
            "screaming_pie",
            ScreamingPieItem::new
    );
    /**
     * 虚空吞噬之剑
     * 击杀生物收集虚空能量，释放黑洞吸引敌人
     */
    public static final RegistryObject<Item> VOID_SWORD = ITEMS.register(
            "void_sword",
            () -> new VoidSwordItem(
                    VoidSwordTier.INSTANCE,
                    3, // 攻击伤害加成
                    -2.4F, // 攻击速度，1.6 = -2.4F
                    new Item.Properties().fireResistant()
            )
    );
    /**
     * 控制之杖
     * 右键点击生物让它们互相攻击
     */
    public static final RegistryObject<Item> CONTROL_STAFF = ITEMS.register(
            "control_staff",
            ControlStaffItem::new
    );
    /**
     * 木质镰刀
     * 基础农作物收割工具
     */
    public static final RegistryObject<Item> WOODEN_SCYTHE = ITEMS.register(
            "wooden_scythe",
            () -> new ScytheItem(
                    Tiers.WOOD,
                    3,
                    -4.0f,
                    new Item.Properties()
            )
    );
    /**
     * 石质镰刀
     * 中级农作物收割工具
     */
    public static final RegistryObject<Item> STONE_SCYTHE = ITEMS.register(
            "stone_scythe",
            () -> new ScytheItem(
                    Tiers.STONE,
                    3,
                    -4.0f,
                    new Item.Properties()
            )
    );
    /**
     * 铁质镰刀
     * 高级农作物收割工具
     */
    public static final RegistryObject<Item> IRON_SCYTHE = ITEMS.register(
            "iron_scythe",
            () -> new ScytheItem(
                    Tiers.IRON,
                    3,
                    -4.0f,
                    new Item.Properties()
            )
    );
    /**
     * 金质镰刀
     * 快速但低耐久的农作物收割工具
     */
    public static final RegistryObject<Item> GOLDEN_SCYTHE = ITEMS.register(
            "golden_scythe",
            () -> new ScytheItem(
                    Tiers.GOLD,
                    3,
                    -4.0f,
                    new Item.Properties()
            )
    );
    /**
     * 钻石镰刀
     * 最高级农作物收割工具
     */
    public static final RegistryObject<Item> DIAMOND_SCYTHE = ITEMS.register(
            "diamond_scythe",
            () -> new ScytheItem(
                    Tiers.DIAMOND,
                    3,
                    -4.0f,
                    new Item.Properties()
            )
    );
    /**
     * 下界合金镰刀
     * 终极农作物收割工具
     */
    public static final RegistryObject<Item> NETHERITE_SCYTHE = ITEMS.register(
            "netherite_scythe",
            () -> new ScytheItem(
                    Tiers.NETHERITE,
                    3,
                    -4.0f,
                    new Item.Properties().fireResistant()
            )
    );
    /**
     * 蜜蜂手雷
     * 投掷后爆炸并释放愤怒的蜜蜂
     */
    public static final RegistryObject<Item> BEE_GRENADE = ITEMS.register(
            "bee_grenade",
            BeeGrenadeItem::new
    );
    /**
     * 时空卷轴
     * 右键使用可在当前位置创建时空锚点，再次使用可瞬间传送回锚点位置
     */
    public static final RegistryObject<Item> SCROLL_OF_SPACETIME = ITEMS.register(
            "scroll_of_spacetime",
            ScrollOfSpacetimeItem::new
    );
    /**
     * 无限水桶
     * 可无限倒出水，右键收水时可以收集3x3范围的水源
     */
    public static final RegistryObject<Item> INFINITE_WATER_BUCKET = ITEMS.register(
            "infinite_water_bucket",
            InfiniteWaterBucketItem::new
    );
    /**
     * 涡毒腺体
     * 食用后补充4格饱食度与2的饱和度，并给予玩家多种负面效果
     */
    public static final RegistryObject<Item> TOXIC_GLAND = ITEMS.register(
            "toxic_gland",
            ToxicGlandItem::new
    );
    /**
     * 概率圣剑
     * 基础攻击伤害6点，耐久度300点，20%概率触发随机特殊效果
     * 只能用铁锭修复，不能附魔（除了诅咒）
     */
    public static final RegistryObject<Item> PROBABILITY_HOLY_SWORD = ITEMS.register(
            "probability_holy_sword",
            ProbabilityHolySwordItem::new
    );

    /**
     * 生物指南针
     * 右键使用寻找生物，Shift+右键选择生物类型
     * 可在岩浆中漂浮，具有物品冷却机制
     */
    public static final RegistryObject<Item> ENTITY_COMPASS = ITEMS.register(
            "entity_compass",
            EntityCompassItem::new
    );

    /**
     * 玫瑰金镐子
     * 使用锻造台由金镐和铜锭合成
     */
    public static final RegistryObject<Item> ROSE_GOLD_PICKAXE = ITEMS.register(
            "rose_gold_pickaxe",
            () -> new PickaxeItem(
                    RoseGoldTier.INSTANCE,
                    3,
                    -2.8f,
                    new Item.Properties()
            )
    );

    /**
     * 玫瑰金斧子
     * 使用锻造台由金斧和铜锭合成
     */
    public static final RegistryObject<Item> ROSE_GOLD_AXE = ITEMS.register(
            "rose_gold_axe",
            () -> new AxeItem(
                    RoseGoldTier.INSTANCE,
                    8.0f,
                    -3.1f,
                    new Item.Properties()
            )
    );

    /**
     * 玫瑰金锄头
     * 使用锻造台由金锄和铜锭合成
     */
    public static final RegistryObject<Item> ROSE_GOLD_HOE = ITEMS.register(
            "rose_gold_hoe",
            () -> new HoeItem(
                    RoseGoldTier.INSTANCE,
                    0,
                    -1.0f,
                    new Item.Properties()
            )
    );

    /**
     * 玫瑰金铲子
     * 使用锻造台由金铲和铜锭合成
     */
    public static final RegistryObject<Item> ROSE_GOLD_SHOVEL = ITEMS.register(
            "rose_gold_shovel",
            () -> new ShovelItem(
                    RoseGoldTier.INSTANCE,
                    3.5f,
                    -3.0f,
                    new Item.Properties()
            )
    );

    /**
     * 玫瑰金剑
     * 使用锻造台由金剑和铜锭合成
     */
    public static final RegistryObject<Item> ROSE_GOLD_SWORD = ITEMS.register(
            "rose_gold_sword",
            () -> new SwordItem(
                    RoseGoldTier.INSTANCE,
                    5,
                    -2.4f,
                    new Item.Properties()
            )
    );

    /**
     * 因果怀表
     * 可以储存玩家当前状态并在15秒内回溯到储存的状态
     */
    public static final RegistryObject<Item> CAUSAL_POCKET_WATCH = ITEMS.register(
            "causal_pocket_watch",
            CausalPocketWatchItem::new
    );

    private static final Logger LOGGER = LogManager.getLogger();
}