package me.tuanzi.curiosities.items;

import me.tuanzi.curiosities.config.ModConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 无限水桶物品
 * 可以无限倒出水，右键收水时可以收集3x3范围的水源
 */
public class InfiniteWaterBucketItem extends BucketItem {

    // 收集范围常量，便于未来可能的配置
    private static final int COLLECT_RANGE = 1; // 1表示3x3范围(中心点加减1)

    /**
     * 构造函数
     */
    public InfiniteWaterBucketItem() {
        super(Fluids.WATER, new Item.Properties().stacksTo(1));
    }

    /**
     * 使用物品时执行的逻辑
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // 如果禁用了无限水桶功能，则直接返回失败（物品不可使用）
        if (!ModConfigManager.INFINITE_WATER_BUCKET_ENABLED.get()) {
            player.displayClientMessage(Component.translatable("item.curiosities.disabled").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack itemStack = player.getItemInHand(hand);

        // 先检查是否点击了水源方块
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = hitResult.getBlockPos();

            // 检查交互权限
            if (!level.mayInteract(player, blockPos)) {
                return InteractionResultHolder.fail(itemStack);
            }

            BlockState blockState = level.getBlockState(blockPos);

            // 如果点击的是水源方块，执行收集操作
            if (blockState.getFluidState().is(FluidTags.WATER) && blockState.getFluidState().isSource()) {
                // 收集3x3范围内的所有水源
                int collected = collectWaterInArea(level, blockPos);
                if (collected > 0) {
                    // 只有成功收集水源时才播放音效
                    level.playSound(player, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                    return InteractionResultHolder.success(itemStack);
                }
            }
        }

        // 如果没有点击水源方块或收集操作未成功，尝试放置水
        hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = hitResult.getBlockPos();
            Direction direction = hitResult.getDirection();
            BlockPos adjacentPos = blockPos.relative(direction);

            // 检查交互权限
            if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(adjacentPos, direction, itemStack)) {
                return InteractionResultHolder.fail(itemStack);
            }
            
            // 尝试放置水源
            if (tryPlaceWater(level, adjacentPos)) {
                level.playSound(player, adjacentPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResultHolder.success(itemStack);
            }
        }

        return InteractionResultHolder.fail(itemStack);
    }
    
    /**
     * 尝试在指定位置放置水
     * 优化：移除player参数，简化逻辑判断
     */
    private boolean tryPlaceWater(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return true; // 客户端直接返回成功，减少计算
        }
        
        BlockState blockState = level.getBlockState(pos);
        // 判断是否可以放置水
        if (blockState.canBeReplaced() || (blockState.getBlock() instanceof LiquidBlock && blockState.getFluidState().is(FluidTags.WATER))) {
            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            return true;
        }
        return false;
    }

    /**
     * 收集指定位置周围3x3范围内的所有水源
     * 优化：返回收集的水源数量，优化循环结构，避免重复获取方块状态
     * @return 成功收集的水源数量
     */
    private int collectWaterInArea(Level level, BlockPos center) {
        if (level.isClientSide) {
            return 0; // 客户端不执行实际操作
        }

        int collectedCount = 0;
        // 遍历3x3x3的范围
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = -COLLECT_RANGE; x <= COLLECT_RANGE; x++) {
            for (int y = -COLLECT_RANGE; y <= COLLECT_RANGE; y++) {
                for (int z = -COLLECT_RANGE; z <= COLLECT_RANGE; z++) {
                    mutablePos.set(center.getX() + x, center.getY() + y, center.getZ() + z);

                    // 使用可变方块位置，减少对象创建
                    BlockState state = level.getBlockState(mutablePos);
                    FluidState fluidState = state.getFluidState();

                    // 优化检查逻辑：直接检查流体状态是否为水源
                    if (fluidState.is(FluidTags.WATER) && fluidState.isSource()) {
                        // 移除水源
                        level.setBlockAndUpdate(new BlockPos(mutablePos), Blocks.AIR.defaultBlockState());
                        collectedCount++;
                    }
                }
            }
        }

        return collectedCount;
    }

    /**
     * 添加物品工具提示
     */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // 添加物品描述
        tooltip.add(Component.translatable("item.curiosities.infinite_water_bucket.desc")
                .withStyle(ChatFormatting.GRAY));

        // 如果物品被禁用，添加禁用提示
        if (!ModConfigManager.INFINITE_WATER_BUCKET_ENABLED.get()) {
            tooltip.add(Component.translatable("item.curiosities.disabled")
                    .withStyle(ChatFormatting.RED));
        }
    }
} 