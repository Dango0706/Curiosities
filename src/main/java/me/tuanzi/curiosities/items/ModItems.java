package me.tuanzi.curiosities.items;

import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.items.rocket_boots.RocketBootsItem;
import me.tuanzi.curiosities.items.scythe.ScytheConfig;
import me.tuanzi.curiosities.items.scythe.ScytheItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
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
    private static final Logger LOGGER = LogManager.getLogger();

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
} 