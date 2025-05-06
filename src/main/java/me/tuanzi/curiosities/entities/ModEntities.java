package me.tuanzi.curiosities.entities;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.items.bee_grenade.BeeGrenadeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * 实体类型注册类
 * 负责注册所有模组自定义实体类型
 */
public class ModEntities {
    /**
     * 实体类型注册表
     */
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Curiosities.MODID);
    /**
     * 蜜蜂手雷实体类型
     */
    public static final RegistryObject<EntityType<BeeGrenadeEntity>> BEE_GRENADE = ENTITIES.register(
            "bee_grenade",
            () -> EntityType.Builder.<BeeGrenadeEntity>of(BeeGrenadeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F) // 设置实体大小
                    .clientTrackingRange(4) // 客户端跟踪范围
                    .updateInterval(10) // 更新间隔
                    .build("bee_grenade")
    );
    private static final Logger LOGGER = LogUtils.getLogger();
} 