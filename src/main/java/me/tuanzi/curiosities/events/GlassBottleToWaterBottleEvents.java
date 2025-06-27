package me.tuanzi.curiosities.events;

import com.mojang.logging.LogUtils;
import me.tuanzi.curiosities.Curiosities;
import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 玻璃瓶转水瓶事件处理类
 * 当玻璃瓶被投掷到水中时，自动转换为水瓶
 */
@Mod.EventBusSubscriber(modid = Curiosities.MODID)
public class GlassBottleToWaterBottleEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 存储需要检查的玻璃瓶实体和它们的检查次数
    private static final Map<ItemEntity, Integer> pendingChecks = new HashMap<>();

    /**
     * 处理物品投掷事件
     * 当玻璃瓶被投掷时，检查是否接触水源
     */
    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        // 检查配置是否启用
        if (!ModConfigManager.GLASS_BOTTLE_TO_WATER_BOTTLE_ENABLED.get()) {
            return;
        }

        ItemEntity itemEntity = event.getEntity();
        ItemStack itemStack = itemEntity.getItem();

        // 检查是否为玻璃瓶
        if (!itemStack.is(Items.GLASS_BOTTLE)) {
            return;
        }

        Level level = itemEntity.level();

        // 在服务器端处理
        if (level.isClientSide()) {
            return;
        }

        // 立即检查一次
        if (isInWater(level, itemEntity.blockPosition())) {
            convertToWaterBottle(itemEntity);
        } else {
            // 如果没有立即接触水，添加到待检查列表
            pendingChecks.put(itemEntity, 0);
        }
    }

    /**
     * 处理服务器tick事件，检查待处理的玻璃瓶
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || pendingChecks.isEmpty()) {
            return;
        }

        // 检查配置是否启用
        if (!ModConfigManager.GLASS_BOTTLE_TO_WATER_BOTTLE_ENABLED.get()) {
            pendingChecks.clear();
            return;
        }

        Iterator<Map.Entry<ItemEntity, Integer>> iterator = pendingChecks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<ItemEntity, Integer> entry = iterator.next();
            ItemEntity itemEntity = entry.getKey();
            int checkCount = entry.getValue();

            // 如果实体已经不存在，移除
            if (!itemEntity.isAlive()) {
                iterator.remove();
                continue;
            }

            // 检查是否接触水
            Level level = itemEntity.level();
            BlockPos pos = itemEntity.blockPosition();

            if (isInWater(level, pos)) {
                convertToWaterBottle(itemEntity);
                iterator.remove();
            } else {
                // 增加检查次数
                checkCount++;
                if (checkCount >= 40) { // 40 ticks = 2秒
                    iterator.remove(); // 超时，停止检查
                } else {
                    entry.setValue(checkCount);
                }
            }
        }
    }

    /**
     * 检查指定位置是否有水
     *
     * @param level 世界
     * @param pos   位置
     * @return 是否有水
     */
    private static boolean isInWater(Level level, BlockPos pos) {
        // 检查当前位置的方块状态
        BlockState blockState = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);

        // 检查是否为水方块或水流体
        if (blockState.is(Blocks.WATER) || fluidState.is(Fluids.WATER) || fluidState.is(Fluids.FLOWING_WATER)) {
            return true;
        }

        // 检查周围的位置（上下左右前后）
        BlockPos[] surroundingPositions = {
                pos.above(),
                pos.below(),
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west()
        };

        for (BlockPos checkPos : surroundingPositions) {
            BlockState surroundingState = level.getBlockState(checkPos);
            FluidState surroundingFluid = level.getFluidState(checkPos);

            if (surroundingState.is(Blocks.WATER) ||
                    surroundingFluid.is(Fluids.WATER) ||
                    surroundingFluid.is(Fluids.FLOWING_WATER)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将玻璃瓶转换为水瓶
     *
     * @param itemEntity 玻璃瓶物品实体
     */
    private static void convertToWaterBottle(ItemEntity itemEntity) {
        ItemStack glassBottle = itemEntity.getItem();
        int count = glassBottle.getCount();

        // 创建正确的水瓶物品栈
        // 使用PotionUtils.setPotion来创建真正的水瓶，而不是"不可合成的药水"
        ItemStack waterBottle = new ItemStack(Items.POTION, count);
        PotionUtils.setPotion(waterBottle, Potions.WATER);

        // 移除原来的玻璃瓶
        itemEntity.discard();

        // 在相同位置生成水瓶
        ItemEntity waterBottleEntity = new ItemEntity(
                itemEntity.level(),
                itemEntity.getX(),
                itemEntity.getY(),
                itemEntity.getZ(),
                waterBottle
        );

        // 保持原有的运动状态
        waterBottleEntity.setDeltaMovement(itemEntity.getDeltaMovement());
        waterBottleEntity.setPickUpDelay(10);

        // 添加到世界中
        itemEntity.level().addFreshEntity(waterBottleEntity);

        LOGGER.debug("玻璃瓶已转换为水瓶，数量: {}", count);
    }
}
