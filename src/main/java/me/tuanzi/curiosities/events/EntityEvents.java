package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import me.tuanzi.curiosities.items.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * 实体相关事件处理类
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class EntityEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 蝙蝠翅膀掉落概率(30%)
    private static final float BAT_WING_DROP_CHANCE = 0.3F;

    /**
     * 处理实体死亡事件
     * 当蝙蝠死亡时，有几率掉落蝙蝠翅膀
     */
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        // 检查是否为蝙蝠
        if (event.getEntity() instanceof Bat bat) {
            // 检查配置是否启用蝙蝠翅膀掉落
            if (ModConfigManager.BAT_WING_ENABLED.get()) {
                // 根据概率决定是否掉落
                if (bat.getRandom().nextFloat() < BAT_WING_DROP_CHANCE) {
                    // 创建蝙蝠翅膀物品实体
                    Entity batEntity = event.getEntity();
                    ItemEntity itemEntity = new ItemEntity(
                            batEntity.level(),
                            batEntity.getX(),
                            batEntity.getY(),
                            batEntity.getZ(),
                            new ItemStack(ModItems.BAT_WING.get())
                    );

                    // 在世界中生成物品
                    batEntity.level().addFreshEntity(itemEntity);

                    LOGGER.debug("蝙蝠死亡掉落了蝙蝠翅膀");
                }
            }
        }
    }
} 